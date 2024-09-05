package com.witboost.provisioning.framework.controller;

import com.witboost.provisioning.framework.common.ErrorBuilder;
import com.witboost.provisioning.framework.common.TechAdapterValidationException;
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
 * Exception handler for the API layer. The following methods wrap both custom and generic exceptions into 400 and 500 errors.
 */
@RestControllerAdvice
class ProvisioningExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ProvisioningExceptionHandler.class);

    @ExceptionHandler({TechAdapterValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected RequestValidationError handleValidationException(TechAdapterValidationException ex) {
        logger.error("Caught Tech Adapter Validation exception", ex);
        return ErrorBuilder.buildRequestValidationError(ex.getFailedOperation());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected RequestValidationError handleConflict(ConstraintViolationException ex) {
        logger.error("Caught ConstraintViolationException", ex);
        return ErrorBuilder.buildRequestValidationError(ex);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected SystemError handleSystemError(RuntimeException ex) {
        logger.error("Caught Runtime exception", ex);
        return ErrorBuilder.buildSystemError(Optional.empty(), ex);
    }
}
