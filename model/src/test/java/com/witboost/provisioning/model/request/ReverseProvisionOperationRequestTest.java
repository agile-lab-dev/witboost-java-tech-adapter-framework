package com.witboost.provisioning.model.request;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.model.common.Constants;
import com.witboost.provisioning.parser.Parser;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReverseProvisionOperationRequestTest {

    @Test
    void getComponentKindOk() {

        var expected = Constants.OUTPUTPORT_KIND;
        var reverseProvisionRequest = new ReverseProvisionOperationRequest<>(
                "useCaseTemplateId",
                "environment",
                JsonNodeFactory.instance.objectNode(),
                Parser.objectToJsonNode(Map.of("spec", Map.of("mesh", Map.of("kind", expected))))
                        .get());

        var actual = reverseProvisionRequest.getComponentKind();

        Assertions.assertTrue(actual.isRight());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void getComponentKindMissingPiece() {

        var expected = Constants.OUTPUTPORT_KIND;
        var reverseProvisionRequest = new ReverseProvisionOperationRequest<>(
                "useCaseTemplateId",
                "environment",
                JsonNodeFactory.instance.objectNode(),
                Parser.objectToJsonNode(Map.of("mesh", Map.of("kind", expected)))
                        .get());

        var actual = reverseProvisionRequest.getComponentKind();
        var expectedMessage = "Error while extracting the component kind from the Reverse Provision input catalog info";
        var expectedInputErrorField = "spec.mesh.kind";

        Assertions.assertTrue(actual.isLeft());
        Assertions.assertEquals(expectedMessage, actual.getLeft().message());
        Assertions.assertTrue(actual.getLeft().inputErrorField().isPresent());
        Assertions.assertEquals(
                expectedInputErrorField, actual.getLeft().inputErrorField().get());
    }
}
