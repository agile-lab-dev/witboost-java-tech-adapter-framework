package com.witboost.provisioning.model.request;

import static io.vavr.control.Either.left;

import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.DataProduct;
import com.witboost.provisioning.model.common.FailedOperation;
import com.witboost.provisioning.model.common.Problem;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Collections;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OperationRequest<T, U> {
    private final DataProduct<T> dataProduct;
    private final Optional<Component<U>> component;

    public Either<FailedOperation, String> getComponentKindToProvision() {

        if (component.isEmpty()) {
            return left(new FailedOperation(
                    "Error while processing the input descriptor. Component to provision is empty or malformed",
                    Optional.empty(),
                    Optional.of("dataProduct.components"),
                    Collections.singletonList(new Problem(String.format(
                            "Parsing of descriptor resulted in a request with an empty component: %s", this)))));
        }

        return Option.ofOptional(Optional.ofNullable(component.get().getKind()))
                .toEither(() -> new FailedOperation(
                        "Error while processing the input descriptor. Component to provision is empty or malformed",
                        Optional.empty(),
                        Optional.of(String.format(
                                "dataProduct.components.[(@.id == '%s')].kind",
                                component.get().getId())),
                        Collections.singletonList(new Problem(String.format(
                                "Couldn't retrieve 'kind' field for the component with ID '%s'",
                                component.get().getId())))));
    }
}
