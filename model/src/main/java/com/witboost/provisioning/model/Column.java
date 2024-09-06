package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Column {
    private String name;
    private String description;
    private String dataType;
    private Optional<String> arrayDataType = Optional.empty();
    private Optional<Integer> dataLength = Optional.empty();
    private Optional<String> constraint = Optional.empty();
    private Optional<Integer> precision = Optional.empty();
    private Optional<Integer> scale = Optional.empty();
    private List<Tag> tags = List.of();
}
