package com.witboost.provisioning.framework.controller;

import com.witboost.provisioning.framework.common.ErrorBuilder;
import com.witboost.provisioning.framework.common.SpecificProvisionerValidationException;
import com.witboost.provisioning.framework.openapi.model.RequestValidationError;
import com.witboost.provisioning.framework.openapi.model.SystemError;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for the API layer.
 *
 * <p>The following methods wrap generic exceptions into 400 and 500 errors. Implement your own
 * exception handlers based on the business exception that the provisioner throws. No further
 * modifications need to be done outside this file to make it work, as Spring identifies at startup
 * the handlers with the @ExceptionHandler annotation
 */
@RestControllerAdvice
public class SpecificProvisionerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(SpecificProvisionerExceptionHandler.class);

    @ExceptionHandler({SpecificProvisionerValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected RequestValidationError handleValidationException(SpecificProvisionerValidationException ex) {
        logger.error("Specific provisioner validation error", ex);
        return ErrorBuilder.buildRequestValidationError(
                Optional.ofNullable(ex.getMessage()), ex.getFailedOperation(), ex.getInput(), ex.getInputErrorField());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected RequestValidationError handleConflict(ConstraintViolationException ex) {
        logger.error("Constraint violation Error", ex);
        return ErrorBuilder.buildRequestValidationError(ex);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected SystemError handleSystemError(RuntimeException ex) {
        logger.error("Runtime error", ex);
        return ErrorBuilder.buildSystemError(Optional.empty(), ex);
    }
}
