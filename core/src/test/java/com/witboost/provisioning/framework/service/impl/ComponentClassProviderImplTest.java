package com.witboost.provisioning.framework.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.witboost.provisioning.model.OutputPort;
import com.witboost.provisioning.model.StorageArea;
import com.witboost.provisioning.model.Workload;
import io.vavr.control.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ComponentClassProviderImplTest {

    @Test
    void defaultComponentsImpl() {
        var classProvider = ComponentClassProviderImpl.defaultComponentsImpl(
                "storageUseCaseTemplateId", "workloadUseCaseTemplateId", "outputPortUseCaseTemplateId");

        Assertions.assertEquals(classProvider.get("storageUseCaseTemplateId"), Option.of(StorageArea.class));
        Assertions.assertEquals(classProvider.get("workloadUseCaseTemplateId"), Option.of(Workload.class));
        Assertions.assertEquals(classProvider.get("outputPortUseCaseTemplateId"), Option.of(OutputPort.class));
    }

    @Test
    void builder() {
        var classProvider = ComponentClassProviderImpl.builder()
                .withComponentClass("myUseCaseTemplateId", StorageArea.class)
                .build();

        Assertions.assertEquals(classProvider.get("myUseCaseTemplateId"), Option.of(StorageArea.class));
        Assertions.assertEquals(classProvider.get("another"), Option.none());
    }

    @Test
    void builderWithDefault() {
        var classProvider = ComponentClassProviderImpl.builder()
                .withComponentClass("myUseCaseTemplateId", StorageArea.class)
                .withDefaultClass(Workload.class)
                .build();

        Assertions.assertEquals(classProvider.get("myUseCaseTemplateId"), Option.of(StorageArea.class));
        Assertions.assertEquals(classProvider.get("another"), Option.of(Workload.class));
    }
}
