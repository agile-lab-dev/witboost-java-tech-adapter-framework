package com.witboost.provisioning.model.common;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class representing an error on the operation flow of the framework. These are intended to be translated into 400 Bad Request errors by the API layer,
 * as they stand for controlled failures in a pure-functional flow.
 * @param message User-friendly message explaining the error at high level. This message is intended to be shown to the end user, so it should not contain technical details
 * @param input Optional input that caused the error, usually a descriptor
 * @param inputErrorField Optional field to include the field path (e.g. {@code specific.name}) that raised the error
 * @param problems List of problems storing the error description, cause and possible solution hints. Used to accumulate several errors onto a single {@link FailedOperation}
 */
public record FailedOperation(
        String message, Optional<String> input, Optional<String> inputErrorField, List<Problem> problems) {

    public FailedOperation {
        Objects.requireNonNull(message);
        Objects.requireNonNull(problems);
    }

    public FailedOperation(String message, List<Problem> problems) {
        this(message, Optional.empty(), Optional.empty(), problems);
    }
}
