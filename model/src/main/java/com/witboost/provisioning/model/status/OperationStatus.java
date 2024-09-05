package com.witboost.provisioning.model.status;

import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class OperationStatus {

    private @NotNull OperationStatusEnum operationStatus;
    private @NotNull Optional<TaskToken> taskToken;

    public abstract OperationType getOperationType();

    public enum OperationStatusEnum {
        WAITING,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
