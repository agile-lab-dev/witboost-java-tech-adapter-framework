package com.witboost.provisioning.framework.service.impl;

import com.witboost.provisioning.framework.service.ComponentClassProvider;
import com.witboost.provisioning.model.Component;
import com.witboost.provisioning.model.OutputPort;
import com.witboost.provisioning.model.StorageArea;
import com.witboost.provisioning.model.Workload;
import io.vavr.control.Option;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Singular;

/**
 * Provides a base implementation for the {@link ComponentClassProvider} interface using a key-value map, where the key
 * corresponds to the component useCaseTemplateId, and the value to the {@link Component} class. It also provides an optional
 * default class that, if set, is returned when the key-value map doesn't contain the received useCaseTemplateId.
 * <p>
 * This class provides a {@code builder} method to instantiate itself, as well as a static {@code defaultComponentsImpl} method to
 * set up the ClassProvider using the default {@link StorageArea}, {@link Workload}, and {@link OutputPort} components.
 *
 * @see ComponentClassProvider
 */
@Builder(setterPrefix = "with")
public class ComponentClassProviderImpl implements ComponentClassProvider {

    @Singular
    private Map<String, Class<? extends Component>> componentClasses;

    private Class<? extends Component> defaultClass;

    @Override
    public Option<Class<? extends Component>> get(String useCaseTemplateId) {
        Option<Class<? extends Component>> mappedClass = Option.of(componentClasses.get(useCaseTemplateId));
        if (mappedClass.isEmpty()) return Option.of(defaultClass);
        else return mappedClass;
    }

    /**
     * Creates a {@link ComponentClassProviderImpl} that maps up to three useCaseTemplateId (ignoring {@code null} parameters)
     * onto the default component classes ({@link StorageArea}, {@link Workload}, {@link OutputPort}).
     * @param storageUseCaseTemplateId Use Case Template Id to associate with {@link StorageArea}
     * @param workloadUseCaseTemplateId Use Case Template Id to associate with {@link Workload}
     * @param outputPortUseCaseTemplateId Use Case Template Id to associate with {@link OutputPort}
     * @return {@link ComponentClassProviderImpl} with an internal map to associate the useCaseTemplateId parameters with the default
     * component classes.
     */
    public static ComponentClassProviderImpl defaultComponentsImpl(
            String storageUseCaseTemplateId, String workloadUseCaseTemplateId, String outputPortUseCaseTemplateId) {
        var map = new HashMap<String, Class<? extends Component>>();
        if (storageUseCaseTemplateId != null) {
            map.put(storageUseCaseTemplateId, StorageArea.class);
        }
        if (workloadUseCaseTemplateId != null) {
            map.put(workloadUseCaseTemplateId, Workload.class);
        }
        if (outputPortUseCaseTemplateId != null) {
            map.put(outputPortUseCaseTemplateId, OutputPort.class);
        }
        return new ComponentClassProviderImpl(map, null);
    }
}
