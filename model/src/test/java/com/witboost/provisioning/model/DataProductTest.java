package com.witboost.provisioning.model;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.parser.Parser;
import com.witboost.provisioning.util.ResourceUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataProductTest {
    String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage.yml");
    ComponentDescriptor<JsonNode> componentDescriptor =
            Parser.parseComponentDescriptor(ymlDescriptor).get();

    DataProductTest() throws IOException {}

    @Test
    void getComponentToProvisionPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult = dataProduct.getComponentToProvision("urn:dmb:cmp:healthcare:vaccinations:0:storage");

        Assertions.assertTrue(actualResult.isDefined());
        Assertions.assertEquals(
                actualResult.get().path("id").textValue(), "urn:dmb:cmp:healthcare:vaccinations:0:storage");
    }

    @Test
    void getComponentToProvisionNotPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult = dataProduct.getComponentToProvision("inexistent");

        Assertions.assertTrue(actualResult.isEmpty());
    }

    @Test
    void getComponentKindToProvisionPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult = dataProduct.getComponentKindToProvision("urn:dmb:cmp:healthcare:vaccinations:0:storage");

        Assertions.assertTrue(actualResult.isRight());
        Assertions.assertEquals(actualResult.get(), "storage");
    }

    @Test
    void getComponentKindToProvisionComponentNotPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult = dataProduct.getComponentKindToProvision("inexistent");

        Assertions.assertTrue(actualResult.isLeft());
        Assertions.assertEquals(
                actualResult.getLeft().message(),
                "Error while processing the input descriptor. Component to provision is empty or malformed");
        Assertions.assertTrue(actualResult.getLeft().inputErrorField().isPresent());
        Assertions.assertEquals(actualResult.getLeft().inputErrorField().get(), "dataProduct.components");
        actualResult.getLeft().problems().forEach(p -> {
            Assertions.assertTrue(
                    p.description().startsWith("Parsing of descriptor resulted in a request with an empty component:"));
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    void getComponentKindToProvisionKindNotPresent() throws IOException {
        String ymlDescriptorMissingKind =
                ResourceUtils.getContentFromResource("/pr_descriptor_storage_missing_componentKind.yml");
        ComponentDescriptor<JsonNode> componentDescriptorMissingKind =
                Parser.parseComponentDescriptor(ymlDescriptorMissingKind).get();

        var dataProduct = componentDescriptorMissingKind.getDataProduct();

        var expectedError = new FailedOperation(
                "Error while processing the input descriptor. Component to provision is empty or malformed",
                Optional.empty(),
                Optional.of("dataProduct.components.urn:dmb:cmp:healthcare:vaccinations:0:storage.kind"),
                Collections.singletonList(
                        new Problem(
                                "Couldn't retrieve 'kind' field for the component with ID 'urn:dmb:cmp:healthcare:vaccinations:0:storage'")));

        var actualResult = dataProduct.getComponentKindToProvision("urn:dmb:cmp:healthcare:vaccinations:0:storage");

        Assertions.assertTrue(actualResult.isLeft());
        Assertions.assertEquals(actualResult.getLeft(), expectedError);
    }

    @Test
    void getComponentUseCaseTemplateIdToProvision() {}

    @Test
    void getComponentUseCaseTemplateIdToProvisionPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult =
                dataProduct.getComponentUseCaseTemplateIdToProvision("urn:dmb:cmp:healthcare:vaccinations:0:storage");

        Assertions.assertTrue(actualResult.isDefined());
        Assertions.assertEquals(actualResult.get(), "urn:dmb:utm:cdp-private-hdfs-storage-template:0.0.0");
    }

    @Test
    void getComponentUseCaseTemplateIdToProvisionComponentNotPresent() {
        var dataProduct = componentDescriptor.getDataProduct();

        var actualResult = dataProduct.getComponentUseCaseTemplateIdToProvision("inexistent");

        Assertions.assertTrue(actualResult.isEmpty());
    }

    @Test
    void getComponentUseCaseTemplateIdToProvisionUseCaseTemplateIdNotPresent() throws IOException {
        String ymlDescriptorMissingUCID =
                ResourceUtils.getContentFromResource("/pr_descriptor_storage_missing_useCaseTemplateId.yml");
        ComponentDescriptor<JsonNode> componentDescriptorMissingUCID =
                Parser.parseComponentDescriptor(ymlDescriptorMissingUCID).get();

        var dataProduct = componentDescriptorMissingUCID.getDataProduct();

        var actualResult =
                dataProduct.getComponentUseCaseTemplateIdToProvision("urn:dmb:cmp:healthcare:vaccinations:0:storage");

        Assertions.assertTrue(actualResult.isEmpty());
    }
}
