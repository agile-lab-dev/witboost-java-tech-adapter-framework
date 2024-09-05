package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.common.Log;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ProvisionInfo {
    @Builder.Default
    private final Optional<Object> publicInfo = Optional.empty();

    @Builder.Default
    private final Optional<Object> privateInfo = Optional.empty();

    @Singular
    private final List<Log> logs;
}
