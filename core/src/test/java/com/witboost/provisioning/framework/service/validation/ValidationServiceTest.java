package com.witboost.provisioning.framework.service.validation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.witboost.provisioning.autoconfigure.ValidationAutoConfiguration;
import com.witboost.provisioning.framework.common.ErrorConstants;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.framework.service.ComponentClassProvider;
import com.witboost.provisioning.framework.service.SpecificClassProvider;
import com.witboost.provisioning.framework.util.ResourceUtils;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.model.common.Constants;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.parser.Parser;
import io.vavr.control.Option;
import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

class MyComponent<T> extends Component<T> {}

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    ComponentClassProvider componentClassProvider;

    @Spy
    ValidationConfiguration validationConfiguration =
            new ValidationAutoConfiguration().defaultValidationConfiguration();

    @Mock
    SpecificClassProvider specificClassProvider;

    @InjectMocks
    ValidationServiceImpl service;

    private final FailedOperation unimplementedFailedOperation = new FailedOperation(
            "Validation for the operation request not supported",
            Collections.singletonList(new Problem(
                    "This adapter doesn't support validation for the received request",
                    Set.of(
                            "Ensure that the adapter is registered correctly for this type of request and that the ValidationConfiguration is set up to support the requested component",
                            ErrorConstants.PLATFORM_TEAM_SOLUTION))));

    @Test
    public void testValidateOutputPortFailureToImplement() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_outputport.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);

        when(componentClassProvider.get(anyString())).thenReturn(Option.of(OutputPort.class));
        when(specificClassProvider.get(anyString())).thenReturn(Option.of(Specific.class));

        var actualResult = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertEquals(unimplementedFailedOperation, actualResult.getLeft());
    }

    @Test
    public void testValidateStorageFailureToImplement() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);

        when(componentClassProvider.get(anyString())).thenReturn(Option.of(StorageArea.class));
        when(specificClassProvider.get(anyString())).thenReturn(Option.of(Specific.class));

        var actualResult = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertEquals(unimplementedFailedOperation, actualResult.getLeft());
    }

    @Test
    public void testValidateWorkloadFailureToImplement() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_workload.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);

        when(componentClassProvider.get(anyString())).thenReturn(Option.of(Workload.class));
        when(specificClassProvider.get(anyString())).thenReturn(Option.of(Specific.class));

        var actualResult = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertEquals(unimplementedFailedOperation, actualResult.getLeft());
    }

    @Test
    public void testValidateUpdateAclOutputPortFailureToImplement() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_outputport.yml");
        UpdateAclRequest updateAclRequest =
                new UpdateAclRequest(List.of("user:john.doe_witboost.com"), new ProvisionInfo(ymlDescriptor, ""));

        when(componentClassProvider.get(anyString())).thenReturn(Option.of(OutputPort.class));
        when(specificClassProvider.get(anyString())).thenReturn(Option.of(Specific.class));

        var actualResult = service.validateUpdateAcl(updateAclRequest);

        Assertions.assertEquals(unimplementedFailedOperation, actualResult.getLeft());
    }

    @Test
    public void testValidateReverseProvisionFailureToImplement() throws IOException {
        String ymlCatalogInfo = ResourceUtils.getContentFromResource("/pr_cataloginfo_outputport.yml");
        ReverseProvisioningRequest reverseProvisioningRequest =
                new ReverseProvisioningRequest("useCaseTemplateId", "development");
        reverseProvisioningRequest.setCatalogInfo(
                Optional.of(Parser.stringToJsonNode(ymlCatalogInfo).get()));
        reverseProvisioningRequest.setParams(Optional.of(Map.of(
                "database", "importDb",
                "tableName", "importTable")));

        when(specificClassProvider.getReverseProvisioningParams(anyString())).thenReturn(Option.of(Specific.class));

        var actualResult = service.validateReverseProvision(reverseProvisioningRequest);
        verify(componentClassProvider, never()).get(anyString());

        Assertions.assertTrue(actualResult.isRight());
        Assertions.assertTrue(actualResult.get().getComponentKind().isRight());
        Assertions.assertEquals(
                Constants.OUTPUTPORT_KIND, actualResult.get().getComponentKind().get());
    }

    @Test
    public void testValidateWrongDescriptorKind() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_outputport.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.DATAPRODUCT_DESCRIPTOR, ymlDescriptor, false);
        String expectedDesc =
                "Received descriptorKind not supported by the Java Tech Adapter Framework. Expected: 'COMPONENT_DESCRIPTOR', Actual: 'DATAPRODUCT_DESCRIPTOR'";

        var actualResult = service.validate(provisioningRequest, OperationType.VALIDATE);

        verify(componentClassProvider, never()).get(anyString());
        verify(specificClassProvider, never()).get(anyString());

        Assertions.assertTrue(actualResult.isLeft());
        Assertions.assertEquals(1, actualResult.getLeft().problems().size());
        actualResult.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    public void testValidateWrongDescriptorFormat() {
        String ymlDescriptor = "an_invalid_descriptor";
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String expectedDesc = "Failed to deserialize the Yaml Descriptor. Details: ";

        var actualRes = service.validate(provisioningRequest, OperationType.VALIDATE);
        verify(componentClassProvider, never()).get(anyString());
        verify(specificClassProvider, never()).get(anyString());

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertTrue(p.description().startsWith(expectedDesc));
            Assertions.assertTrue(p.cause().isPresent());
        });
    }

    @Test
    public void testValidateMissingComponentIdToProvision() throws IOException {
        String ymlDescriptor =
                ResourceUtils.getContentFromResource("/pr_descriptor_storage_missing_componentIdToProvision.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String expectedDesc = "The component with ID 'null' wasn't found in the received descriptor";

        var actualRes = service.validate(provisioningRequest, OperationType.VALIDATE);
        verify(componentClassProvider, never()).get(anyString());
        verify(specificClassProvider, never()).get(anyString());

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    public void testValidateMissingComponentToProvision() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage_missing_component.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String expectedDesc =
                "The component with ID 'urn:dmb:cmp:healthcare:vaccinations:0:storage' wasn't found in the received descriptor";

        var actualResult = service.validate(provisioningRequest, OperationType.VALIDATE);
        verify(componentClassProvider, never()).get(anyString());
        verify(specificClassProvider, never()).get(anyString());

        Assertions.assertTrue(actualResult.isLeft());
        Assertions.assertEquals(1, actualResult.getLeft().problems().size());
        actualResult.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    public void testValidateMissingComponentKindToProvision() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage_missing_componentKind.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String expectedDesc =
                "Couldn't retrieve 'kind' field for the component with ID 'urn:dmb:cmp:healthcare:vaccinations:0:storage'";

        // As default components are intelligent enough to know their own kind, we test this behaviour with a custom
        // class not setting a kind
        when(componentClassProvider.get(anyString())).thenReturn(Option.of(MyComponent.class));
        when(specificClassProvider.get(anyString())).thenReturn(Option.of(Specific.class));

        var actualRes = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    public void testValidateNoComponentClassProvided() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage_wrong_componentKind.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String useCaseTemplateId = "urn:dmb:utm:cdp-private-hdfs-storage-template:0.0.0";
        String expectedDesc = String.format(
                "No model class provided on ComponentClassProvider for component with useCaseTemplateId '%s'",
                useCaseTemplateId);

        when(componentClassProvider.get(useCaseTemplateId)).thenReturn(Option.none());
        var actualRes = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        Assertions.assertEquals(
                "The service doesn't support the received component descriptor. Cannot find configuration on how to parse the component.",
                actualRes.getLeft().message());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }

    @Test
    public void testValidateNoSpecificClassProvided() throws IOException {
        String ymlDescriptor = ResourceUtils.getContentFromResource("/pr_descriptor_storage_wrong_componentKind.yml");
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, ymlDescriptor, false);
        String useCaseTemplateId = "urn:dmb:utm:cdp-private-hdfs-storage-template:0.0.0";
        String expectedDesc = String.format(
                "No Specific class provided on SpecificClassProvider for useCaseTemplateId '%s'", useCaseTemplateId);

        when(componentClassProvider.get(useCaseTemplateId)).thenReturn(Option.of(StorageArea.class));
        when(specificClassProvider.get(useCaseTemplateId)).thenReturn(Option.none());
        var actualRes = service.validate(provisioningRequest, OperationType.VALIDATE);

        Assertions.assertTrue(actualRes.isLeft());
        Assertions.assertEquals(1, actualRes.getLeft().problems().size());
        Assertions.assertEquals(
                "The service doesn't support the received component descriptor. Cannot find configuration on how to parse the 'specific' field.",
                actualRes.getLeft().message());
        actualRes.getLeft().problems().forEach(p -> {
            Assertions.assertEquals(expectedDesc, p.description());
            Assertions.assertTrue(p.cause().isEmpty());
        });
    }
}
