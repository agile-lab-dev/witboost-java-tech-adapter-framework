package com.witboost.provisioning.framework.api;

import static org.mockito.Mockito.when;

import com.witboost.provisioning.framework.common.SpecificProvisionerValidationException;
import com.witboost.provisioning.framework.openapi.model.DescriptorKind;
import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.ValidationError;
import com.witboost.provisioning.framework.openapi.model.ValidationResult;
import com.witboost.provisioning.framework.service.validation.ValidationService;
import com.witboost.provisioning.model.ProvisionRequest;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApiServiceImplTest {

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ApiServiceImpl provisionService;

    @Test
    public void testValidateOk() {
        ProvisioningRequest provisioningRequest = new ProvisioningRequest();
        when(validationService.validate(provisioningRequest))
                .thenReturn(Either.right(new ProvisionRequest<Specific>(null, null, false)));
        var expectedRes = new ValidationResult(true);

        var actualRes = provisionService.validate(provisioningRequest);

        Assertions.assertEquals(expectedRes, actualRes);
    }

    @Test
    public void testValidateError() {
        ProvisioningRequest provisioningRequest = new ProvisioningRequest();
        var failedOperation = new FailedOperation(Collections.singletonList(new Problem("error")));
        when(validationService.validate(provisioningRequest)).thenReturn(Either.left(failedOperation));
        var expectedRes = new ValidationResult(false).error(new ValidationError(List.of("error")));

        var actualRes = provisionService.validate(provisioningRequest);

        Assertions.assertEquals(expectedRes, actualRes);
    }

    @Test
    void testProvisionKO() {

        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        var failedOperation = new FailedOperation(
                Collections.singletonList(new Problem("Implement the provision logic based on your requirements!")));

        var exception = Assertions.assertThrows(
                SpecificProvisionerValidationException.class, () -> provisionService.provision(provisioningRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }

    @Test
    void testUnprovisionKO() {

        ProvisioningRequest provisioningRequest =
                new ProvisioningRequest(DescriptorKind.COMPONENT_DESCRIPTOR, "", false);
        var failedOperation = new FailedOperation(
                Collections.singletonList(new Problem("Implement the unprovision logic based on your requirements!")));

        var exception = Assertions.assertThrows(
                SpecificProvisionerValidationException.class, () -> provisionService.unprovision(provisioningRequest));

        Assertions.assertEquals(failedOperation, exception.getFailedOperation());
    }
}
