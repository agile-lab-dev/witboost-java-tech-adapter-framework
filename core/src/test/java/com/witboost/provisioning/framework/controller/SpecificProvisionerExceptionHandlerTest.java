package com.witboost.provisioning.framework.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.framework.common.SpecificProvisionerValidationException;
import com.witboost.provisioning.framework.common.TestFixtures;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SpecificProvisionerExceptionHandler.class)
public class SpecificProvisionerExceptionHandlerTest {

    @InjectMocks
    SpecificProvisionerExceptionHandler specificProvisionerExceptionHandler;

    @Test
    void testHandleConflictRequestValidationError() {
        FailedOperation failedOperation = new FailedOperation(List.of(
                new Problem("Error1 - No cause"),
                new Problem("Error2 - cause", new Exception("Cause message")),
                new Problem("Error3 - solutions", Optional.empty(), Set.of("Try again"))));

        var customMessage = "Error! See info";

        var error = specificProvisionerExceptionHandler.handleValidationException(
                new SpecificProvisionerValidationException(customMessage, failedOperation));

        assertEquals(error.getUserMessage(), customMessage);
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
    void testHandleConflictConstraintViolationException() {
        Set<ConstraintViolation<?>> violations =
                Set.of(TestFixtures.buildConstraintViolation("is not valid", "path.to.field"));
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        var error = specificProvisionerExceptionHandler.handleConflict(ex);

        var expectedMessage =
                "Validation on the received descriptor failed, check the error details for more information";
        var expectedErrors = Set.of("path.to.field is not valid");

        Assertions.assertEquals(1, error.getErrors().size());
        Assertions.assertEquals("path.to.field", error.getInputErrorField());
        Assertions.assertEquals(expectedErrors, Set.copyOf(error.getErrors()));
        Assertions.assertEquals(error.getErrors(), error.getMoreInfo().getProblems());
        Assertions.assertEquals(expectedMessage, error.getUserMessage());
    }

    @Test
    void testHandleConflict() {
        var exception = new RuntimeException("System error!");

        var actual = specificProvisionerExceptionHandler.handleSystemError(exception);

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
}
