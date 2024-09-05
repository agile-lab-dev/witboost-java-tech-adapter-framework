package com.witboost.provisioning.model.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProblemTest {

    @Test
    void getMessageNoThrowableCause() {
        var problem = new Problem("Description");

        Assertions.assertEquals("Description", problem.getMessage());
    }

    @Test
    void getMessageWithThrowableCause() {
        var problem = new Problem("Description", new Exception("Exception message description"));

        Assertions.assertEquals("Description: Exception message description", problem.getMessage());
    }

    @Test
    void fromConstraintViolation() {
        var constrationViolation = TestFixtures.buildConstraintViolation("is not valid", "path.to.field");

        var problem = Problem.fromConstraintViolation(constrationViolation);

        Assertions.assertEquals("path.to.field is not valid", problem.description());
    }
}
