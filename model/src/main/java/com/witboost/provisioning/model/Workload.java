package com.witboost.provisioning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Workload<T> extends Component<T> {

    private String version;
    private String infrastructureTemplateId;
    private Optional<String> useCaseTemplateId = Optional.empty();
    private List<String> dependsOn;
    private Optional<String> platform = Optional.empty();
    private Optional<String> technology = Optional.empty();
    private Optional<String> workloadType = Optional.empty();
    private Optional<String> connectionType = Optional.empty();
    private List<Tag> tags;
    private List<String> readsFrom;

    public Workload() {
        this.kind = Constants.WORKLOAD_KIND;
    }
}
