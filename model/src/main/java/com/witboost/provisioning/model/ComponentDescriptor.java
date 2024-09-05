package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Valid
public class ComponentDescriptor<T> {

    @JsonProperty("dataProduct")
    private @Valid DataProduct<T> dataProduct;

    @JsonProperty("componentIdToProvision")
    @NotBlank
    private String componentIdToProvision;
}
