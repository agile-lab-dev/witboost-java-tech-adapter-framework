package com.witboost.provisioning.model.status;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReverseProvisionOperationStatusTest {

    @Test
    void reverseProvisionBuilder() {
        var provisionStatus = ReverseProvisionOperationStatus.builder()
                .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                .provisionInfo(ReverseProvisionInfo.builder()
                        .updates(Optional.of(JsonNodeFactory.instance.objectNode()))
                        .build())
                .build();

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getReverseProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isEmpty());
        Assertions.assertEquals(OperationType.REVERSE_PROVISION, provisionStatus.getOperationType());
    }

    @Test
    void createReverseProvisionStatus() {
        var provisionStatus = ReverseProvisionOperationStatus.createReverseProvisionOperationStatus(
                OperationStatus.OperationStatusEnum.RUNNING,
                new TaskToken("task-token"),
                ReverseProvisionInfo.builder().build());

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.RUNNING);
        Assertions.assertTrue(provisionStatus.getReverseProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isPresent());
        Assertions.assertEquals(provisionStatus.getTaskToken().get().toString(), "task-token");
        Assertions.assertEquals(OperationType.REVERSE_PROVISION, provisionStatus.getOperationType());
    }
}
