package com.witboost.provisioning.model.status;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.task.TaskToken;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProvisionOperationStatusTest {

    @Test
    void provisionBuilder() {
        var provisionStatus = ProvisionOperationStatus.provisionBuilder()
                .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                .provisionInfo(ProvisionInfo.builder()
                        .privateInfo(Optional.of(JsonNodeFactory.instance.objectNode()))
                        .build())
                .build();

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isEmpty());
        Assertions.assertEquals(provisionStatus.getOperationType(), OperationType.PROVISION);
    }

    @Test
    void createProvisionStatus() {
        var provisionStatus = ProvisionOperationStatus.createProvisionOperationStatus(
                OperationStatus.OperationStatusEnum.COMPLETED,
                new TaskToken("task-token"),
                ProvisionInfo.builder().build());

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isPresent());
        Assertions.assertEquals(provisionStatus.getTaskToken().get().toString(), "task-token");
        Assertions.assertEquals(provisionStatus.getOperationType(), OperationType.PROVISION);
    }

    @Test
    void unprovisionBuilder() {
        var unprovisionStatus = ProvisionOperationStatus.unprovisionBuilder()
                .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                .provisionInfo(ProvisionInfo.builder()
                        .privateInfo(Optional.of(JsonNodeFactory.instance.objectNode()))
                        .build())
                .build();

        Assertions.assertEquals(unprovisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(unprovisionStatus.getProvisionInfo().isPresent());
        Assertions.assertEquals(OperationType.UNPROVISION, unprovisionStatus.getOperationType());
    }

    @Test
    void createUnprovisionStatus() {
        var provisionStatus = ProvisionOperationStatus.createUnprovisionOperationStatus(
                OperationStatus.OperationStatusEnum.COMPLETED,
                new TaskToken("task-token"),
                ProvisionInfo.builder().build());

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isPresent());
        Assertions.assertEquals(provisionStatus.getTaskToken().get().toString(), "task-token");
        Assertions.assertEquals(OperationType.UNPROVISION, provisionStatus.getOperationType());
    }

    @Test
    void updateAclBuilder() {
        var provisionStatus = ProvisionOperationStatus.updateAclBuilder()
                .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                .provisionInfo(ProvisionInfo.builder()
                        .privateInfo(Optional.of(JsonNodeFactory.instance.objectNode()))
                        .build())
                .build();

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getProvisionInfo().isPresent());
        Assertions.assertEquals(OperationType.UPDATE_ACL, provisionStatus.getOperationType());
    }

    @Test
    void createUpdateAclStatus() {
        var provisionStatus = ProvisionOperationStatus.createUpdateAclOperationStatus(
                OperationStatus.OperationStatusEnum.COMPLETED,
                new TaskToken("task-token"),
                ProvisionInfo.builder().build());

        Assertions.assertEquals(provisionStatus.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(provisionStatus.getProvisionInfo().isPresent());
        Assertions.assertTrue(provisionStatus.getTaskToken().isPresent());
        Assertions.assertEquals(provisionStatus.getTaskToken().get().toString(), "task-token");
        Assertions.assertEquals(OperationType.UPDATE_ACL, provisionStatus.getOperationType());
    }
}
