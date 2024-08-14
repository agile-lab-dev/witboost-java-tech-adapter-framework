package com.witboost.provisioning.framework.service.validation;

import com.witboost.provisioning.framework.openapi.model.ProvisioningRequest;
import com.witboost.provisioning.model.ProvisionRequest;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import io.vavr.control.Either;

public interface ValidationService {

    Either<FailedOperation, ProvisionRequest<? extends Specific>> validate(ProvisioningRequest provisioningRequest);
}
