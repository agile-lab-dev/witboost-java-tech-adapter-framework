package com.witboost.provisioning.framework.controller.mapping;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.model.status.*;
import com.witboost.provisioning.model.status.ProvisionInfo;
import com.witboost.provisioning.model.status.ValidationInfo;
import java.util.List;
import java.util.Optional;

public class ProvisioningMapper {

    public static ValidationResult map(ValidationInfo validationInfo) {
        return new ValidationResult(validationInfo.isValid()).error(new ValidationError(validationInfo.errors()));
    }

    public static ProvisioningStatus map(ProvisionOperationStatus operationStatus) {
        var status = new ProvisioningStatus(map(operationStatus.getOperationStatus()), "");

        status.logs(operationStatus.getProvisionInfo().map(ProvisionInfo::getLogs).orElse(List.of()).stream()
                .map(ProvisioningMapper::map)
                .toList());
        status.setInfo(mapInfo(operationStatus));

        return status;
    }

    public static ReverseProvisioningStatus map(ReverseProvisionOperationStatus operationStatus) {
        var status = new ReverseProvisioningStatus().status(mapReverse(operationStatus.getOperationStatus()));

        status.logs(
                operationStatus.getReverseProvisionInfo().map(ReverseProvisionInfo::getLogs).orElse(List.of()).stream()
                        .map(ProvisioningMapper::map)
                        .toList());
        status.setUpdates(operationStatus
                .getReverseProvisionInfo()
                .flatMap(ReverseProvisionInfo::getUpdates)
                .orElse(JsonNodeFactory.instance.objectNode()));

        return status;
    }

    public static ValidationStatus map(ValidationOperationStatus operationStatus) {
        var status = new ValidationStatus(mapValidate(operationStatus.getOperationStatus()));

        status.setInfo(operationStatus
                .getValidationInfo()
                .map(validationInfo ->
                        new com.witboost.provisioning.framework.openapi.model.ValidationInfo(map(validationInfo))));

        return status;
    }

    public static Optional<Info> mapInfo(ProvisionOperationStatus provisionOperationStatus) {

        if (provisionOperationStatus.getProvisionInfo().isPresent()) {
            var provisionInfo = provisionOperationStatus.getProvisionInfo().get();
            var info = new Info();
            if (provisionInfo.getPrivateInfo().isPresent())
                info.setPrivateInfo(provisionInfo.getPrivateInfo().get());
            if (provisionInfo.getPublicInfo().isPresent())
                info.setPublicInfo(provisionInfo.getPublicInfo().get());
            return Optional.of(info);
        }
        return Optional.empty();
    }

    public static Log map(com.witboost.provisioning.model.common.Log log) {
        var newLog = new Log(log.getTimestamp(), map(log.getLevel()), log.getMessage());
        newLog.setPhase(log.getPhase());
        return newLog;
    }

    public static Log.LevelEnum map(com.witboost.provisioning.model.common.Log.LogLevelEnum logLevelEnum) {
        return switch (logLevelEnum) {
            case DEBUG -> Log.LevelEnum.DEBUG;
            case INFO -> Log.LevelEnum.INFO;
            case WARNING -> Log.LevelEnum.WARNING;
            case ERROR -> Log.LevelEnum.ERROR;
        };
    }

    public static ProvisioningStatus.StatusEnum map(OperationStatus.OperationStatusEnum operationStatusEnum) {
        return switch (operationStatusEnum) {
            case COMPLETED -> ProvisioningStatus.StatusEnum.COMPLETED;
            case FAILED -> ProvisioningStatus.StatusEnum.FAILED;
            case RUNNING, WAITING -> ProvisioningStatus.StatusEnum.RUNNING;
        };
    }

    public static ReverseProvisioningStatus.StatusEnum mapReverse(
            OperationStatus.OperationStatusEnum operationStatusEnum) {
        return switch (operationStatusEnum) {
            case COMPLETED -> ReverseProvisioningStatus.StatusEnum.COMPLETED;
            case FAILED -> ReverseProvisioningStatus.StatusEnum.FAILED;
            case RUNNING, WAITING -> ReverseProvisioningStatus.StatusEnum.RUNNING;
        };
    }

    public static ValidationStatus.StatusEnum mapValidate(OperationStatus.OperationStatusEnum operationStatusEnum) {
        return switch (operationStatusEnum) {
            case COMPLETED -> ValidationStatus.StatusEnum.COMPLETED;
            case FAILED -> ValidationStatus.StatusEnum.FAILED;
            case RUNNING, WAITING -> ValidationStatus.StatusEnum.RUNNING;
        };
    }
}
