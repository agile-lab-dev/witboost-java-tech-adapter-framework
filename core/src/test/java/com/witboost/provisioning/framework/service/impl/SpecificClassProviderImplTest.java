package com.witboost.provisioning.framework.service.impl;

import com.witboost.provisioning.model.Specific;
import io.vavr.control.Option;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpecificClassProviderImplTest {

    @Test
    void get() {
        var classProvider =
                new SpecificClassProviderImpl(Map.of("useCaseTemplateId", Specific.class), null, Map.of(), null);

        Assertions.assertEquals(classProvider.get("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.get("another"), Option.none());
    }

    @Test
    void getReverseProvisioning() {
        var classProvider =
                new SpecificClassProviderImpl(Map.of(), null, Map.of("useCaseTemplateId", Specific.class), null);

        Assertions.assertEquals(
                classProvider.getReverseProvisioningParams("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.getReverseProvisioningParams("another"), Option.none());
    }

    @Test
    void builder() {
        var classProvider = SpecificClassProviderImpl.builder()
                .withReverseProvisionSpecificClass("useCaseTemplateId", Specific.class)
                .withSpecificClass("useCaseTemplateId", Specific.class)
                .build();

        Assertions.assertEquals(classProvider.get("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.get("another"), Option.none());
        Assertions.assertEquals(
                classProvider.getReverseProvisioningParams("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.getReverseProvisioningParams("another"), Option.none());
    }

    @Test
    void builderWithDefaults() {
        var classProvider = SpecificClassProviderImpl.builder()
                .withReverseProvisionSpecificClass("useCaseTemplateId", Specific.class)
                .withDefaultReverseProvisionSpecificClass(Specific.class)
                .withSpecificClass("useCaseTemplateId", Specific.class)
                .withDefaultSpecificClass(Specific.class)
                .build();

        Assertions.assertEquals(classProvider.get("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.get("another"), Option.of(Specific.class));
        Assertions.assertEquals(
                classProvider.getReverseProvisioningParams("useCaseTemplateId"), Option.of(Specific.class));
        Assertions.assertEquals(classProvider.getReverseProvisioningParams("another"), Option.of(Specific.class));
    }
}
