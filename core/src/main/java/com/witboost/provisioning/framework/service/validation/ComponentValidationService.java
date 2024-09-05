package com.witboost.provisioning.framework.service.validation;

import static io.vavr.control.Either.left;

import com.witboost.provisioning.framework.common.ErrorConstants;
import com.witboost.provisioning.model.*;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import com.witboost.provisioning.model.request.OperationRequest;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Set;

/**
 * Interface called during the validation process of every operation request to encapsulate business logic component validation.
 * <p>
 * It must be implemented by the Tech Adapter developer one for each of the different component types to be supported, and
 * then injected onto the Framework using the {@link ValidationConfiguration} bean.
 */
public interface ComponentValidationService {

    /**
     * Performs a synchronous validation of a {@code OperationRequest} which contains the parsed data product and component to provision,
     * as well as the information about which type of operation process this validation method call is being part of.
     * @param operationRequest Operation request containing the parsed data product and component to provision
     * @param operationType Which operation is being currently processed by the Tech Adapter where this validation is being called.
     * @return Either a {@link FailedOperation} if the validation fails, containing the error information to be shown to the Tech Adapter user,
     * or an empty {@code Either.right()} in case the validation is successful
     */
    default Either<FailedOperation, Void> validate(
            @Valid OperationRequest<?, ? extends Specific> operationRequest, OperationType operationType) {
        return left(new FailedOperation(
                "Validation for the operation request not supported",
                Collections.singletonList(new Problem(
                        "This adapter doesn't support validation for the received request",
                        Set.of(
                                "Ensure that the adapter is registered correctly for this type of request and that the ValidationConfiguration is set up to support the requested component",
                                ErrorConstants.PLATFORM_TEAM_SOLUTION)))));
    }
}
