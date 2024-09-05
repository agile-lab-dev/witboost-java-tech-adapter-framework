package com.witboost.provisioning.framework.service.validation;

import static com.witboost.provisioning.framework.common.ErrorConstants.PLATFORM_TEAM_SOLUTION;
import static com.witboost.provisioning.model.common.Constants.*;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.framework.openapi.model.DescriptorKind;
import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.ReverseProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.UpdateAclRequest;
import com.witboost.provisioning.framework.service.ComponentClassProvider;
import com.witboost.provisioning.framework.service.SpecificClassProvider;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.request.AccessControlOperationRequest;
import com.witboost.provisioning.model.request.OperationRequest;
import com.witboost.provisioning.model.request.ProvisionOperationRequest;
import com.witboost.provisioning.model.request.ReverseProvisionOperationRequest;
import com.witboost.provisioning.parser.Parser;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final ValidationConfiguration validationConfiguration;

    private final SpecificClassProvider specificClassProvider;

    private final ComponentClassProvider componentClassProvider;

    private static final Logger logger = LoggerFactory.getLogger(ValidationServiceImpl.class);

    public ValidationServiceImpl(
            ValidationConfiguration validationConfiguration,
            ComponentClassProvider componentClassProvider,
            SpecificClassProvider specificClassProvider) {
        this.validationConfiguration = validationConfiguration;
        this.componentClassProvider = componentClassProvider;
        this.specificClassProvider = specificClassProvider;
    }

    @Override
    public Either<FailedOperation, ProvisionOperationRequest<?, ? extends Specific>> validate(
            ProvisioningRequest provisioningRequest, OperationType operationType) {
        logger.info("Starting Descriptor validation");

        logger.info("Checking Descriptor Kind equals COMPONENT_DESCRIPTOR");
        // TODO Currently we support only component-level provisioning, but the framework is designed to implement data
        //  product level provisioning in the future
        if (!DescriptorKind.COMPONENT_DESCRIPTOR.equals(provisioningRequest.getDescriptorKind())) {
            String errorMessage = String.format(
                    "Received descriptorKind not supported by the Java Tech Adapter Framework. Expected: '%s', Actual: '%s'",
                    DescriptorKind.COMPONENT_DESCRIPTOR, provisioningRequest.getDescriptorKind());
            logger.error(errorMessage);
            return left(new FailedOperation(
                    "The service doesn't support the received kind of descriptor. See the error details for more information",
                    Optional.empty(),
                    Optional.of("descriptorKind"),
                    Collections.singletonList(new Problem(errorMessage))));
        }

        var eitherBaseOperationRequest = getAndParseComponentDescriptor(provisioningRequest.getDescriptor());
        if (eitherBaseOperationRequest.isLeft()) return left(eitherBaseOperationRequest.getLeft());
        var baseOperationRequest = eitherBaseOperationRequest.get();

        var eitherComponentKind = baseOperationRequest.getComponentKindToProvision();
        if (eitherComponentKind.isLeft()) {
            logger.error("Error while retrieving component kind: {}", eitherComponentKind.getLeft());
            return left(eitherComponentKind.getLeft());
        }

        var componentKindToProvision = eitherComponentKind.get();

        Optional<JsonNode> optionalEnrichedDescriptor = Optional.empty();
        if (provisioningRequest.getLatestEnrichedDescriptor().isPresent()) {
            var stringEnrichedDescriptor =
                    provisioningRequest.getLatestEnrichedDescriptor().get();
            logger.info("Parsing latest enriched descriptor");
            logger.debug("Parsing latest enriched descriptor {}", stringEnrichedDescriptor);
            var eitherParsedEnrichedDescriptor = Parser.objectToJsonNode(stringEnrichedDescriptor);
            if (eitherParsedEnrichedDescriptor.isLeft()) {
                logger.error(
                        "Latest enriched descriptor parsing failed with error: {}",
                        eitherParsedEnrichedDescriptor.getLeft());
                return left(eitherParsedEnrichedDescriptor.getLeft());
            }
            optionalEnrichedDescriptor = Optional.of(eitherParsedEnrichedDescriptor.get());
        }

        var operationRequest = new ProvisionOperationRequest<>(
                baseOperationRequest.getDataProduct(),
                baseOperationRequest.getComponent(),
                provisioningRequest.getRemoveData(),
                optionalEnrichedDescriptor);

        logger.info("Sending parsed operation request to ValidationService");
        logger.debug("Sending parsed operation request {} to ValidationService", operationRequest);
        switch (componentKindToProvision) {
            case STORAGE_KIND:
                var storageAreaValidationResult =
                        validationConfiguration.getStorageValidationService().validate(operationRequest, operationType);
                if (storageAreaValidationResult.isLeft()) {
                    logger.error(
                            "Received error on storage area business logic validation: {}",
                            storageAreaValidationResult.getLeft());
                    return left(storageAreaValidationResult.getLeft());
                }
                break;
            case OUTPUTPORT_KIND:
                var outputPortValidationResult = validationConfiguration
                        .getOutputPortValidationService()
                        .validate(operationRequest, operationType);
                if (outputPortValidationResult.isLeft()) {
                    logger.error(
                            "Received error on output port business logic validation: {}",
                            outputPortValidationResult.getLeft());
                    return left(outputPortValidationResult.getLeft());
                }
                break;
            case WORKLOAD_KIND:
                var workloadValidationResult = validationConfiguration
                        .getWorkloadValidationService()
                        .validate(operationRequest, operationType);
                if (workloadValidationResult.isLeft()) {
                    logger.error(
                            "Received error on workload business logic validation: {}",
                            workloadValidationResult.getLeft());
                    return left(workloadValidationResult.getLeft());
                }
                break;
            default:
                String errorMessage = String.format(
                        "The received component is of kind '%s' which is not supported by the Java Tech Adapter Framework",
                        componentKindToProvision);
                logger.error(errorMessage);
                return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
        }
        return right(operationRequest);
    }

    @Override
    public Either<FailedOperation, AccessControlOperationRequest<?, ? extends Specific>> validateUpdateAcl(
            UpdateAclRequest updateAclRequest) {

        var eitherBaseOperationRequest = getAndParseComponentDescriptor(
                updateAclRequest.getProvisionInfo().getRequest());
        if (eitherBaseOperationRequest.isLeft()) return left(eitherBaseOperationRequest.getLeft());
        var baseOperationRequest = eitherBaseOperationRequest.get();

        var eitherComponentKind = baseOperationRequest.getComponentKindToProvision();
        if (eitherBaseOperationRequest.isLeft()) {
            logger.error("Error while retrieving component kind: {}", eitherBaseOperationRequest.getLeft());
            return left(eitherBaseOperationRequest.getLeft());
        }

        var componentKindToProvision = eitherComponentKind.get();

        var operationRequest = new AccessControlOperationRequest<>(
                baseOperationRequest.getDataProduct(),
                baseOperationRequest.getComponent(),
                Set.copyOf(updateAclRequest.getRefs()));

        logger.info("Sending parsed Access Control operation request to ValidationService");
        logger.debug("Sending parsed Access Control operation request {} to ValidationService", operationRequest);
        switch (componentKindToProvision) {
            case STORAGE_KIND:
                var storageAreaValidationResult = validationConfiguration
                        .getStorageValidationService()
                        .validate(operationRequest, OperationType.UPDATE_ACL);
                if (storageAreaValidationResult.isLeft()) {
                    logger.error(
                            "Received error on storage area business logic validation: {}",
                            storageAreaValidationResult.getLeft());
                    return left(storageAreaValidationResult.getLeft());
                }
                break;
            case OUTPUTPORT_KIND:
                var outputPortValidationResult = validationConfiguration
                        .getOutputPortValidationService()
                        .validate(operationRequest, OperationType.UPDATE_ACL);
                if (outputPortValidationResult.isLeft()) {
                    logger.error(
                            "Received error on output port business logic validation: {}",
                            outputPortValidationResult.getLeft());
                    return left(outputPortValidationResult.getLeft());
                }
                break;
            case WORKLOAD_KIND:
                var workloadValidationResult = validationConfiguration
                        .getWorkloadValidationService()
                        .validate(operationRequest, OperationType.UPDATE_ACL);
                if (workloadValidationResult.isLeft()) {
                    logger.error(
                            "Received error on workload business logic validation: {}",
                            workloadValidationResult.getLeft());
                    return left(workloadValidationResult.getLeft());
                }
                break;
            default:
                String errorMessage = String.format(
                        "The received component is of kind '%s' which is not supported by the Java Tech Adapter Framework",
                        componentKindToProvision);
                logger.error(errorMessage);
                return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
        }
        return right(operationRequest);
    }

    private Either<FailedOperation, OperationRequest<?, ? extends Specific>> getAndParseComponentDescriptor(
            String componentDescriptor) {

        logger.info("Parsing Descriptor and component to provision");
        var eitherDescriptor = Parser.parseComponentDescriptor(componentDescriptor);
        if (eitherDescriptor.isLeft()) {
            logger.error("Descriptor parsing failed with error {}", eitherDescriptor.getLeft());
            return left(eitherDescriptor.getLeft());
        }
        var descriptor = eitherDescriptor.get();

        var componentId = descriptor.getComponentIdToProvision();

        logger.info("Checking presence of component to provision '{}' in the descriptor", componentId);
        Option<JsonNode> optionalComponentToProvision =
                descriptor.getDataProduct().getComponentToProvision(componentId);

        if (optionalComponentToProvision.isEmpty()) {
            String errorMessage =
                    String.format("The component with ID '%s' wasn't found in the received descriptor", componentId);
            logger.error(errorMessage);
            return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
        }

        JsonNode componentToProvisionAsJson = optionalComponentToProvision.get();

        logger.info("Getting useCaseTemplateId for component to provision '{}'", componentId);
        var optionalComponentUseCaseTemplateId =
                descriptor.getDataProduct().getComponentUseCaseTemplateIdToProvision(componentId);
        if (optionalComponentUseCaseTemplateId.isEmpty()) {
            String errorMessage = String.format(
                    "Couldn't retrieve 'useCaseTemplateId' field for the component with ID '%s'", componentId);
            logger.error(errorMessage);
            return left(new FailedOperation(errorMessage, Collections.singletonList(new Problem(errorMessage))));
        }
        var useCaseTemplateId = optionalComponentUseCaseTemplateId.get();

        logger.info(
                "Retrieving model class for component '{}' with useCaseTemplateId '{}'",
                componentId,
                useCaseTemplateId);
        var optionalComponentSchema = componentClassProvider.get(useCaseTemplateId);
        if (optionalComponentSchema.isEmpty()) {
            String errorMessage = String.format(
                    "No model class provided on ComponentClassProvider for component with useCaseTemplateId '%s'",
                    useCaseTemplateId);
            logger.error(errorMessage);
            return left(new FailedOperation(
                    "The service doesn't support the received component descriptor. Cannot find configuration on how to parse the component.",
                    List.of(new Problem(errorMessage))));
        }
        Class<? extends Component> componentClass = optionalComponentSchema.get();
        logger.info(
                "Retrieved class {} for component with useCaseTemplateId {}",
                componentClass.getName(),
                useCaseTemplateId);

        logger.info(
                "Retrieving specific class for component '{}' with useCaseTemplateId '{}'",
                componentId,
                useCaseTemplateId);
        var optionalSpecificSchema = specificClassProvider.get(useCaseTemplateId);
        if (optionalSpecificSchema.isEmpty()) {
            String errorMessage = String.format(
                    "No Specific class provided on SpecificClassProvider for useCaseTemplateId '%s'",
                    useCaseTemplateId);
            logger.error(errorMessage);
            return left(new FailedOperation(
                    "The service doesn't support the received component descriptor. Cannot find configuration on how to parse the 'specific' field.",
                    Collections.singletonList(new Problem(errorMessage))));
        }
        Class<? extends Specific> specificSchema = optionalSpecificSchema.get();
        logger.info(
                "Retrieved class {} for specific schema for component with useCaseTemplateId {}",
                specificSchema.getName(),
                useCaseTemplateId);

        Component<? extends Specific> parsedComponent;
        logger.info(
                "Parsing Component '{}' with Specific schema of type '{}'",
                componentClass.getName(),
                specificSchema.getName());
        Either<FailedOperation, ? extends Component<? extends Specific>> eitherParsedComponent =
                Parser.parseComponent(componentToProvisionAsJson, componentClass, specificSchema);
        if (eitherParsedComponent.isLeft()) {
            logger.error("Component parsing failed with error: {}", eitherParsedComponent.getLeft());
            return left(eitherParsedComponent.getLeft());
        }

        parsedComponent = eitherParsedComponent.get();
        logger.debug("Parsing successful with output: {}", parsedComponent);

        return right(new OperationRequest<>(descriptor.getDataProduct(), Optional.of(parsedComponent)));
    }

    @Override
    public Either<FailedOperation, ReverseProvisionOperationRequest<? extends Specific>> validateReverseProvision(
            ReverseProvisioningRequest reverseProvisioningRequest) {
        logger.info("Starting Reverse Provisioning request validation");

        if (reverseProvisioningRequest.getCatalogInfo().isEmpty()) {
            String errorMessage = String.format(
                    "Received Reverse provisioning request with empty catalog info: %s", reverseProvisioningRequest);
            logger.error(errorMessage);
            return left(new FailedOperation(
                    "Received Reverse provisioning request with empty catalog info. Operation cannot be performed",
                    Collections.singletonList(new Problem(errorMessage, Set.of(PLATFORM_TEAM_SOLUTION)))));
        }
        var objectCatalogInfo = reverseProvisioningRequest.getCatalogInfo().get();
        logger.info("Parsing Reverse provisioning catalog info");
        logger.debug("Parsing Reverse provisioning catalog info {}", objectCatalogInfo);
        var eitherParsedCatalogInfo = Parser.objectToJsonNode(objectCatalogInfo);
        if (eitherParsedCatalogInfo.isLeft()) {
            logger.error(
                    "Reverse provisioning catalog info parsing failed with error: {}",
                    eitherParsedCatalogInfo.getLeft());
            return left(eitherParsedCatalogInfo.getLeft());
        }
        var catalogInfo = eitherParsedCatalogInfo.get();

        logger.info(
                "Retrieving specific class for useCaseTemplateId '{}'",
                reverseProvisioningRequest.getUseCaseTemplateId());
        var optionalSpecificSchema =
                specificClassProvider.getReverseProvisioningParams(reverseProvisioningRequest.getUseCaseTemplateId());
        if (optionalSpecificSchema.isEmpty()) {
            String errorMessage = String.format(
                    "No Specific class provided for reverse provisioning for useCaseTemplateId '%s'",
                    reverseProvisioningRequest.getUseCaseTemplateId());
            logger.error(errorMessage);
            return left(new FailedOperation(
                    "The service doesn't support reverse provisioning for received component. Cannot find configuration on how to parse the 'params' field.",
                    Collections.singletonList(new Problem(errorMessage))));
        }
        var specificSchema = optionalSpecificSchema.get();

        logger.info(
                "Parsing Reverse provisioning parameters with Specific schema of type '{}'", specificSchema.getName());
        if (reverseProvisioningRequest.getParams().isEmpty()) {
            logger.error(
                    "Reverse provisioning request doesn't contain parameters object. Received object: {}",
                    reverseProvisioningRequest);
            return left(new FailedOperation(
                    "The request input doesn't contain parameters to perform reverse provisioning",
                    Optional.of(reverseProvisioningRequest.toString()),
                    Optional.of("params"),
                    Collections.singletonList(new Problem(
                            "The request input doesn't contain parameters to perform reverse provisioning",
                            Set.of(PLATFORM_TEAM_SOLUTION)))));
        }

        var eitherParsedReverseProvisionParams =
                Parser.parseObject(reverseProvisioningRequest.getParams().get(), specificSchema);
        if (eitherParsedReverseProvisionParams.isLeft()) {
            logger.error(
                    "Reverse provisioning params parsing failed with error: {}",
                    eitherParsedReverseProvisionParams.getLeft());
            return left(eitherParsedReverseProvisionParams.getLeft());
        }
        var reverseProvisionParams = eitherParsedReverseProvisionParams.get();

        var reverseProvisionOpRequest = new ReverseProvisionOperationRequest<>(
                reverseProvisioningRequest.getUseCaseTemplateId(),
                reverseProvisioningRequest.getEnvironment(),
                reverseProvisionParams,
                catalogInfo);

        var eitherComponentKind = reverseProvisionOpRequest.getComponentKind();
        if (eitherComponentKind.isLeft()) {
            logger.error(eitherComponentKind.getLeft().message());
            return left(eitherComponentKind.getLeft());
        }

        return right(reverseProvisionOpRequest);
    }
}
