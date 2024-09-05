package com.witboost.provisioning.framework.controller.mapping;

import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.model.common.Log;
import com.witboost.provisioning.model.status.*;
import com.witboost.provisioning.model.status.ProvisionInfo;
import com.witboost.provisioning.model.status.ValidationInfo;
import com.witboost.provisioning.model.task.TaskToken;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProvisioningMapperTest {

    @Test
    void mapValidationInfo() {
        var validationInfo = new ValidationInfo(false, List.of("error1", "error2"));

        var expected = new ValidationResult(false).error(new ValidationError(List.of("error1", "error2")));

        var actualResult = ProvisioningMapper.map(validationInfo);

        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void mapProvisioningStatus() {
        var privateInfo = Map.of("key", "value");
        var timestamp = OffsetDateTime.of(2024, 12, 31, 12, 59, 59, 0, ZoneOffset.UTC);
        var status = ProvisionOperationStatus.createProvisionOperationStatus(
                OperationStatus.OperationStatusEnum.COMPLETED,
                null,
                ProvisionInfo.builder()
                        .privateInfo(Optional.of(privateInfo))
                        .logs(Collections.singletonList(new Log(timestamp, Log.LogLevelEnum.INFO, "Executing")))
                        .build());

        var expected = new ProvisioningStatus(ProvisioningStatus.StatusEnum.COMPLETED, "")
                .info(new Info(null, privateInfo))
                .logs(Collections.singletonList(new com.witboost.provisioning.framework.openapi.model.Log(
                        timestamp, com.witboost.provisioning.framework.openapi.model.Log.LevelEnum.INFO, "Executing")));

        var actualResult = ProvisioningMapper.map(status);

        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void mapReverseProvisioningStatus() {
        var updates = Map.of("key", "value");
        var timestamp = OffsetDateTime.of(2024, 12, 31, 12, 59, 59, 0, ZoneOffset.UTC);

        var reverseProvisionOperationStatus = ReverseProvisionOperationStatus.createReverseProvisionOperationStatus(
                OperationStatus.OperationStatusEnum.FAILED,
                null,
                ReverseProvisionInfo.builder()
                        .updates(Optional.of(updates))
                        .logs(Collections.singletonList(new Log(timestamp, Log.LogLevelEnum.DEBUG, "Executing")))
                        .build());

        var expected = new ReverseProvisioningStatus(ReverseProvisioningStatus.StatusEnum.FAILED, updates)
                .logs(Collections.singletonList(new com.witboost.provisioning.framework.openapi.model.Log(
                        timestamp,
                        com.witboost.provisioning.framework.openapi.model.Log.LevelEnum.DEBUG,
                        "Executing")));

        var actualResult = ProvisioningMapper.map(reverseProvisionOperationStatus);

        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void mapValidationStatus() {
        var status = new ValidationOperationStatus(OperationStatus.OperationStatusEnum.RUNNING, new TaskToken("token"));

        var expected = new ValidationStatus(ValidationStatus.StatusEnum.RUNNING);

        var actualResult = ProvisioningMapper.map(status);

        Assertions.assertEquals(expected, actualResult);
    }

    @Test
    void mapValidationStatusCompleted() {
        var status = new ValidationOperationStatus(ValidationInfo.invalid(List.of("error1", "error2")));

        var expected = new ValidationStatus(ValidationStatus.StatusEnum.COMPLETED)
                .info(new com.witboost.provisioning.framework.openapi.model.ValidationInfo(
                        new ValidationResult(false).error(new ValidationError(List.of("error1", "error2")))));

        var actualResult = ProvisioningMapper.map(status);

        Assertions.assertEquals(expected, actualResult);
    }
}
