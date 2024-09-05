package com.witboost.provisioning.framework.service.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Class used to inject the {@link ComponentValidationService} implementation for each type of component.
 * It provides a {@code builder} method to easily create the class, which then needs to be injected as a Spring Boot Bean.
 * <p>
 * Uninitialized validation service classes will default to the interface default behaviour, which returns an error explaining
 * that the validation is not supported for said component.
 *
 */
@Builder
@Getter
@AllArgsConstructor
public class ValidationConfiguration {

    @Builder.Default
    private final ComponentValidationService storageValidationService = new ComponentValidationService() {};

    @Builder.Default
    private final ComponentValidationService workloadValidationService = new ComponentValidationService() {};

    @Builder.Default
    private final ComponentValidationService outputPortValidationService = new ComponentValidationService() {};
}
