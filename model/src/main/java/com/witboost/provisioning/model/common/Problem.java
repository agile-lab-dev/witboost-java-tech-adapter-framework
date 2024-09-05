package com.witboost.provisioning.model.common;

import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Class representing a specific failure instance on the operation flow of the framework. Intended to store the technical details of the error
 * @param description Complete description on the error, including technical information, bodies, etc. that may be useful to understand the cause of the error
 * @param cause Optional throwable representing a caught exception that caused the failure
 * @param solutions A set of solution hints intended to be shown to the end user in order to address the issue, or to steer them into the appropriate support.
 */
public record Problem(String description, Optional<Throwable> cause, Set<String> solutions) {

    public Problem {
        Objects.requireNonNull(description);
        Objects.requireNonNull(cause);
        Objects.requireNonNull(solutions);
    }

    public String getMessage() {
        if (cause.isPresent())
            return String.format("%s: %s", description, cause.get().getMessage());
        else return description;
    }

    public Problem(String description) {
        this(description, Optional.empty(), new HashSet<>());
    }

    public Problem(String description, Throwable cause) {
        this(description, Optional.of(cause), new HashSet<>());
    }

    public Problem(String description, Set<String> solutions) {
        this(description, Optional.empty(), solutions);
    }

    public static Problem fromConstraintViolation(ConstraintViolation<?> constraintViolation) {
        return new Problem(String.format(
                "%s %s", constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
    }
}
