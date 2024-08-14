package com.witboost.provisioning.framework.controller;

import com.witboost.provisioning.framework.api.ApiServiceImpl;
import com.witboost.provisioning.framework.openapi.controller.V1ApiDelegate;
import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.ProvisioningStatus;
import com.witboost.provisioning.framework.openapi.model.ValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * API Controller for the Java Specific Provisioner which implements the autogenerated {@link
 * V1ApiDelegate} interface. The interface defaults the endpoints to throw 501 Not Implemented
 * unless overridden in this class.
 *
 * <p>Exceptions thrown will be handled by {@link SpecificProvisionerExceptionHandler}
 */
@Service
public class SpecificProvisionerController implements V1ApiDelegate {

    private final ApiServiceImpl apiService;

    public SpecificProvisionerController(ApiServiceImpl apiService) {
        this.apiService = apiService;
    }

    @Override
    public ResponseEntity<ProvisioningStatus> provision(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(apiService.provision(provisioningRequest));
    }

    @Override
    public ResponseEntity<ProvisioningStatus> unprovision(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(apiService.unprovision(provisioningRequest));
    }

    @Override
    public ResponseEntity<ValidationResult> validate(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(apiService.validate(provisioningRequest));
    }
}
