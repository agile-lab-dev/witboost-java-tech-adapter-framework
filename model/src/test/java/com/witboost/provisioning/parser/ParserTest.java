package com.witboost.provisioning.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.util.ResourceUtils;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Class used to test parsing of custom classes
 */
@Setter
@EqualsAndHashCode
class CustomClass {
    private int id;
    private String value;
}

public class ParserTest {

    @Test
    void testParseStorageDescriptorOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage.yml");

        var actualResult = Parser.parseComponentDescriptor(ymlDescriptor);

        Assertions.assertTrue(actualResult.isRight());
    }

    @Test
    void testParseOutputPortDescriptorOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_outputport.yml");

        var actualResult = Parser.parseComponentDescriptor(ymlDescriptor);

        Assertions.assertTrue(actualResult.isRight());
    }

    @Test
    void testParseWorkloadDescriptorOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_workload.yml");

        var actualResult = Parser.parseComponentDescriptor(ymlDescriptor);

        Assertions.assertTrue(actualResult.isRight());
    }

    @Test
    public void testParseStorageDescriptorFail() {
        String invalidDescriptor = "an_invalid_descriptor";
        String expectedDesc = "Failed to deserialize the Yaml Descriptor. Details: ";

        var actualRes = Parser.parseComponentDescriptor(invalidDescriptor);

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertTrue(p.description().startsWith(expectedDesc));
            Assertions.assertTrue(p.cause().isPresent());
        });
    }

    @Test
    public void testParseStorageComponentOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage.yml");
        var eitherDescriptor = Parser.parseComponentDescriptor(ymlDescriptor);
        Assertions.assertTrue(eitherDescriptor.isRight());
        ComponentDescriptor<JsonNode> descriptor = eitherDescriptor.get();
        String componentIdToProvision = "urn:dmb:cmp:healthcare:vaccinations:0:storage";
        var optionalComponent = descriptor.getDataProduct().getComponentToProvision(componentIdToProvision);
        Assertions.assertTrue(optionalComponent.isDefined());
        JsonNode component = optionalComponent.get();

        var actualRes = Parser.parseComponent(component, StorageArea.class, Specific.class);
        Assertions.assertTrue(actualRes.isRight());
    }

    @Test
    public void testParseOutputPortComponentOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_outputport.yml");
        var eitherDescriptor = Parser.parseComponentDescriptor(ymlDescriptor);
        Assertions.assertTrue(eitherDescriptor.isRight());
        ComponentDescriptor<JsonNode> descriptor = eitherDescriptor.get();
        String componentIdToProvision = "urn:dmb:cmp:healthcare:vaccinations:0:hdfs-output-port";
        var optionalComponent = descriptor.getDataProduct().getComponentToProvision(componentIdToProvision);
        Assertions.assertTrue(optionalComponent.isDefined());
        JsonNode component = optionalComponent.get();

        var actualRes = Parser.parseComponent(component, OutputPort.class, Specific.class);

        Assertions.assertTrue(actualRes.isRight());
    }

    @Test
    public void testParseWorkloadComponentOk() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_workload.yml");
        var eitherDescriptor = Parser.parseComponentDescriptor(ymlDescriptor);
        Assertions.assertTrue(eitherDescriptor.isRight());
        ComponentDescriptor<JsonNode> descriptor = eitherDescriptor.get();
        String componentIdToProvision = "urn:dmb:cmp:healthcare:dbt-provisioner:0:dbt-transformation-workload";
        var optionalComponent = descriptor.getDataProduct().getComponentToProvision(componentIdToProvision);
        Assertions.assertTrue(optionalComponent.isDefined());
        JsonNode component = optionalComponent.get();

        var actualRes = Parser.parseComponent(component, Workload.class, Specific.class);

        Assertions.assertTrue(actualRes.isRight());
    }

    @Test
    public void testParseStorageComponentFail() {
        JsonNode node = null;
        String expectedDesc = "Failed to deserialize the component. Details: ";

        var actualRes = Parser.parseComponent(node, StorageArea.class, Specific.class);

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertTrue(p.description().startsWith(expectedDesc));
            Assertions.assertTrue(p.cause().isPresent());
        });
    }

    @Test
    public void testParseStringToObject() {
        String aJson = "{\"id\": 123, \"value\": \"my-value\"}";
        String aYaml = "id: 123\nvalue: my-value";

        var expected = new CustomClass();
        expected.setId(123);
        expected.setValue("my-value");

        var actualJson = Parser.parseString(aJson, CustomClass.class);
        var actualYaml = Parser.parseString(aYaml, CustomClass.class);

        Assertions.assertTrue(actualJson.isRight());
        Assertions.assertEquals(expected, actualJson.get());
        Assertions.assertTrue(actualYaml.isRight());
        Assertions.assertEquals(expected, actualYaml.get());
    }

    @Test
    public void testParseStringToObjectFailsMalformed() {
        String aJson = "{\"id\": 123, \"value\": \"my-value";
        String aYaml = "id: 123\nvalue:my-value";

        var actualJson = Parser.parseString(aJson, CustomClass.class);
        var actualYaml = Parser.parseString(aYaml, CustomClass.class);

        var expectedMessage = "Failed deserialize object. See error details for more information.";
        Assertions.assertTrue(actualJson.isLeft());
        Assertions.assertEquals(expectedMessage, actualJson.getLeft().message());

        Assertions.assertTrue(actualYaml.isLeft());
        Assertions.assertEquals(expectedMessage, actualYaml.getLeft().message());
    }

    @Test
    public void testParseJsonNodeToObject() {
        JsonNode aJsonNode =
                JsonNodeFactory.instance.objectNode().put("id", 123).put("value", "my-value");

        var expected = new CustomClass();
        expected.setId(123);
        expected.setValue("my-value");

        var actual = Parser.parseObject(aJsonNode, CustomClass.class);

        Assertions.assertTrue(actual.isRight());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    public void testParseJsonNodeToObjectFailsMalformed() {
        JsonNode aJsonNode =
                JsonNodeFactory.instance.objectNode().put("other-field", 123).put("value", "my-value");

        var actual = Parser.parseObject(aJsonNode, CustomClass.class);

        var expectedMessage = "Failed deserialize object. See error details for more information.";
        Assertions.assertTrue(actual.isLeft());
        Assertions.assertEquals(expectedMessage, actual.getLeft().message());
    }

    @Test
    public void testObjectIsStringToJsonNode() {
        String aJson = "{\"id\": 123, \"value\": \"my-value\"}";
        String aYaml = "id: 123\nvalue: my-value";

        JsonNode expected = JsonNodeFactory.instance.objectNode().put("id", 123).put("value", "my-value");

        var actualJson = Parser.objectToJsonNode(aJson);
        var actualYaml = Parser.objectToJsonNode(aYaml);

        Assertions.assertTrue(actualJson.isRight());
        Assertions.assertEquals(expected, actualJson.get());
        Assertions.assertTrue(actualYaml.isRight());
        Assertions.assertEquals(expected, actualYaml.get());
    }

    @Test
    public void testObjectIsMapToJsonNode() {
        Map<String, Object> aMap = Map.of("id", 123, "value", "my-value");
        JsonNode expected = JsonNodeFactory.instance.objectNode().put("id", 123).put("value", "my-value");

        var actual = Parser.objectToJsonNode(aMap);

        Assertions.assertTrue(actual.isRight());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    public void testObjectIsStringToJsonNodeFailsMalformed() {
        String aJson = "{\"id\": 123, \"value\": \"my-value";
        String aYaml = "id: 123\nvalue:my-value";

        var actualJson = Parser.objectToJsonNode(aJson);
        var actualYaml = Parser.objectToJsonNode(aYaml);

        var expectedMessage = "Failed deserialize object. See error details for more information.";
        Assertions.assertTrue(actualJson.isLeft());
        Assertions.assertEquals(expectedMessage, actualJson.getLeft().message());

        Assertions.assertTrue(actualYaml.isLeft());
        Assertions.assertEquals(expectedMessage, actualYaml.getLeft().message());
    }

    @Test
    public void testObjectToJsonNodeFailsUnreadableObject() {
        var aMap = new ParserTest();

        var actual = Parser.objectToJsonNode(aMap);

        var expectedMessage = "Failed deserialize object. See error details for more information.";
        Assertions.assertTrue(actual.isLeft());
        Assertions.assertEquals(expectedMessage, actual.getLeft().message());
    }

    @Test
    public void testParseObjectIsMap() {
        Map<String, Object> aMap = Map.of("id", 123, "value", "my-value");

        var expected = new CustomClass();
        expected.setId(123);
        expected.setValue("my-value");

        var actual = Parser.parseObject(aMap, CustomClass.class);

        Assertions.assertTrue(actual.isRight());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    public void testParseObjectIsMapFails() {
        Map<String, Object> aMap = Map.of("other-value", 123, "value", "my-value");

        var actual = Parser.parseObject(aMap, CustomClass.class);

        var expectedMessage = "Failed deserialize object. See error details for more information.";
        Assertions.assertTrue(actual.isLeft());
        Assertions.assertEquals(expectedMessage, actual.getLeft().message());
    }
}
