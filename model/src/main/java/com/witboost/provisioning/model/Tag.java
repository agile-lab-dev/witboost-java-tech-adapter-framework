package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {
    private String tagFQN;
    private String source;
    private String labelType;
    private String state;
}
