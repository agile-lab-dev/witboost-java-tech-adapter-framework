package com.witboost.provisioning.framework.service;

import com.witboost.provisioning.framework.service.impl.SpecificClassProviderImpl;
import com.witboost.provisioning.model.Specific;
import io.vavr.control.Option;

/**
 * Provides an interface to get a {@link Specific} class based on an entity useCaseTemplateId, allowing to inject custom classes to the
 * Framework parsing process, so that components {@code specific} field, and reverse provisioning {@code params} field are parsed to a desired schema,
 * and the business logic validation and provisioning operations to receive the expected class as input.
 * <p>
 * It may be implemented by the Tech Adapter developer, or use {@link SpecificClassProviderImpl} which provides a base implementation
 *
 * @see SpecificClassProviderImpl
 */
public interface SpecificClassProvider {

    /**
     * Retrieve the {@link Specific} class based on the component's {@code useCaseTemplateId} to be used on the descriptor parsing
     * by the validation module
     * @param useCaseTemplateId Component's {@code useCaseTemplateId}
     * @return Option of a {@link Specific} class to be instanced as the {@code specific} field of the parsed component
     *
     */
    default Option<Class<? extends Specific>> get(String useCaseTemplateId) {
        return Option.none();
    }

    /**
     * Retrieve the {@link Specific} class based on the component's {@code useCaseTemplateId} to be used by the validation module
     * to parse the reverse provisioning {@code params} field on the request
     * @param useCaseTemplateId Component's {@code useCaseTemplateId}
     * @return Option of a {@link Specific} class to be instanced as the {@code params} field of the reverse provisioning request class
     *
     */
    default Option<Class<? extends Specific>> getReverseProvisioningParams(String useCaseTemplateId) {
        return Option.none();
    }
}
