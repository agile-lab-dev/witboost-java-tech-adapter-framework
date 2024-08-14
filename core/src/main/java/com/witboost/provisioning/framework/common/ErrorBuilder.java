package com.witboost.provisioning.framework.common;

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

public class ErrorBuilder {

    public static RequestValidationError buildRequestValidationError(
            Optional<String> message,
            FailedOperation failedOperation,
            Optional<String> input,
            Optional<String> inputErrorField) {

        List<String> problems =
                failedOperation.problems().stream().map(Problem::getMessage).collect(Collectors.toList());

        ArrayList<String> solutions = new ArrayList<>(failedOperation.problems().stream()
                .flatMap(p -> p.solutions().stream())
                .toList());
        solutions.add("If the problem persists, contact the platform team");

        return new RequestValidationError(problems)
                .userMessage(message.orElse(
                        "Validation on the received descriptor failed, check the error details for more information"))
                .input(input.orElse(null))
                .inputErrorField(inputErrorField.orElse(null))
                .moreInfo(new ErrorMoreInfo(problems, solutions));
    }

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

    public static SystemError buildSystemError(Optional<String> message, Throwable throwable) {

        List<String> problems = List.of(throwable.getMessage());

        List<String> solutions = List.of("Please try again and if the problem persists contact the platform team.");

        return new SystemError(throwable.getMessage())
                .userMessage(
                        message.orElse(
                                "An unexpected error occurred while processing the request. Check the error details for more information"))
                .moreInfo(new ErrorMoreInfo(problems, solutions));
    }
}
