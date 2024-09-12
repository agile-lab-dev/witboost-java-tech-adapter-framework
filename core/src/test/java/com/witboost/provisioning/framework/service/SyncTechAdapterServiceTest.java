package com.witboost.provisioning.framework.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.autoconfigure.ProvisionAutoConfiguration;
import com.witboost.provisioning.framework.common.ErrorConstants;
import com.witboost.provisioning.framework.common.TechAdapterValidationException;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.framework.service.validation.ValidationService;
import com.witboost.provisioning.framework.util.ResourceUtils;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.request.AccessControlOperationRequest;
import com.witboost.provisioning.model.request.ProvisionOperationRequest;
import com.witboost.provisioning.model.request.ReverseProvisionOperationRequest;
import com.witboost.provisioning.model.status.OperationStatus;
import com.witboost.provisioning.model.status.ReverseProvisionInfo;
import com.witboost.provisioning.model.status.ValidationInfo;
import com.witboost.provisioning.parser.Parser;
import io.vavr.control.Either;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SyncTechAdapterServiceTest {

    @Mock
    private ValidationService validationService;

    @Spy
    private ProvisionConfiguration provisionConfiguration =
            new ProvisionAutoConfiguration().defaultProvisionConfiguration();

    @InjectMocks
    private SyncTechAdapterService techAdapterService;

    @Test
    public void testValidateOk() {
        ProvisioningRequest provisioningRequest = new ProvisioningRequest();
        when(validationService.validate(provisioningRequest, OperationType.VALIDATE))
                .thenReturn(
                        Either.right(new ProvisionOperationRequest<JsonNode, Specific>(null, false, Optional.empty())));
        var expectedRes = ValidationInfo.valid();

        var actualRes = techAdapterService.validate(provisioningRequest);

        Assertions.assertEquals(expectedRes, actualRes);
    }

    @Test
    public void testValidateError() {
        ProvisioningRequest provisioningRequest = new ProvisioningRequest();
        var failedOperation = new FailedOperation("error message", Collections.singletonList(new Problem("error")));
        when(validationService.validate(provisioningRequest, OperationType.VALIDATE))
                .thenReturn(Either.left(failedOperation));

        var expectedRes = ValidationInfo.invalid((List.of("error message", "error")));

        var actualRes = techAdapterService.validate(provisioningRequest);

        Assertions.assertEquals(expectedRes, actualRes);
    }

    @Test
    void testProvisionBusinessLogicNotImplemented() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        var failedOperation = new FailedOperation(
                "Provision for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support provisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION))));

        when(validationService.validate(provisioningRequest, OperationType.PROVISION))
                .thenReturn(Either.right(new ProvisionOperationRequest<JsonNode, Specific>(
                        null, new OutputPort<>(), false, Optional.empty())));

        var exception = Assertions.assertThrows(
                TechAdapterValidationException.class, () -> techAdapterService.provision(provisioningRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }

    @Test
    void testProvisionOk() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);

        var operationRequest =
                new ProvisionOperationRequest<JsonNode, Specific>(null, new Workload<>(), false, Optional.empty());
        var provisionInfo =
                com.witboost.provisioning.model.status.ProvisionInfo.builder().build();

        ProvisionService mockProvisionService = Mockito.mock(ProvisionService.class);

        when(validationService.validate(provisioningRequest, OperationType.PROVISION))
                .thenReturn(Either.right(operationRequest));

        doReturn(mockProvisionService).when(provisionConfiguration).getWorkloadProvisionService();

        when(mockProvisionService.provision(operationRequest)).thenReturn(Either.right(provisionInfo));

        var actualResult = techAdapterService.provision(provisioningRequest);

        Assertions.assertEquals(actualResult.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(actualResult.getProvisionInfo().isPresent());
        Assertions.assertEquals(actualResult.getProvisionInfo().get(), provisionInfo);
    }

    @Test
    void testUnprovisionBusinessLogicNotImplemented() {

        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        var failedOperation = new FailedOperation(
                "Unprovision for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support unprovisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION))));

        when(validationService.validate(provisioningRequest, OperationType.UNPROVISION))
                .thenReturn(Either.right(new ProvisionOperationRequest<JsonNode, Specific>(
                        null, new OutputPort<>(), false, Optional.empty())));

        var exception = Assertions.assertThrows(
                TechAdapterValidationException.class, () -> techAdapterService.unprovision(provisioningRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }

    @Test
    void testUnprovisionOk() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);

        var operationRequest =
                new ProvisionOperationRequest<JsonNode, Specific>(null, new OutputPort<>(), false, Optional.empty());
        var provisionInfo =
                com.witboost.provisioning.model.status.ProvisionInfo.builder().build();

        ProvisionService mockProvisionService = Mockito.mock(ProvisionService.class);

        when(validationService.validate(provisioningRequest, OperationType.UNPROVISION))
                .thenReturn(Either.right(operationRequest));

        doReturn(mockProvisionService).when(provisionConfiguration).getOutputPortProvisionService();

        when(mockProvisionService.unprovision(operationRequest)).thenReturn(Either.right(provisionInfo));

        var actualResult = techAdapterService.unprovision(provisioningRequest);

        Assertions.assertEquals(actualResult.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(actualResult.getProvisionInfo().isPresent());
        Assertions.assertEquals(actualResult.getProvisionInfo().get(), provisionInfo);
    }

    @Test
    void testUpdateAclBusinessLogicNotImplemented() {
        UpdateAclRequest updateAclRequest =
                new UpdateAclRequest(List.of("user:john.doe_witboost.com"), new ProvisionInfo("", ""));

        var failedOperation = new FailedOperation(
                "Access control lists update for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support updating access control lists for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION))));

        when(validationService.validateUpdateAcl(updateAclRequest))
                .thenReturn(Either.right(new AccessControlOperationRequest<>(
                        new DataProduct<JsonNode>(),
                        Optional.of(new StorageArea<>()),
                        Set.of("user:john.doe_witboost.com"))));

        var exception = Assertions.assertThrows(
                TechAdapterValidationException.class, () -> techAdapterService.updateacl(updateAclRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }

    @Test
    void testUpdateAclOk() {
        UpdateAclRequest updateAclRequest =
                new UpdateAclRequest(List.of("user:john.doe_witboost.com"), new ProvisionInfo("", ""));

        AccessControlOperationRequest<JsonNode, Specific> operationRequest = new AccessControlOperationRequest<>(
                new DataProduct<>(), Optional.of(new StorageArea<>()), Set.of("user:john.doe_witboost.com"));
        var provisionInfo =
                com.witboost.provisioning.model.status.ProvisionInfo.builder().build();

        ProvisionService mockProvisionService = Mockito.mock(ProvisionService.class);

        when(validationService.validateUpdateAcl(updateAclRequest)).thenReturn(Either.right(operationRequest));

        doReturn(mockProvisionService).when(provisionConfiguration).getStorageProvisionService();

        when(mockProvisionService.updateAcl(operationRequest)).thenReturn(Either.right(provisionInfo));

        var actualResult = techAdapterService.updateacl(updateAclRequest);

        Assertions.assertEquals(actualResult.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(actualResult.getProvisionInfo().isPresent());
        Assertions.assertEquals(actualResult.getProvisionInfo().get(), provisionInfo);
    }

    @Test
    void testReverseProvisionBusinessLogicNotImplemented() throws IOException {
        String ymlCatalogInfo = ResourceUtils.getContentFromResource("/pr_cataloginfo_outputport.yml");
        var catalogInfo = Parser.stringToJsonNode(ymlCatalogInfo).get();
        ReverseProvisioningRequest reverseProvisioningRequest =
                new ReverseProvisioningRequest("useCaseTemplateId", "development");
        reverseProvisioningRequest.setCatalogInfo(Optional.of(catalogInfo));
        reverseProvisioningRequest.setParams(Optional.of(Map.of(
                "database", "importDb",
                "tableName", "importTable")));

        var failedOperation = new FailedOperation(
                "Reverse provisioning for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support reverse provisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION))));

        when(validationService.validateReverseProvision(reverseProvisioningRequest))
                .thenReturn(Either.right(new ReverseProvisionOperationRequest<>(
                        "useCaseTemplateId", "development", new Specific(), catalogInfo)));

        var exception = Assertions.assertThrows(
                TechAdapterValidationException.class,
                () -> techAdapterService.runReverseProvisioning(reverseProvisioningRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }

    @Test
    void testReverseProvisioningOk() throws IOException {
        String ymlCatalogInfo = ResourceUtils.getContentFromResource("/pr_cataloginfo_outputport.yml");
        var catalogInfo = Parser.stringToJsonNode(ymlCatalogInfo).get();

        ReverseProvisioningRequest reverseProvisioningRequest =
                new ReverseProvisioningRequest("useCaseTemplateId", "development");

        ReverseProvisionOperationRequest<Specific> operationRequest =
                new ReverseProvisionOperationRequest<>("useCaseTemplateId", "development", new Specific(), catalogInfo);

        ReverseProvisionInfo reverseProvisionInfo = ReverseProvisionInfo.builder()
                .updates(Optional.of(Map.of("key", "value")))
                .build();

        ProvisionService mockProvisionService = Mockito.mock(ProvisionService.class);

        when(validationService.validateReverseProvision(reverseProvisioningRequest))
                .thenReturn(Either.right(operationRequest));

        doReturn(mockProvisionService).when(provisionConfiguration).getOutputPortProvisionService();

        when(mockProvisionService.reverseProvision(operationRequest)).thenReturn(Either.right(reverseProvisionInfo));

        var actualResult = techAdapterService.runReverseProvisioning(reverseProvisioningRequest);

        Assertions.assertEquals(actualResult.getOperationStatus(), OperationStatus.OperationStatusEnum.COMPLETED);
        Assertions.assertTrue(actualResult.getReverseProvisionInfo().isPresent());
        Assertions.assertEquals(actualResult.getReverseProvisionInfo().get(), reverseProvisionInfo);
    }
}
