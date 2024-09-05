package com.witboost.provisioning.framework.service;

import com.witboost.provisioning.framework.common.ErrorConstants;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.request.AccessControlOperationRequest;
import com.witboost.provisioning.model.request.ProvisionOperationRequest;
import com.witboost.provisioning.model.request.ReverseProvisionOperationRequest;
import com.witboost.provisioning.model.status.ProvisionInfo;
import com.witboost.provisioning.model.status.ReverseProvisionInfo;
import io.vavr.control.Either;
import java.util.Collections;
import java.util.Set;

/**
 * Interface which encapsulate business logic component operations, like provisioning, unprovision, updateAcl, and reverse provisioning.
 * <p>
 * It must be implemented by the Tech Adapter developer one for each of the different component types to be supported, and
 * then injected onto the Framework using the {@link ProvisionConfiguration} bean.
 */
public interface ProvisionService {

    /**
     * Performs a synchronous provisioning operation using the operation request information, containing the data product and the component to provision previously parsed
     * @param operationRequest Operation request containing the parsed data product and component to provision
     * @return Either a {@link FailedOperation} if provisioning fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link ProvisionInfo} with the information to be sent back to the platform as the provision result.
     */
    default Either<FailedOperation, ProvisionInfo> provision(
            ProvisionOperationRequest<?, ? extends Specific> operationRequest) {
        return Either.left(new FailedOperation(
                "Provision for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support provisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION)))));
    }

    /**
     * Performs a synchronous unprovisioning operation using the operation request information, containing the data product and the component to provision previously parsed
     * @param operationRequest Operation request containing the parsed data product and component to provision
     * @return Either a {@link FailedOperation} if unprovisioning fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link ProvisionInfo} with the information to be sent back to the platform as the unprovision result.
     */
    default Either<FailedOperation, ProvisionInfo> unprovision(
            ProvisionOperationRequest<?, ? extends Specific> operationRequest) {
        return Either.left(new FailedOperation(
                "Unprovision for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support unprovisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION)))));
    }

    /**
     * Performs a synchronous update of the access control lists using the operation request information, containing the data product and the component to provision previously parsed.
     * This method is intended to be implemented only for consumable entities (e.g. Output Ports).
     * @param operationRequest Operation request containing the parsed data product, component to provision and the set of refs to be granted access to the consumable entity.
     *                         This set contains the full list of users and groups that have granted access, including old entries and excluding revoked principals.
     * @return Either a {@link FailedOperation} if update of the ACLs fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link ProvisionInfo} with the information to be sent back to the platform as the operation result.
     */
    default Either<FailedOperation, ProvisionInfo> updateAcl(
            AccessControlOperationRequest<?, ? extends Specific> operationRequest) {
        return Either.left(new FailedOperation(
                "Access control lists update for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support updating access control lists for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION)))));
    }

    /**
     * Performs a synchronous reverse provision operation using the operation request information, containing the input parameters, environment and more information.
     * @param operationRequest Operation request containing the parameters needed to perform the reverse provisioning operation.
     * @return Either a {@link FailedOperation} if reverse provisioning fails, containing the error information to be shown to the Tech Adapter user,
     * or a {@link ReverseProvisionInfo} with the updates to send back to the platform as a set of key value pairs to update on the component's catalog info.
     * See Witboost documentation for more information on the shape of the {@code updates} field.
     */
    default Either<FailedOperation, ReverseProvisionInfo> reverseProvision(
            ReverseProvisionOperationRequest<? extends Specific> operationRequest) {
        return Either.left(new FailedOperation(
                "Reverse provisioning for the operation request is not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support reverse provisioning for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ProvisionConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION)))));
    }
}
