package com.witboost.provisioning.model.request;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.DataProduct;
import com.witboost.provisioning.model.OutputPort;
import com.witboost.provisioning.model.common.Constants;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OperationRequestTest {

    @Test
    void getComponentKindToProvisionReturnsOK() {

        var operationRequest = new OperationRequest<>(new DataProduct<>(), Optional.of(new OutputPort<>()));

        var actualResult = operationRequest.getComponentKindToProvision();

        var expected = Constants.OUTPUTPORT_KIND;

        assertTrue(actualResult.isRight());
        assertEquals(expected, actualResult.get());
    }

    @Test
    void getComponentKindToProvisionReturnsErrorOnMissingComponent() {
        var operationRequest = new OperationRequest<>(new DataProduct<>(), Optional.empty());

        var actualResult = operationRequest.getComponentKindToProvision();
        var expectedMessage =
                "Error while processing the input descriptor. Component to provision is empty or malformed";
        var expectedInputErrorField = "dataProduct.components";

        assertTrue(actualResult.isLeft());
        assertEquals(expectedMessage, actualResult.getLeft().message());
        assertTrue(actualResult.getLeft().inputErrorField().isPresent());
        assertEquals(
                expectedInputErrorField,
                actualResult.getLeft().inputErrorField().get());
    }

    @Test
    void getComponentKindToProvisionReturnsErrorOnNullKind() {

        class MyWrongComponent extends Component<JsonNode> {}

        var component = new MyWrongComponent();
        component.setId("id");

        var operationRequest = new OperationRequest<>(new DataProduct<>(), Optional.of(component));

        var actualResult = operationRequest.getComponentKindToProvision();
        var expectedMessage =
                "Error while processing the input descriptor. Component to provision is empty or malformed";
        var expectedInputErrorField = "dataProduct.components.[(@.id == 'id')].kind";

        assertTrue(actualResult.isLeft());
        assertEquals(expectedMessage, actualResult.getLeft().message());
        assertTrue(actualResult.getLeft().inputErrorField().isPresent());
        assertEquals(
                expectedInputErrorField,
                actualResult.getLeft().inputErrorField().get());
    }
}
