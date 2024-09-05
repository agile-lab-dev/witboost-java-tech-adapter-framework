package com.witboost.provisioning.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.DataProduct;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProvisionOperationRequest<T, U> extends OperationRequest<T, U> {

    private final boolean removeData;

    private final Optional<JsonNode> latestEnrichedDescriptor;

    public ProvisionOperationRequest(
            DataProduct<T> dataProduct, boolean removeData, JsonNode latestEnrichedDescriptor) {
        this(dataProduct, Optional.empty(), removeData, Optional.of(latestEnrichedDescriptor));
    }

    public ProvisionOperationRequest(
            DataProduct<T> dataProduct, boolean removeData, Optional<JsonNode> latestEnrichedDescriptor) {
        this(dataProduct, Optional.empty(), removeData, latestEnrichedDescriptor);
    }

    public ProvisionOperationRequest(
            DataProduct<T> dataProduct, Component<U> component, boolean removeData, JsonNode latestEnrichedDescriptor) {
        this(dataProduct, Optional.of(component), removeData, Optional.of(latestEnrichedDescriptor));
    }

    public ProvisionOperationRequest(
            DataProduct<T> dataProduct,
            Component<U> component,
            boolean removeData,
            Optional<JsonNode> latestEnrichedDescriptor) {
        this(dataProduct, Optional.of(component), removeData, latestEnrichedDescriptor);
    }

    public ProvisionOperationRequest(
            DataProduct<T> dataProduct,
            Optional<Component<U>> component,
            boolean removeData,
            Optional<JsonNode> latestEnrichedDescriptor) {
        super(dataProduct, component);
        this.removeData = removeData;
        this.latestEnrichedDescriptor = latestEnrichedDescriptor;
    }
}
