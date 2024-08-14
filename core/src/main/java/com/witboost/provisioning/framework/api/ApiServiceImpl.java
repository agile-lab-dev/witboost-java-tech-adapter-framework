package com.witboost.provisioning.framework.api;

import com.witboost.provisioning.framework.common.SpecificProvisionerValidationException;
import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.ProvisioningStatus;
import com.witboost.provisioning.framework.openapi.model.ValidationError;
import com.witboost.provisioning.framework.openapi.model.ValidationResult;
import com.witboost.provisioning.framework.service.validation.ValidationService;
import com.witboost.provisioning.model.ProvisionRequest;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ApiServiceImpl {

    private final ValidationService service;

    public ApiServiceImpl(ValidationService validationService) {
        this.service = validationService;
    }

    public ValidationResult validate(ProvisioningRequest provisioningRequest) {
        Either<FailedOperation, ProvisionRequest<? extends Specific>> validate = service.validate(provisioningRequest);
        return validate.fold(
                failedOperation -> new ValidationResult(false)
                        .error(new ValidationError(failedOperation.problems().stream()
                                .map(Problem::description)
                                .collect(Collectors.toList()))),
                provisionRequest -> new ValidationResult(true));
    }

    public ProvisioningStatus provision(ProvisioningRequest provisioningRequest) {
        throw new SpecificProvisionerValidationException(
                "Implement the provision logic based on your requirements!",
                new FailedOperation(Collections.singletonList(
                        new Problem("Implement the provision logic based on your requirements!"))));
    }

    public ProvisioningStatus unprovision(ProvisioningRequest provisioningRequest) {
        throw new SpecificProvisionerValidationException(
                "Implement the unprovision logic based on your requirements!",
                new FailedOperation(Collections.singletonList(
                        new Problem("Implement the unprovision logic based on your requirements!"))));
    }
}
