package com.witboost.provisioning.model.status;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationInfoTest {

    @Test
    void validShouldReturnIsValidTrue() {
        var result = ValidationInfo.valid();

        Assertions.assertTrue(result.isValid());
        Assertions.assertTrue(result.errors().isEmpty());
    }

    @Test
    void invalidShouldReturnIsValidFalse() {
        var result = ValidationInfo.invalid(List.of("Error while validating"));

        Assertions.assertFalse(result.isValid());
        Assertions.assertFalse(result.errors().isEmpty());
    }
}
