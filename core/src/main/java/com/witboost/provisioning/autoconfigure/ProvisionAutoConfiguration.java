package com.witboost.provisioning.autoconfigure;

import com.witboost.provisioning.framework.service.ProvisionConfiguration;
import com.witboost.provisioning.framework.service.ProvisionService;
import com.witboost.provisioning.framework.service.SyncTechAdapterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides autoconfiguration for the {@link ProvisionConfiguration} class which encapsulates the set of {@link ProvisionService}
 * interfaces for the different types of supported components. Configures the behaviour as the interface default behaviour,
 * which returns an error explaining that provisioning operations are not supported for any component.
 *
 * @see ProvisionConfiguration
 * @see ProvisionService
 */
@Configuration
@ConditionalOnClass(SyncTechAdapterService.class)
public class ProvisionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProvisionConfiguration defaultProvisionConfiguration() {
        return new ProvisionConfiguration(
                new ProvisionService() {}, new ProvisionService() {}, new ProvisionService() {});
    }

    @Bean
    @ConditionalOnMissingBean
    ProvisionService defaultProvisionService() {
        return new ProvisionService() {};
    }
}
