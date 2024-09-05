package com.witboost.provisioning.framework.controller;

import static com.witboost.provisioning.framework.common.ErrorConstants.PLATFORM_TEAM_SOLUTION;
import static com.witboost.provisioning.framework.common.TestFixtures.buildConstraintViolation;
import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.framework.common.TechAdapterValidationException;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProvisioningExceptionHandlerTest {

    @InjectMocks
    ProvisioningExceptionHandler specificProvisionerExceptionHandler;

    @Test
    void testHandleConflictRequestValidationError() {
        var customMessage = "Error! See info";
        FailedOperation failedOperation = new FailedOperation(
                customMessage,
                List.of(
                        new Problem("Error1 - No cause"),
                        new Problem("Error2 - cause", new Exception("Cause message")),
                        new Problem("Error3 - solutions", Set.of("Try again"))));

        var error = specificProvisionerExceptionHandler.handleValidationException(
                new TechAdapterValidationException(failedOperation));

        assertTrue(error.getUserMessage().isPresent());
        assertEquals(error.getUserMessage().get(), customMessage);
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
    void testHandleConflictConstraintViolationException() {
        Set<ConstraintViolation<?>> violations = Set.of(buildConstraintViolation("is not valid", "path.to.field"));
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        var error = specificProvisionerExceptionHandler.handleConflict(ex);

        var expectedMessage =
                "Validation on the received descriptor failed, check the error details for more information";
        var expectedErrors = Set.of("path.to.field is not valid");

        Assertions.assertTrue(error.getMoreInfo().isPresent());
        Assertions.assertEquals(1, error.getErrors().size());
        Assertions.assertTrue(error.getInputErrorField().isPresent());
        Assertions.assertEquals("path.to.field", error.getInputErrorField().get());
        Assertions.assertEquals(expectedErrors, Set.copyOf(error.getErrors()));
        Assertions.assertEquals(error.getErrors(), error.getMoreInfo().get().getProblems());
        Assertions.assertTrue(error.getUserMessage().isPresent());
        Assertions.assertEquals(expectedMessage, error.getUserMessage().get());
    }

    @Test
    void testHandleConflict() {
        var exception = new RuntimeException("System error!");

        var actual = specificProvisionerExceptionHandler.handleSystemError(exception);

        assertTrue(actual.getUserMessage().isPresent());
        assertEquals(
                "An unexpected error occurred while processing the request. Check the error details for more information",
                actual.getUserMessage().get());
        assertTrue(actual.getMoreInfo().isPresent());
        assertTrue(actual.getMoreInfo().get().getProblems().contains(actual.getError()));
        assertTrue(actual.getMoreInfo().get().getProblems().contains(exception.getMessage()));
        assertEquals(actual.getMoreInfo().get().getSolutions(), List.of(PLATFORM_TEAM_SOLUTION));
    }
}
