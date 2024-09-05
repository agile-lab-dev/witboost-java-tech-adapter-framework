package com.witboost.provisioning.parser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.ComponentDescriptor;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static class which provides Parsing features using Jackson library. It provides methods to parse components and descriptors from
 * YAML strings, JsonNodes and objects.
 */
public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.registerModule(new Jdk8Module());
    }

    /**
     * Parses a YAML String representing a Component Descriptor (that is, including a {@code dataProduct} field and a {@code componentIdToProvision}
     * into a {@link ComponentDescriptor} class.
     * @param yamlDescriptor YAML String representing a Component Descriptor
     * @param specificClass Class to parse the Data Product {@code specific} field
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link ComponentDescriptor}
     * @param <T> Data Product type parameter representing the type of the {@code specific} field
     */
    public static <T> Either<FailedOperation, ComponentDescriptor<T>> parseComponentDescriptor(
            String yamlDescriptor, Class<T> specificClass) {

        return Try.of(() -> {
                    JavaType javaType =
                            mapper.getTypeFactory().constructParametricType(ComponentDescriptor.class, specificClass);
                    return mapper.<ComponentDescriptor<T>>readValue(yamlDescriptor, javaType);
                })
                .toEither()
                .mapLeft(throwable -> {
                    String errorMessage =
                            "Failed to deserialize the Yaml Descriptor. Details: " + throwable.getMessage();
                    logger.error(errorMessage, throwable);
                    return new FailedOperation(
                            "Failed to deserialize the input YAML descriptor. Check the error details for more information.",
                            Collections.singletonList(new Problem(errorMessage, throwable)));
                });
    }

    /**
     * Parses a YAML String representing a Component Descriptor (that is, including a {@code dataProduct} field and a {@code componentIdToProvision}
     * into a {@link ComponentDescriptor<JsonNode>} class.
     * @param yamlDescriptor YAML String representing a Component Descriptor
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link ComponentDescriptor} where the {@code specific} field is parsed as a {@link JsonNode}
     */
    public static Either<FailedOperation, ComponentDescriptor<JsonNode>> parseComponentDescriptor(
            String yamlDescriptor) {
        return parseComponentDescriptor(yamlDescriptor, JsonNode.class);
    }

    /**
     * Parses a {@link JsonNode} representing a component into a {@link Component} class, allowing to provide the class for the component {@code specific} field.
     * @param componentNode {@link JsonNode} representing a component entity
     * @param componentClass Class to parse the component entity, which must extend from the {@link Component} class.
     * @param specificClass Class to parse the Component {@code specific} field
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link Component}
     * @param <U> Component type parameter representing the type of the {@code specific} field
     */
    public static <U> Either<FailedOperation, Component<U>> parseComponent(
            JsonNode componentNode, Class<? extends Component> componentClass, Class<U> specificClass) {
        return Try.of(() -> {
                    JavaType javaType = mapper.getTypeFactory().constructParametricType(componentClass, specificClass);
                    return mapper.<Component<U>>readValue(componentNode.toString(), javaType);
                })
                .toEither()
                .mapLeft(throwable -> {
                    String errorMessage = "Failed to deserialize the component. Details: " + throwable.getMessage();
                    logger.error(errorMessage, throwable);
                    return new FailedOperation(
                            "Failed to deserialize a component input YAML descriptor. Check the error details for more information.",
                            Collections.singletonList(new Problem(errorMessage, throwable)));
                });
    }

    /**
     * Tries to parse a JSON or YAML String into a provided class, handling the error cases.
     * @param objectString JSON or YAML String representing the input class
     * @param clazz Class to parse the string into
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link T}
     * @param <T> Type parameter for the target class
     */
    public static <T> Either<FailedOperation, T> parseString(String objectString, Class<T> clazz) {
        return Try.of(() -> mapper.readTree(objectString))
                .toEither()
                .mapLeft(throwable -> {
                    String errorMessage = "Failed to deserialize object. Details: " + throwable.getMessage();
                    logger.error(errorMessage, throwable);
                    return new FailedOperation(
                            "Failed deserialize object. See error details for more information.",
                            Collections.singletonList(new Problem(errorMessage, throwable)));
                })
                .flatMap(node -> parseObject(node, clazz));
    }

    /**
     * Tries to parse a Jackson {@link JsonNode} into a provided class, handling the error cases.
     * @param node Jackson {@link JsonNode} representing the input class
     * @param clazz Class to parse the JSON String into
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link T}
     * @param <T> Type parameter for the target class
     */
    public static <T> Either<FailedOperation, T> parseObject(JsonNode node, Class<T> clazz) {
        return Try.of(() -> {
                    JavaType javaType = mapper.getTypeFactory().constructType(clazz);
                    return mapper.<T>treeToValue(node, javaType);
                })
                .toEither()
                .mapLeft(throwable -> {
                    String errorMessage = "Failed to deserialize the object. Details: " + throwable.getMessage();
                    logger.error(errorMessage, throwable);
                    return new FailedOperation(
                            "Failed deserialize object. See error details for more information.",
                            Collections.singletonList(new Problem(errorMessage, throwable)));
                });
    }

    /**
     * Tries to parse a Java {@link Object} into a provided class, handling the error cases.
     * @param object {@link Object} to be parsed into a specific class. It maybe an object that Jackson is able to deserialize and parse, like a {@link String}, a {@link java.util.Map}, a {@link JsonNode} etc.
     * @param clazz Class to parse the JSON String into
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a parsed {@link T}
     * @param <T> Type parameter for the target class
     */
    public static <T> Either<FailedOperation, T> parseObject(Object object, Class<T> clazz) {
        return objectToJsonNode(object).flatMap(node -> parseObject(node, clazz));
    }

    /**
     * Tries to transform a Java {@link Object} into a Jackson {@link JsonNode}, handling the error cases.
     * @param object {@link Object} to be parsed into a Jackson {@link JsonNode}
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link JsonNode}
     */
    public static Either<FailedOperation, JsonNode> objectToJsonNode(Object object) {
        if (object instanceof String) return stringToJsonNode((String) object);
        return Try.of(() -> (JsonNode) mapper.valueToTree(object)).toEither().mapLeft(throwable -> {
            String errorMessage = "Failed to deserialize object. Details: " + throwable.getMessage();
            logger.error(errorMessage, throwable);
            return new FailedOperation(
                    "Failed deserialize object. See error details for more information.",
                    Collections.singletonList(new Problem(errorMessage, throwable)));
        });
    }

    /**
     * Tries to transform a {@link String} into a Jackson {@link JsonNode}, handling the error cases.
     * @param yamlString {@link String} to be parsed into a Jackson {@link JsonNode}
     * @return Either a {@link FailedOperation} if the parsing fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link JsonNode}
     */
    public static Either<FailedOperation, JsonNode> stringToJsonNode(String yamlString) {
        return Try.of(() -> mapper.readTree(yamlString)).toEither().mapLeft(throwable -> {
            String errorMessage = "Failed to deserialize object. Details: " + throwable.getMessage();
            logger.error(errorMessage, throwable);
            return new FailedOperation(
                    "Failed deserialize object. See error details for more information.",
                    Collections.singletonList(new Problem(errorMessage, throwable)));
        });
    }
}
