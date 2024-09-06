package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.model.common.Constants;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputPort<T> extends Component<T> {

    private String version;
    private String infrastructureTemplateId;
    private Optional<String> useCaseTemplateId = Optional.empty();
    private List<String> dependsOn;
    private Optional<String> platform = Optional.empty();
    private Optional<String> technology = Optional.empty();
    private String outputPortType;
    private Optional<String> creationDate = Optional.empty();
    private Optional<String> startDate = Optional.empty();
    private Optional<String> retentionTime = Optional.empty();
    private Optional<String> processDescription = Optional.empty();
    private DataContract dataContract;
    private JsonNode dataSharingAgreement;
    private List<Tag> tags;
    private Optional<JsonNode> sampleData = Optional.empty();
    private Optional<JsonNode> semanticLinking = Optional.empty();

    public OutputPort() {
        this.kind = Constants.OUTPUTPORT_KIND;
    }
}
