package com.witboost.provisioning.framework.common;

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
    void buildRequestValidationErrorDefaultMessage() {
        FailedOperation failedOperation = new FailedOperation(List.of(
                new Problem("Error1 - No cause"),
                new Problem("Error2 - cause", new Exception("Cause message")),
                new Problem("Error3 - solutions", Optional.empty(), Set.of("Try again"))));

        var error = ErrorBuilder.buildRequestValidationError(
                Optional.empty(), failedOperation, Optional.empty(), Optional.empty());
        assertEquals(
                error.getUserMessage(),
                "Validation on the received descriptor failed, check the error details for more information");
        assertNotNull(error.getMoreInfo());
        assertNull(error.getInputErrorField());
        assertNull(error.getInput());
        assertEquals(error.getErrors(), error.getMoreInfo().getProblems());
        assertEquals(error.getMoreInfo().getProblems().size(), 3);
        assertEquals(error.getMoreInfo().getSolutions().size(), 2);
        assertTrue(error.getMoreInfo().getSolutions().contains("Try again"));
        assertEquals(
                error.getMoreInfo().getProblems(),
                List.of("Error1 - No cause", "Error2 - cause: Cause message", "Error3 - solutions"));
    }

    @Test
    void buildRequestValidationErrorCustomMessage() {
        FailedOperation failedOperation = new FailedOperation(List.of(
                new Problem("Error1 - No cause"),
                new Problem("Error2 - cause", new Exception("Cause message")),
                new Problem("Error3 - solutions", Optional.empty(), Set.of("Try again"))));

        var customMessage = "Error! See info";

        var error = ErrorBuilder.buildRequestValidationError(
                Optional.of(customMessage), failedOperation, Optional.empty(), Optional.empty());
        assertEquals(customMessage, error.getUserMessage());
        assertNotNull(error.getMoreInfo());
        assertNull(error.getInputErrorField());
        assertNull(error.getInput());
        assertEquals(error.getErrors(), error.getMoreInfo().getProblems());
        assertEquals(error.getMoreInfo().getProblems().size(), 3);
        assertEquals(error.getMoreInfo().getSolutions().size(), 2);
        assertTrue(error.getMoreInfo().getSolutions().contains("Try again"));
        assertEquals(
                error.getMoreInfo().getProblems(),
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
        Assertions.assertEquals(
                requestValidationError.getErrors(),
                requestValidationError.getMoreInfo().getProblems());
        Assertions.assertEquals(expectedMessage, requestValidationError.getUserMessage());
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
        Assertions.assertEquals("path.to.field", requestValidationError.getInputErrorField());
        Assertions.assertEquals(expectedErrors, Set.copyOf(requestValidationError.getErrors()));
        Assertions.assertEquals(
                requestValidationError.getErrors(),
                requestValidationError.getMoreInfo().getProblems());
        Assertions.assertEquals(expectedMessage, requestValidationError.getUserMessage());
    }

    @Test
    void buildSystemErrorDefaultMessage() {
        var exception = new Exception("System error!");

        var actual = ErrorBuilder.buildSystemError(Optional.empty(), exception);

        assertEquals(
                "An unexpected error occurred while processing the request. Check the error details for more information",
                actual.getUserMessage());
        assertNotNull(actual.getMoreInfo());
        assertTrue(actual.getMoreInfo().getProblems().contains(actual.getError()));
        assertTrue(actual.getMoreInfo().getProblems().contains(exception.getMessage()));
        assertEquals(
                actual.getMoreInfo().getSolutions(),
                List.of("Please try again and if the problem persists contact the platform team."));
    }

    @Test
    void buildSystemErrorCustomMessage() {
        var exception = new Exception("System error!");
        var message = "Error while system erroring";

        var actual = ErrorBuilder.buildSystemError(Optional.of(message), exception);

        assertEquals(message, actual.getUserMessage());
        assertNotNull(actual.getMoreInfo());
        assertTrue(actual.getMoreInfo().getProblems().contains(actual.getError()));
        assertTrue(actual.getMoreInfo().getProblems().contains(exception.getMessage()));
        assertEquals(
                actual.getMoreInfo().getSolutions(),
                List.of("Please try again and if the problem persists contact the platform team."));
    }
}
