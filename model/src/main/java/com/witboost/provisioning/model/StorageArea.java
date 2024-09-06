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
public class StorageArea<T> extends Component<T> {

    private List<String> owners;
    private String infrastructureTemplateId;
    private Optional<String> useCaseTemplateId = Optional.empty();
    private List<String> dependsOn;
    private Optional<String> platform = Optional.empty();
    private Optional<String> technology = Optional.empty();
    private Optional<String> storageType = Optional.empty();
    private List<Tag> tags;

    public StorageArea() {
        this.kind = Constants.STORAGE_KIND;
    }
}
