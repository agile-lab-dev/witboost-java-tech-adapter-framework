package com.witboost.provisioning.framework.service;

import static com.witboost.provisioning.model.common.Constants.*;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.witboost.provisioning.framework.common.TechAdapterValidationException;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.framework.service.validation.ValidationService;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.request.OperationRequest;
import com.witboost.provisioning.model.status.*;
import com.witboost.provisioning.model.status.ProvisionInfo;
import com.witboost.provisioning.model.status.ValidationInfo;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SyncTechAdapterService implements TechAdapterService {

    private final ValidationService service;
    private final ProvisionConfiguration provisionConfiguration;

    private static final Logger logger = LoggerFactory.getLogger(SyncTechAdapterService.class);

    public SyncTechAdapterService(ValidationService validationService, ProvisionConfiguration provisionConfiguration) {
        this.service = validationService;
        this.provisionConfiguration = provisionConfiguration;
    }

    @Override
    public ValidationInfo validate(ProvisioningRequest provisioningRequest) {
        logger.info("Starting validate operation");
        logger.debug("Starting validate operation with body {}", provisioningRequest);
        var validate = service.validate(provisioningRequest, OperationType.VALIDATE);

        logger.info("Validate operation returned with result successful? {}. Body: {}", validate.isRight(), validate);

        return validate.fold(
                failedOperation -> {
                    logger.error("Validation operation failed with response {}", failedOperation);
                    var a = new ArrayList<String>();
                    a.add(failedOperation.message());
                    a.addAll(failedOperation.problems().stream()
                            .map(Problem::getMessage)
                            .toList());
                    return ValidationInfo.invalid(a);
                },
                operationRequest -> ValidationInfo.valid());
    }

    @Override
    public ProvisionOperationStatus provision(ProvisioningRequest provisioningRequest) {
        logger.info("Starting provision operation");
        logger.debug("Starting provision operation with body {}", provisioningRequest);

        var validate = service.validate(provisioningRequest, OperationType.PROVISION);
        logger.info(
                "Validate operation for provision request returned with result successful? {}. Body: {}",
                validate.isRight(),
                validate);

        return executeComponentLevelAction(validate, ProvisionService::provision)
                .fold(
                        error -> {
                            logger.error("Provision operation failed. Received error from ProvisionService: {}", error);
                            throw new TechAdapterValidationException(error);
                        },
                        result -> {
                            logger.info(
                                    "Provision operation successful. Received info from ProvisionService: {}", result);
                            return ProvisionOperationStatus.provisionBuilder()
                                    .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                                    .provisionInfo(result)
                                    .build();
                        });
    }

    @Override
    public ProvisionOperationStatus unprovision(ProvisioningRequest unprovisioningRequest) {
        logger.info("Starting unprovision operation");
        logger.debug("Starting unprovision operation with body {}", unprovisioningRequest);

        var validate = service.validate(unprovisioningRequest, OperationType.UNPROVISION);
        logger.info(
                "Validate operation for unprovision request returned with result successful? {}. Body: {}",
                validate.isRight(),
                validate);

        return executeComponentLevelAction(validate, ProvisionService::unprovision)
                .fold(
                        error -> {
                            logger.error(
                                    "Unprovision operation failed. Received error from ProvisionService: {}", error);
                            throw new TechAdapterValidationException(error);
                        },
                        result -> {
                            logger.info(
                                    "Unprovision operation successful. Received info from ProvisionService: {}",
                                    result);
                            return ProvisionOperationStatus.unprovisionBuilder()
                                    .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                                    .provisionInfo(result)
                                    .build();
                        });
    }

    @Override
    public ProvisionOperationStatus updateacl(UpdateAclRequest updateAclRequest) {
        logger.info("Starting update ACL operation");
        logger.debug("Starting update ACL operation with body {}", updateAclRequest);

        var validate = service.validateUpdateAcl(updateAclRequest);
        logger.info(
                "Validate operation for update ACL request returned with result successful? {}. Body: {}",
                validate.isRight(),
                validate);

        return executeComponentLevelAction(validate, ProvisionService::updateAcl)
                .fold(
                        error -> {
                            logger.error(
                                    "Update ACL operation failed. Received error from ProvisionService: {}", error);
                            throw new TechAdapterValidationException(error);
                        },
                        result -> {
                            logger.info(
                                    "Update ACL operation successful. Received info from ProvisionService: {}", result);
                            return ProvisionOperationStatus.updateAclBuilder()
                                    .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                                    .provisionInfo(result)
                                    .build();
                        });
    }

    @Override
    public ReverseProvisionOperationStatus runReverseProvisioning(
            ReverseProvisioningRequest reverseProvisioningRequest) {
        logger.info("Starting reverse provisioning operation");
        logger.debug("Starting reverse provisioning operation with body {}", reverseProvisioningRequest);

        var validate = service.validateReverseProvision(reverseProvisioningRequest);
        logger.info(
                "Validate operation for reverse provisioning request returned with result successful? {}. Body: {}",
                validate.isRight(),
                validate);

        return validate.flatMap(reverseProvisionOpRequest -> {
                    var eitherKind = reverseProvisionOpRequest.getComponentKind();
                    if (eitherKind.isLeft()) {
                        logger.error(eitherKind.getLeft().message());
                        return left(eitherKind.getLeft());
                    }

                    return getProvisionService(eitherKind.get())
                            .flatMap(provisionService -> provisionService.reverseProvision(reverseProvisionOpRequest));
                })
                .fold(
                        error -> {
                            logger.error(
                                    "Reverse provisioning operation failed. Received error from ProvisionService: {}",
                                    error);
                            throw new TechAdapterValidationException(error);
                        },
                        result -> {
                            logger.info(
                                    "Reverse provisioning operation successful. Received info from ProvisionService: {}",
                                    result);
                            return ReverseProvisionOperationStatus.builder()
                                    .operationStatus(OperationStatus.OperationStatusEnum.COMPLETED)
                                    .provisionInfo(result)
                                    .build();
                        });
    }

    @Override
    public ProvisionOperationStatus getStatus(String token) {
        throw new TechAdapterValidationException(new FailedOperation(
                "The getStatus operation is not supported by the Java Tech Adapter Framework",
                Collections.singletonList(
                        new Problem("The getStatus operation is not supported by the Java Tech Adapter Framework"))));
    }

    @Override
    public ReverseProvisionOperationStatus getReverseProvisioningStatus(String token) {
        throw new TechAdapterValidationException(
                new FailedOperation(
                        "The getReverseProvisioningStatus operation is not supported by the Java Tech Adapter Framework",
                        Collections.singletonList(
                                new Problem(
                                        "The getReverseProvisioningStatus operation is not supported by the Java Tech Adapter Framework"))));
    }

    @Override
    public String asyncValidate(ProvisioningRequest provisioningRequest) {
        throw new TechAdapterValidationException(new FailedOperation(
                "The asyncValidate operation is not supported by the Java Tech Adapter Framework",
                Collections.singletonList(new Problem(
                        "The asyncValidate operation is not supported by the Java Tech Adapter Framework"))));
    }

    @Override
    public ValidationOperationStatus getValidationStatus(String token) {
        throw new TechAdapterValidationException(new FailedOperation(
                "The getValidationStatus operation is not supported by the Java Tech Adapter Framework",
                Collections.singletonList(new Problem(
                        "The getValidationStatus operation is not supported by the Java Tech Adapter Framework"))));
    }

    private <U extends OperationRequest<?, ? extends Specific>>
            Either<FailedOperation, ProvisionInfo> executeComponentLevelAction(
                    Either<FailedOperation, U> input,
                    BiFunction<ProvisionService, U, Either<FailedOperation, ProvisionInfo>> action) {
        return input.flatMap(operationRequest -> {
            if (operationRequest.getComponent().isPresent()) {
                var component = operationRequest.getComponent().get();
                return getProvisionService(component.getKind())
                        .flatMap(provisionService -> action.apply(provisionService, operationRequest));
            } else {
                String errorMessage =
                        "The operation request doesn't contain a component and data product level provisioning is not supported by the Java Tech Adapter Framework";
                logger.error(errorMessage);
                return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
            }
        });
    }

    private Either<FailedOperation, ProvisionService> getProvisionService(String componentKind) {
        switch (componentKind) {
            case STORAGE_KIND:
                return right(provisionConfiguration.getStorageProvisionService());
            case WORKLOAD_KIND:
                return right(provisionConfiguration.getWorkloadProvisionService());
            case OUTPUTPORT_KIND:
                return right(provisionConfiguration.getOutputPortProvisionService());
            default:
                String errorMessage = String.format(
                        "The component is of kind '%s' which is not supported by the Java Tech Adapter Framework",
                        componentKind);
                logger.error(errorMessage);
                return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
        }
    }
}
