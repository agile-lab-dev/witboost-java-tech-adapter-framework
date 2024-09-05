package com.witboost.provisioning.framework.service.impl;

import com.witboost.provisioning.framework.service.SpecificClassProvider;
import com.witboost.provisioning.model.*;
import io.vavr.control.Option;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;

/**
 * Provides a base implementation for the {@link SpecificClassProvider} interface using a key-value map, where the key
 * corresponds to the component useCaseTemplateId, and the value to the {@link Specific} class. It also provides an optional
 * default class that, if set, is returned when the key-value map doesn't contain the received useCaseTemplateId.
 * <p>
 * This class provides a {@code builder} method to instantiate itself.
 *
 * @see SpecificClassProvider
 */
@Builder(setterPrefix = "with")
public class SpecificClassProviderImpl implements SpecificClassProvider {

    @Singular
    private Map<String, Class<? extends Specific>> specificClasses;

    private Class<? extends Specific> defaultSpecificClass;

    @Singular
    private Map<String, Class<? extends Specific>> reverseProvisionSpecificClasses;

    private Class<? extends Specific> defaultReverseProvisionSpecificClass;

    @Override
    public Option<Class<? extends Specific>> get(String useCaseTemplateId) {
        Option<Class<? extends Specific>> mappedClass = Option.of(specificClasses.get(useCaseTemplateId));
        if (mappedClass.isEmpty()) return Option.of(defaultSpecificClass);
        else return mappedClass;
    }

    @Override
    public Option<Class<? extends Specific>> getReverseProvisioningParams(String useCaseTemplateId) {
        Option<Class<? extends Specific>> mappedClass =
                Option.of(reverseProvisionSpecificClasses.get(useCaseTemplateId));
        if (mappedClass.isEmpty()) return Option.of(defaultReverseProvisionSpecificClass);
        else return mappedClass;
    }
}
