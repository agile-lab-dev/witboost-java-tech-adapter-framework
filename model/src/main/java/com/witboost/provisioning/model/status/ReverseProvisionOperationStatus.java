package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReverseProvisionOperationStatus extends OperationStatus {

    private final Optional<ReverseProvisionInfo> reverseProvisionInfo;

    private final OperationType operationType = OperationType.REVERSE_PROVISION;

    private ReverseProvisionOperationStatus(
            @NotNull OperationStatusEnum operationStatus,
            TaskToken taskToken,
            ReverseProvisionInfo reverseProvisionInfo) {

        super(operationStatus, Optional.ofNullable(taskToken));
        this.reverseProvisionInfo = Optional.ofNullable(reverseProvisionInfo);
    }

    @Builder
    public static ReverseProvisionOperationStatus createReverseProvisionOperationStatus(
            @NotNull OperationStatusEnum operationStatus, TaskToken taskToken, ReverseProvisionInfo provisionInfo) {

        return new ReverseProvisionOperationStatus(operationStatus, taskToken, provisionInfo);
    }
}
