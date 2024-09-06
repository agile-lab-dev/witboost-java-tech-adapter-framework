package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * POJO representing a Data Contract Service Level Agreements (SLAs), used to parse the {@code dataContract} field on Output Ports.
 * It provides the base fields of the specification, and an {@code additionalProperties} map for additional non-mapped fields
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceLevelAgreements {
    private Optional<String> intervalOfChange = Optional.empty();
    private Optional<String> timeliness = Optional.empty();
    private Optional<String> upTime = Optional.empty();

    @JsonAnySetter
    @JsonAnyGetter
    private Map<String, JsonNode> additionalProperties;
}
