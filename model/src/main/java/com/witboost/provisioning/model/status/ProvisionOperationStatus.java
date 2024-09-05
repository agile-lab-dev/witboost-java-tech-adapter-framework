package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProvisionOperationStatus extends OperationStatus {

    private final OperationType operationType;

    private final Optional<ProvisionInfo> provisionInfo;

    private ProvisionOperationStatus(
            @NotNull OperationStatusEnum operationStatus,
            TaskToken taskToken,
            ProvisionInfo provisionInfo,
            OperationType operationType) {

        super(operationStatus, Optional.ofNullable(taskToken));
        this.operationType = operationType;
        this.provisionInfo = Optional.ofNullable(provisionInfo);
    }

    @Builder(builderClassName = "ProvisionOperationStatusBuilder", builderMethodName = "provisionBuilder")
    public static ProvisionOperationStatus createProvisionOperationStatus(
            @NotNull OperationStatusEnum operationStatus, TaskToken taskToken, ProvisionInfo provisionInfo) {
        return new ProvisionOperationStatus(operationStatus, taskToken, provisionInfo, OperationType.PROVISION);
    }

    @Builder(builderClassName = "UnprovisionOperationStatusBuilder", builderMethodName = "unprovisionBuilder")
    public static ProvisionOperationStatus createUnprovisionOperationStatus(
            @NotNull OperationStatusEnum operationStatus, TaskToken taskToken, ProvisionInfo provisionInfo) {
        return new ProvisionOperationStatus(operationStatus, taskToken, provisionInfo, OperationType.UNPROVISION);
    }

    @Builder(builderClassName = "UpdateAclOperationStatusBuilder", builderMethodName = "updateAclBuilder")
    public static ProvisionOperationStatus createUpdateAclOperationStatus(
            @NotNull OperationStatusEnum operationStatus, TaskToken taskToken, ProvisionInfo provisionInfo) {
        return new ProvisionOperationStatus(operationStatus, taskToken, provisionInfo, OperationType.UPDATE_ACL);
    }
}
