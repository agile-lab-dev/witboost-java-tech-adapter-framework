package com.witboost.provisioning.model.status;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ValidationInfo(Boolean isValid, List<String> errors) {

    public static ValidationInfo valid() {
        return new ValidationInfo(true, List.of());
    }

    public static ValidationInfo invalid(@NotEmpty List<String> errors) {
        return new ValidationInfo(false, errors);
    }
}
