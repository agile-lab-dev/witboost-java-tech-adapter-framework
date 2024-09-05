package com.witboost.provisioning.framework.common;

import com.witboost.provisioning.model.common.FailedOperation;

/**
 * Custom RuntimeException to throw validation errors which will be handled by a Spring Exception Handler. It contains
 * a {@link FailedOperation} storing the error information.
 *
 * @see java.lang.RuntimeException
 */
public class TechAdapterValidationException extends RuntimeException {

    private final FailedOperation failedOperation;

    public TechAdapterValidationException(FailedOperation failedOperation) {
        super(failedOperation.message());
        this.failedOperation = failedOperation;
    }

    public FailedOperation getFailedOperation() {
        return failedOperation;
    }
}
