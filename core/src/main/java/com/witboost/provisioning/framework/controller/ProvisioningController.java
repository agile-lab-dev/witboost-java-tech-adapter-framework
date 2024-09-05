package com.witboost.provisioning.framework.controller;

import com.witboost.provisioning.framework.controller.mapping.ProvisioningMapper;
import com.witboost.provisioning.framework.openapi.controller.V1ApiDelegate;
import com.witboost.provisioning.framework.openapi.controller.V2ApiDelegate;
import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.framework.service.TechAdapterService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * API Controller for the Java Specific Provisioner which implements the autogenerated {@link
 * V1ApiDelegate} interface. The interface defaults the endpoints to throw 501 Not Implemented
 * unless overridden in this class.
 *
 * <p>Exceptions thrown will be handled by {@link ProvisioningExceptionHandler}
 */
@Service
class ProvisioningController implements V1ApiDelegate, V2ApiDelegate {

    private final TechAdapterService techAdapterService;

    public ProvisioningController(TechAdapterService techAdapterService) {
        this.techAdapterService = techAdapterService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return V1ApiDelegate.super.getRequest();
    }

    @Override
    public ResponseEntity<ProvisioningStatus> provision(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.provision(provisioningRequest)));
    }

    @Override
    public ResponseEntity<ProvisioningStatus> getStatus(String token) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.getStatus(token)));
    }

    @Override
    public ResponseEntity<ProvisioningStatus> unprovision(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.unprovision(provisioningRequest)));
    }

    @Override
    public ResponseEntity<ProvisioningStatus> updateacl(UpdateAclRequest updateAclRequest) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.updateacl(updateAclRequest)));
    }

    @Override
    public ResponseEntity<ValidationResult> validate(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.validate(provisioningRequest)));
    }

    @Override
    public ResponseEntity<ReverseProvisioningStatus> runReverseProvisioning(
            ReverseProvisioningRequest reverseProvisioningRequest) {
        return ResponseEntity.ok(
                ProvisioningMapper.map(techAdapterService.runReverseProvisioning(reverseProvisioningRequest)));
    }

    @Override
    public ResponseEntity<ReverseProvisioningStatus> getReverseProvisioningStatus(String token) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.getReverseProvisioningStatus(token)));
    }

    @Override
    public ResponseEntity<String> asyncValidate(ProvisioningRequest provisioningRequest) {
        return ResponseEntity.accepted().body(techAdapterService.asyncValidate(provisioningRequest));
    }

    @Override
    public ResponseEntity<ValidationStatus> getValidationStatus(String token) {
        return ResponseEntity.ok(ProvisioningMapper.map(techAdapterService.getValidationStatus(token)));
    }
}