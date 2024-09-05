package com.witboost.provisioning.autoconfigure;

import com.witboost.provisioning.framework.service.ComponentClassProvider;
import com.witboost.provisioning.framework.service.SpecificClassProvider;
import com.witboost.provisioning.framework.service.impl.ComponentClassProviderImpl;
import com.witboost.provisioning.framework.service.impl.SpecificClassProviderImpl;
import com.witboost.provisioning.framework.service.validation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides autoconfiguration for the validation and parsing interfaces, including the
 * {@link ValidationConfiguration} class which encapsulates the set of {@link ComponentValidationService} interfaces for
 * the different types of supported components, and the {@link ComponentClassProvider} and {@link SpecificClassProvider} for class mapping.
 * Configures the behaviour as the interface default behaviour, which returns an error explaining that validation is not supported for any component.
 *
 * @see ValidationConfiguration
 * @see ComponentValidationService
 * @see ComponentClassProvider
 * @see SpecificClassProvider
 *
 */
@Configuration
@ConditionalOnClass(ValidationServiceImpl.class)
public class ValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ValidationConfiguration validationConfiguration() {
        return new ValidationConfiguration(
                new ComponentValidationService() {},
                new ComponentValidationService() {},
                new ComponentValidationService() {});
    }

    @Bean
    @ConditionalOnMissingBean
    public SpecificClassProvider specificMapper() {
        return SpecificClassProviderImpl.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ComponentClassProvider componentClassProvider() {
        return ComponentClassProviderImpl.builder().build();
    }
}
