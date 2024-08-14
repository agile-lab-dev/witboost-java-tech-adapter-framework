package com.witboost.provisioning.framework.common;

import com.witboost.provisioning.model.common.FailedOperation;
import java.util.Optional;

public class SpecificProvisionerValidationException extends RuntimeException {

    private final FailedOperation failedOperation;

    private final Optional<String> input;
    private final Optional<String> inputErrorField;

    public SpecificProvisionerValidationException(String message, FailedOperation failedOperation) {
        this(message, failedOperation, null, null);
    }

    public SpecificProvisionerValidationException(
            String message, FailedOperation failedOperation, String input, String inputErrorField) {
        super(message);
        this.failedOperation = failedOperation;
        this.input = Optional.ofNullable(input);
        this.inputErrorField = Optional.ofNullable(inputErrorField);
    }

    public FailedOperation getFailedOperation() {
        return failedOperation;
    }

    public Optional<String> getInput() {
        return input;
    }

    public Optional<String> getInputErrorField() {
        return inputErrorField;
    }
}
