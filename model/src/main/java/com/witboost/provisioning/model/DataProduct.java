package com.witboost.provisioning.model;

import static io.vavr.control.Either.left;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProduct<T> {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private Optional<String> fullyQualifiedName = Optional.empty();

    @NotNull
    private String description;

    @NotNull
    private String kind;

    @NotNull
    private String domain;

    @NotNull
    private String version;

    @NotNull
    private String environment;

    @NotNull
    private String dataProductOwner;

    @NotNull
    private String dataProductOwnerDisplayName;

    @NotNull
    private Optional<String> email = Optional.empty();

    @NotNull
    private String devGroup;

    @NotNull
    private String ownerGroup;

    @NotNull
    private Optional<String> informationSLA = Optional.empty();

    @NotNull
    private Optional<String> status = Optional.empty();

    @NotNull
    private Optional<String> maturity = Optional.empty();

    @NotNull
    private Optional<JsonNode> billing = Optional.empty();

    @NotNull
    private List<JsonNode> tags;

    @NotNull
    private @Valid T specific;

    private Optional<JsonNode> info = Optional.empty();

    @NotNull
    private List<JsonNode> components;

    public Option<JsonNode> getComponentToProvision(String componentId) {
        return Option.ofOptional(Optional.ofNullable(componentId).flatMap(comp -> components.stream()
                .filter(c -> comp.equals(c.get("id").textValue()))
                .findFirst()));
    }

    public Either<FailedOperation, String> getComponentKindToProvision(String componentId) {

        var component = getComponentToProvision(componentId);

        if (component.isEmpty()) {
            return left(new FailedOperation(
                    "Error while processing the input descriptor. Component to provision is empty or malformed",
                    Optional.empty(),
                    Optional.of("dataProduct.components"),
                    Collections.singletonList(new Problem(String.format(
                            "Parsing of descriptor resulted in a request with an empty component: %s", this)))));
        }

        return Option.ofOptional(
                        Optional.ofNullable(component.get().get("kind")).map(JsonNode::textValue))
                .toEither(() -> new FailedOperation(
                        "Error while processing the input descriptor. Component to provision is empty or malformed",
                        Optional.empty(),
                        Optional.of(String.format(
                                "dataProduct.components.%s.kind",
                                component.get().get("id").textValue())),
                        Collections.singletonList(new Problem(String.format(
                                "Couldn't retrieve 'kind' field for the component with ID '%s'",
                                component.get().get("id").textValue())))));
    }

    public Option<String> getComponentUseCaseTemplateIdToProvision(String componentId) {
        return Option.ofOptional(Optional.ofNullable(componentId).flatMap(comp -> components.stream()
                .filter(c -> comp.equals(c.get("id").textValue()))
                .findFirst()
                .flatMap(c -> Optional.ofNullable(c.get("useCaseTemplateId")))
                .map(JsonNode::textValue)));
    }
}
