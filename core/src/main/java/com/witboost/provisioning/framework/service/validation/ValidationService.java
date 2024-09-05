package com.witboost.provisioning.framework.service.validation;

import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.ReverseProvisioningRequest;
import com.witboost.provisioning.framework.openapi.model.UpdateAclRequest;
import com.witboost.provisioning.model.OperationType;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.request.AccessControlOperationRequest;
import com.witboost.provisioning.model.request.ProvisionOperationRequest;
import com.witboost.provisioning.model.request.ReverseProvisionOperationRequest;
import io.vavr.control.Either;

public interface ValidationService {

    Either<FailedOperation, ProvisionOperationRequest<?, ? extends Specific>> validate(
            ProvisioningRequest provisioningRequest, OperationType operationType);

    Either<FailedOperation, AccessControlOperationRequest<?, ? extends Specific>> validateUpdateAcl(
            UpdateAclRequest updateAclRequest);

    Either<FailedOperation, ReverseProvisionOperationRequest<? extends Specific>> validateReverseProvision(
            ReverseProvisioningRequest reverseProvisionOpRequest);
}
