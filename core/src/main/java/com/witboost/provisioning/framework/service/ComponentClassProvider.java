package com.witboost.provisioning.framework.service;

import com.witboost.provisioning.framework.service.impl.ComponentClassProviderImpl;
import com.witboost.provisioning.model.Component;
import io.vavr.control.Option;

/**
 * Provides an interface to get a custom {@link Component} class based on an entity useCaseTemplateId, allowing to inject custom classes to the
 * Framework parsing process, so that components are parsed to a desired schema, and the business logic validation and provisioning operations to receive the expected class as input.
 * <p>
 * It may be implemented by the Tech Adapter developer, or use {@link ComponentClassProviderImpl} which provides a base implementation.
 *
 * @see ComponentClassProviderImpl
 */
public interface ComponentClassProvider {

    /**
     * Retrieve the {@link Component} class based on the component's {@code useCaseTemplateId} to be used on the descriptor parsing
     * by the validation module
     * @param useCaseTemplateId Component's {@code useCaseTemplateId}
     * @return Option of a {@link Component} class to be instanced
     *
     * @implNote The Component return type is defined as a raw use of the parameterized class, but the return class <b>must</b> have a single type parameter
     * referring to the {@code specific} attribute field.
     */
    default Option<Class<? extends Component>> get(String useCaseTemplateId) {
        return Option.none();
    }
}
