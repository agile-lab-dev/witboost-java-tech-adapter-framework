package com.witboost.provisioning.framework.common;

import static com.witboost.provisioning.framework.common.ErrorConstants.PLATFORM_TEAM_SOLUTION;
import static com.witboost.provisioning.framework.common.TestFixtures.buildConstraintViolation;
import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ErrorBuilderTest {

    @Test
    void buildRequestValidationErrorCustomMessage() {
        FailedOperation failedOperation = new FailedOperation(
                "Error! See info",
                List.of(
                        new Problem("Error1 - No cause"),
                        new Problem("Error2 - cause", new Exception("Cause message")),
                        new Problem("Error3 - solutions", Set.of("Try again"))));

        var error = ErrorBuilder.buildRequestValidationError(failedOperation);

        assertTrue(error.getUserMessage().isPresent());
        assertEquals(failedOperation.message(), error.getUserMessage().get());
        assertTrue(error.getMoreInfo().isPresent());
        assertTrue(error.getInputErrorField().isEmpty());
        assertTrue(error.getInput().isEmpty());
        assertEquals(error.getErrors(), error.getMoreInfo().get().getProblems());
        assertEquals(error.getMoreInfo().get().getProblems().size(), 3);
        assertEquals(error.getMoreInfo().get().getSolutions().size(), 2);
        assertTrue(error.getMoreInfo().get().getSolutions().contains("Try again"));
        assertEquals(
                error.getMoreInfo().get().getProblems(),
                List.of("Error1 - No cause", "Error2 - cause: Cause message", "Error3 - solutions"));
    }

    @Test
    void testBuildRequestValidationErrorConstraintViolation() {
        Set<ConstraintViolation<?>> violations = Set.of(
                buildConstraintViolation("is not valid", "path.to.field"),
                buildConstraintViolation("must not be null", "other.field"));
        ConstraintViolationException error = new ConstraintViolationException(violations);

        var requestValidationError = ErrorBuilder.buildRequestValidationError(error);

        var expectedMessage =
                "Validation on the received descriptor failed, check the error details for more information";
        var expectedErrors = Set.of("path.to.field is not valid", "other.field must not be null");

        Assertions.assertEquals(2, requestValidationError.getErrors().size());
        Assertions.assertEquals(expectedErrors, Set.copyOf(requestValidationError.getErrors()));
        assertTrue(requestValidationError.getMoreInfo().isPresent());
        Assertions.assertEquals(
                requestValidationError.getErrors(),
                requestValidationError.getMoreInfo().get().getProblems());
        Assertions.assertEquals(Optional.of(expectedMessage), requestValidationError.getUserMessage());
    }

    @Test
    void testBuildRequestValidationErrorConstraintViolationSingleField() {
        Set<ConstraintViolation<?>> violations = Set.of(buildConstraintViolation("is not valid", "path.to.field"));
        ConstraintViolationException error = new ConstraintViolationException(violations);

        var requestValidationError = ErrorBuilder.buildRequestValidationError(error);

        var expectedMessage =
                "Validation on the received descriptor failed, check the error details for more information";
        var expectedErrors = Set.of("path.to.field is not valid");

        Assertions.assertEquals(1, requestValidationError.getErrors().size());
        Assertions.assertTrue(requestValidationError.getInputErrorField().isPresent());
        Assertions.assertEquals(
                "path.to.field", requestValidationError.getInputErrorField().get());
        Assertions.assertEquals(expectedErrors, Set.copyOf(requestValidationError.getErrors()));
        assertTrue(requestValidationError.getMoreInfo().isPresent());
        Assertions.assertEquals(
                requestValidationError.getErrors(),
                requestValidationError.getMoreInfo().get().getProblems());
        Assertions.assertEquals(Optional.of(expectedMessage), requestValidationError.getUserMessage());
    }

    @Test
    void buildSystemErrorDefaultMessage() {
        var exception = new Exception("System error!");

        var actual = ErrorBuilder.buildSystemError(Optional.empty(), exception);

        assertEquals(
                Optional.of(
                        "An unexpected error occurred while processing the request. Check the error details for more information"),
                actual.getUserMessage());
        assertTrue(actual.getMoreInfo().isPresent());
        assertTrue(actual.getMoreInfo().get().getProblems().contains(actual.getError()));
        assertTrue(actual.getMoreInfo().get().getProblems().contains(exception.getMessage()));
        assertEquals(actual.getMoreInfo().get().getSolutions(), List.of(PLATFORM_TEAM_SOLUTION));
    }

    @Test
    void buildSystemErrorCustomMessage() {
        var exception = new Exception("System error!");
        var message = "Error while system erroring";

        var actual = ErrorBuilder.buildSystemError(Optional.of(message), exception);

        assertEquals(Optional.of(message), actual.getUserMessage());
        assertTrue(actual.getMoreInfo().isPresent());
        assertTrue(actual.getMoreInfo().get().getProblems().contains(actual.getError()));
        assertTrue(actual.getMoreInfo().get().getProblems().contains(exception.getMessage()));
        assertEquals(actual.getMoreInfo().get().getSolutions(), List.of(PLATFORM_TEAM_SOLUTION));
    }
}
