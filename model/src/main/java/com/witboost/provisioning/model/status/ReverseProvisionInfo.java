package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.common.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReverseProvisionInfo {
    @Builder.Default
    private Optional<Object> updates = Optional.empty();

    @Builder.Default
    private List<Log> logs = new ArrayList<>();
}
