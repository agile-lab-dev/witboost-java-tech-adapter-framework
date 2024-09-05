package com.witboost.provisioning.framework.controller;

import static com.witboost.provisioning.model.status.OperationStatus.OperationStatusEnum.COMPLETED;
import static org.mockito.Mockito.when;

import com.witboost.provisioning.framework.common.TechAdapterValidationException;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.framework.service.TechAdapterService;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.status.ProvisionOperationStatus;
import com.witboost.provisioning.model.status.ReverseProvisionOperationStatus;
import com.witboost.provisioning.model.status.ValidationInfo;
import java.util.Collections;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
public class ProvisioningControllerTest {

    @Mock
    private TechAdapterService service;

    @InjectMocks
    private ProvisioningController provisioningController;

    @Autowired
    public ProvisioningControllerTest() {}

    @Test
    void testValidateOk() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        when(service.validate(provisioningRequest)).thenReturn(ValidationInfo.valid());

        ResponseEntity<ValidationResult> actualRes = provisioningController.validate(provisioningRequest);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), actualRes.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(actualRes.getBody()).getValid());
    }

    @Test
    void testValidateHasError() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        String expectedError = "Validation error";
        when(service.validate(provisioningRequest))
                .thenReturn(ValidationInfo.invalid(Collections.singletonList(expectedError)));

        ResponseEntity<ValidationResult> actualRes = provisioningController.validate(provisioningRequest);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), actualRes.getStatusCode());
        Assertions.assertFalse(Objects.requireNonNull(actualRes.getBody()).getValid());
        Assertions.assertTrue(actualRes.getBody().getError().isPresent());
        Assertions.assertEquals(
                1, actualRes.getBody().getError().get().getErrors().size());
        actualRes.getBody().getError().get().getErrors().forEach(p -> Assertions.assertEquals(expectedError, p));
    }

    @Test
    void testProvisionOk() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        when(service.provision(provisioningRequest))
                .thenReturn(ProvisionOperationStatus.provisionBuilder()
                        .operationStatus(COMPLETED)
                        .build());

        ResponseEntity<ProvisioningStatus> actualRes = provisioningController.provision(provisioningRequest);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), actualRes.getStatusCode());
        Assertions.assertEquals(
                ProvisioningStatus.StatusEnum.COMPLETED,
                Objects.requireNonNull(actualRes.getBody()).getStatus());
    }

    @Test
    void testProvisionHasError() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        var failedOperation = new FailedOperation(
                "Error, see details for more information", Collections.singletonList(new Problem("error")));
        when(service.provision(provisioningRequest)).thenThrow(new TechAdapterValidationException(failedOperation));

        var ex = Assertions.assertThrows(
                TechAdapterValidationException.class, () -> provisioningController.provision(provisioningRequest));
        Assertions.assertEquals(failedOperation, ex.getFailedOperation());
    }

    @Test
    void testUnprovisionOk() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        when(service.unprovision(provisioningRequest))
                .thenReturn(ProvisionOperationStatus.provisionBuilder()
                        .operationStatus(COMPLETED)
                        .build());

        ResponseEntity<ProvisioningStatus> actualRes = provisioningController.unprovision(provisioningRequest);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), actualRes.getStatusCode());
        Assertions.assertEquals(
                ProvisioningStatus.StatusEnum.COMPLETED,
                Objects.requireNonNull(actualRes.getBody()).getStatus());
    }

    @Test
    void testUnprovisionHasError() {
        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        var failedOperation = new FailedOperation(
                "Error, see details for more information", Collections.singletonList(new Problem("error")));
        when(service.unprovision(provisioningRequest)).thenThrow(new TechAdapterValidationException(failedOperation));

        var ex = Assertions.assertThrows(
                TechAdapterValidationException.class, () -> provisioningController.unprovision(provisioningRequest));
        Assertions.assertEquals(failedOperation, ex.getFailedOperation());
    }

    @Test
    void testReverseProvisioningOk() {
        ReverseProvisioningRequest reverseProvisioningRequest =
                new ReverseProvisioningRequest("useCaseTemplateId", "development");
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        when(service.runReverseProvisioning(reverseProvisioningRequest))
                .thenReturn(ReverseProvisionOperationStatus.builder()
                        .operationStatus(COMPLETED)
                        .build());

        ResponseEntity<ReverseProvisioningStatus> actualRes =
                provisioningController.runReverseProvisioning(reverseProvisioningRequest);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), actualRes.getStatusCode());
        Assertions.assertEquals(
                ReverseProvisioningStatus.StatusEnum.COMPLETED,
                Objects.requireNonNull(actualRes.getBody()).getStatus());
    }

    @Test
    void testReverseProvisioningHasError() {
        ReverseProvisioningRequest reverseProvisioningRequest =
                new ReverseProvisioningRequest("useCaseTemplateId", "development");
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
        var failedOperation = new FailedOperation(
                "Error, see details for more information", Collections.singletonList(new Problem("error")));
        when(service.runReverseProvisioning(reverseProvisioningRequest))
                .thenThrow(new TechAdapterValidationException(failedOperation));

        var ex = Assertions.assertThrows(
                TechAdapterValidationException.class,
                () -> provisioningController.runReverseProvisioning(reverseProvisioningRequest));
        Assertions.assertEquals(failedOperation, ex.getFailedOperation());
    }
}
