package com.witboost.provisioning.model.request;

import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.DataProduct;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccessControlOperationRequest<T, U> extends OperationRequest<T, U> {

    private final Set<String> refs;

    public AccessControlOperationRequest(DataProduct<T> dataProduct, Set<String> refs) {
        super(dataProduct, Optional.empty());
        this.refs = refs;
    }

    public AccessControlOperationRequest(
            DataProduct<T> dataProduct, Optional<Component<U>> component, Set<String> refs) {
        super(dataProduct, component);
        this.refs = refs;
    }
}
