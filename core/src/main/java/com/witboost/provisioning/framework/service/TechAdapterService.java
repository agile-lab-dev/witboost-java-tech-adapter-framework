package com.witboost.provisioning.framework.service;

import com.witboost.provisioning.framework.openapi.model.*;
import com.witboost.provisioning.model.status.ProvisionOperationStatus;
import com.witboost.provisioning.model.status.ReverseProvisionOperationStatus;
import com.witboost.provisioning.model.status.ValidationInfo;
import com.witboost.provisioning.model.status.ValidationOperationStatus;

public interface TechAdapterService {
    ValidationInfo validate(ProvisioningRequest provisioningRequest);

    ProvisionOperationStatus provision(ProvisioningRequest provisioningRequest);

    ProvisionOperationStatus unprovision(ProvisioningRequest provisioningRequest);

    ProvisionOperationStatus updateacl(UpdateAclRequest updateAclRequest);

    ReverseProvisionOperationStatus getReverseProvisioningStatus(String token);

    ProvisionOperationStatus getStatus(String token);

    ReverseProvisionOperationStatus runReverseProvisioning(ReverseProvisioningRequest reverseProvisioningRequest);

    String asyncValidate(ProvisioningRequest provisioningRequest);

    ValidationOperationStatus getValidationStatus(String token);
}
