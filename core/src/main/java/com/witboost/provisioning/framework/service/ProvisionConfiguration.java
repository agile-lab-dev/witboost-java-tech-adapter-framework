package com.witboost.provisioning.framework.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Class used to inject the {@link ProvisionService} implementation for each type of component.
 * It provides a {@code builder} method to easily create the class, which then needs to be injected as a Spring Boot Bean.
 * <p>
 * Uninitialized provision service classes will default to the interface default behaviour, which returns an error explaining
 * that provisioning operations are not supported for said component.
 *
 */
@Builder
@Getter
@AllArgsConstructor
public class ProvisionConfiguration {

    @Builder.Default
    private ProvisionService storageProvisionService = new ProvisionService() {};

    @Builder.Default
    private ProvisionService workloadProvisionService = new ProvisionService() {};

    @Builder.Default
    private ProvisionService outputPortProvisionService = new ProvisionService() {};
}
