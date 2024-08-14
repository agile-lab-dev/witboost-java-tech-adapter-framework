package com.witboost.provisioning.framework.service.validation;

import static io.vavr.control.Either.left;

import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.DataProduct;
import com.witboost.provisioning.model.Specific;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import java.util.Collections;
import org.springframework.validation.annotation.Validated;

@org.springframework.stereotype.Component
@Validated
public class StorageAreaValidation {

    public Either<FailedOperation, Void> validate(
            DataProduct dataProduct, @Valid Component<? extends Specific> component) {
        // TODO Remember to implement the validation for the storage area.
        return left(new FailedOperation(Collections.singletonList(
                new Problem("Implement the validation for storage area based on your requirements!"))));
    }
}
