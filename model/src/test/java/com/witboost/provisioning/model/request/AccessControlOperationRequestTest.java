package com.witboost.provisioning.model.request;

import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.model.DataProduct;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessControlOperationRequestTest {

    @Test
    void testOptionalConstructor() {
        var request = new AccessControlOperationRequest<>(new DataProduct<>(), Set.of());
        Assertions.assertTrue(request.getComponent().isEmpty());
    }
}
