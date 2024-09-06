package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * POJO representing an Output Port Data Contract, used to parse the {@code dataContract} field on Output Ports.
 * It provides the base fields of the specification, and an {@code additionalProperties} map for additional non-mapped fields
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataContract {
    private List<Column> schema;
    private Optional<String> termsAndConditions = Optional.empty();
    private Optional<ServiceLevelAgreements> SLA = Optional.empty();
    private Optional<String> endpoint = Optional.empty();
    private Optional<String> biTempBusinessTs = Optional.empty();
    private Optional<String> biTempWriteTs = Optional.empty();

    @JsonAnySetter
    @JsonAnyGetter
    private Map<String, JsonNode> additionalProperties;
}
