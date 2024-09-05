package com.witboost.provisioning.framework.common;

import static com.witboost.provisioning.framework.common.ErrorConstants.PLATFORM_TEAM_SOLUTION;

import com.witboost.provisioning.framework.openapi.model.ErrorMoreInfo;
import com.witboost.provisioning.framework.openapi.model.RequestValidationError;
import com.witboost.provisioning.framework.openapi.model.SystemError;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Static class which provides methods to build API model response errors from the framework objects and exceptions.
 * <p>
 * These methods are transparent to the Tech Adapter developer, as these are only used on the Framework API layer
 */
public class ErrorBuilder {

    /**
     * Builds a {@link RequestValidationError} based on a {@link FailedOperation} instance. It stores the user message and the
     * set of problems and solutions on the response error, as well as any extra information like the input and/or input field
     * which caused the error. It also adds the default solution stored in the {@link ErrorConstants} if not already contained on the FailedOperation.
     * @param failedOperation Framework error model containing the information about the failure.
     * @return {@link RequestValidationError} API object with the stored information, which will then be returned as an HTTP 4xx error.
     */
    public static RequestValidationError buildRequestValidationError(FailedOperation failedOperation) {

        List<String> problems =
                failedOperation.problems().stream().map(Problem::getMessage).collect(Collectors.toList());

        ArrayList<String> solutions = new ArrayList<>(failedOperation.problems().stream()
                .flatMap(p -> p.solutions().stream())
                .toList());
        if (!solutions.contains(PLATFORM_TEAM_SOLUTION)) solutions.add(PLATFORM_TEAM_SOLUTION);

        var error = new RequestValidationError(problems).userMessage(failedOperation.message());
        error.setInput(failedOperation.input());
        error.setInputErrorField(failedOperation.inputErrorField());
        return error.moreInfo(new ErrorMoreInfo(problems, solutions));
    }

    /**
     * Builds a {@link RequestValidationError} based on a Jakarta {@link ConstraintViolationException} instance. It stores the user message and the
     * set of problems based on the constraint violations stored on the exception, as well as any extra information like the input and/or input field
     * which caused the error. It also adds a default solution message to the user.
     * @param validationException Jakarta {@link ConstraintViolationException} thrown by {@code @Valid} annotations.
     * @return {@link RequestValidationError} API object with the stored information, which will then be returned as an HTTP 4xx error.
     */
    public static RequestValidationError buildRequestValidationError(ConstraintViolationException validationException) {

        var problems = validationException.getConstraintViolations().stream()
                .map(Problem::fromConstraintViolation)
                .map(Problem::description)
                .toList();

        var error = new RequestValidationError(problems)
                .userMessage(
                        "Validation on the received descriptor failed, check the error details for more information");

        if (validationException.getConstraintViolations().size() == 1) {
            error = error.inputErrorField(validationException.getConstraintViolations().stream()
                    .map(cv -> cv.getPropertyPath().toString())
                    .findFirst()
                    .get());
        }

        return error.moreInfo(new ErrorMoreInfo(
                problems,
                List.of(
                        "Check the input descriptor is compliant with the schema expected by this Data Catalog Plugin and try again",
                        "If the problem persists, contact the platform team")));
    }

    /**
     * Builds a {@link SystemError} based on a {@link Throwable} instance and an optional user message. It stores the set of problems,
     * as well as adding the default solution stored in the {@link ErrorConstants}.
     * @param message Optional custom user message. This will be shown to the final user, so it should be written in a user-friendly manner
     * @param throwable {@link Throwable} instance which contains the cause of the error
     * @return {@link SystemError} API object with the stored information, which will then be returned as an HTTP 5xx error.
     */
    public static SystemError buildSystemError(Optional<String> message, Throwable throwable) {

        List<String> problems = List.of(throwable.getMessage());

        List<String> solutions = List.of(PLATFORM_TEAM_SOLUTION);

        return new SystemError(throwable.getMessage())
                .userMessage(
                        message.orElse(
                                "An unexpected error occurred while processing the request. Check the error details for more information"))
                .moreInfo(new ErrorMoreInfo(problems, solutions));
    }
}
