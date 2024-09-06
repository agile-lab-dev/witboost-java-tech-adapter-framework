package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class representing a component entity. Default implementations are provided for the base components {@link StorageArea}, {@link Workload}, {@link OutputPort}
 * or it can be implemented. It provides the most used fields and an {@code additionalProperties} map for additional non-mapped fields
 * @param <T> Component type parameter representing the type of the {@code specific} field
 * @implNote When implementing yourself this class, the child <b>must</b> have a single type parameter
 * referring to the {@code specific} attribute field, and the generic must be resolver when instancing the class. This
 * is done to allow  the developer to inject the generic type using a class provider.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Component<T> {

    @NotNull
    protected String id;

    @NotNull
    protected String name;

    @NotNull
    protected Optional<String> fullyQualifiedName = Optional.empty();

    @NotNull
    protected String description;

    @NotNull
    protected String kind;

    @NotNull
    protected @Valid T specific;

    @NotNull
    protected Optional<JsonNode> info = Optional.empty();

    @JsonAnySetter
    @JsonAnyGetter
    private Map<String, JsonNode> additionalProperties;
}
