package com.witboost.provisioning.model.request;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.model.DataProduct;
import com.witboost.provisioning.model.OutputPort;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProvisionOperationRequestTest {

    @Test
    void testOptionalConstructors() {
        var provisionOptionalComponent =
                new ProvisionOperationRequest<>(new DataProduct<>(), true, JsonNodeFactory.instance.objectNode());
        Assertions.assertTrue(provisionOptionalComponent.getComponent().isEmpty());
        Assertions.assertTrue(
                provisionOptionalComponent.getLatestEnrichedDescriptor().isPresent());

        var provisionOptionalEnrichedDescriptor = new ProvisionOperationRequest<>(
                new DataProduct<>(), true, Optional.of(JsonNodeFactory.instance.objectNode()));
        Assertions.assertTrue(provisionOptionalEnrichedDescriptor.getComponent().isEmpty());
        Assertions.assertTrue(provisionOptionalEnrichedDescriptor
                .getLatestEnrichedDescriptor()
                .isPresent());

        var provisionOptionalBothComponentDescriptor = new ProvisionOperationRequest<>(
                new DataProduct<>(), true, Optional.of(JsonNodeFactory.instance.objectNode()));
        Assertions.assertTrue(
                provisionOptionalBothComponentDescriptor.getComponent().isEmpty());
        Assertions.assertTrue(provisionOptionalBothComponentDescriptor
                .getLatestEnrichedDescriptor()
                .isPresent());

        var provisionAllPresent = new ProvisionOperationRequest<>(
                new DataProduct<>(), new OutputPort<>(), true, JsonNodeFactory.instance.objectNode());
        Assertions.assertTrue(provisionAllPresent.getComponent().isPresent());
        Assertions.assertTrue(provisionAllPresent.getLatestEnrichedDescriptor().isPresent());
    }
}
