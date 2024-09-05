package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationOperationStatus extends OperationStatus {

    private final Optional<ValidationInfo> validationInfo;

    private final OperationType operationType = OperationType.VALIDATE;

    @Builder
    public ValidationOperationStatus(
            @NotNull OperationStatusEnum operationStatus, TaskToken taskToken, ValidationInfo validationInfo) {
        super(operationStatus, Optional.ofNullable(taskToken));
        this.validationInfo = Optional.ofNullable(validationInfo);
    }

    @Builder
    public ValidationOperationStatus(@NotNull OperationStatusEnum operationStatus, @NotNull TaskToken taskToken) {
        super(operationStatus, Optional.of(taskToken));
        this.validationInfo = Optional.empty();
    }

    public ValidationOperationStatus(@NotNull ValidationInfo validationInfo) {
        super(OperationStatusEnum.COMPLETED, Optional.empty());
        this.validationInfo = Optional.of(validationInfo);
    }
}
