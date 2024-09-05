# Java Tech Adapter Framework Guide

The Framework has been designed as a plug-and-play library, providing autoconfiguration for the business logic interfaces which default to sensible user errors explaining that the specific feature is not supported by the Tech Adapter.

- [Dependencies](#dependencies)
- [Boostrap a new Tech Adapter](#bootstrapping-your-tech-adapter)
- [Interface implementation](#interface-implementation)
- [Migrating from the old Java Scaffold project](#migrating-from-the-old-java-scaffold-project)

## Dependencies

To start working with the Java Tech Adapter Framework, add the following  to you `pom.xml`:

```xml
<project ...>
    ...
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    ...
    <dependencies>
        <dependency>
            <groupId>com.witboost.provisioning</groupId>
            <artifactId>java-tech-adapter-framework-core</artifactId>
            <version>X.X.X</version>
        </dependency>
    </dependencies>
    ...
</project>
```

Where `X.X.X` is the desired version of the framework.

## Bootstrapping your Tech Adapter

After including the dependencies, define you Main class as follows:

```java
import com.witboost.provisioning.framework.JavaTechAdapterFramework;

@SpringBootApplication (scanBasePackageClasses = {JavaTechAdapterFramework.class, Main.class})
@ConfigurationPropertiesScan(basePackageClasses = {JavaTechAdapterFramework.class, Main.class})
public class Main {

    /** This is the main method which acts as the entry point inside the application. */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

By adding `com.witboost.provisioning.framework.JavaTechAdapterFramework` on the Spring Boot annotations, you are instructing your application to scan the framework package as well for the service beans, regardless of your package definition.

With a single class, you now have a fully-functional Tech Adapter that receives and parses requests. At this point however, it will always return some kind of error, as it falls into its default configuration, so the next step is to implement your own business logic: 

## Interface implementation

The Java Tech Adapter Framework provides four interfaces to be implemented in order to plug your business logic into the provisioning workflow. These are:

- `ProvisionService`: Provides the business logic for component provision, unprovision, update access control, and reverse provisioning.
- `ComponentValidationService`: Provides the business logic for component validation, executed for validation and (un)provisioning operations.
- `ComponentClassProvider`: Interface that maps a component's `useCaseTemplateId` with a Class that represents the Component model, allowing to use extensions of the provided Components.
- `SpecificClassProvider`: Interface that maps a component's `useCaseTemplateId` with a Class that represents the Specific model.

To inject your implementation on the framework, you can use Spring Boot beans. 

### Class providers

`ComponentClassProvider` and `SpecificClassProvider` are two interfaces that must be implemented and injected using Spring Boot beans. Their default implementation return a failed mapping which will return a user error stating that the Tech Adapter is unaware on how to parse a specific component.

`ComponentClassProvider` provides the `Component` class used to parse an entity input based on its `useCaseTemplateId`. Custom implementations of the `Component` abstract class **must** have one generic type parameter, as the injection of the class form the Specific field is done via the `SpecificClassProvider`, not by defining the type on the class declaration.

We provide the `ComponentClassProviderImpl` implementation of the interface as a base implementation that you can use. It is based on a 1-1 mapping using a key-value map, where the key corresponds to the component useCaseTemplateId, and the value to the Component class. It also provides an optional default class that, if set, is returned when the key-value map doesn't contain the received `useCaseTemplateId`.

```java
@Configuration
class ClassProviderConfiguration {
    @Bean
    public ComponentClassProvider componentClassProvider() {
      return ComponentClassProviderImpl.builder()
              .withComponentClass("myUseCaseTemplateId", StorageArea.class)
              .withDefaultClass(OutputPort.class)
              .build();
    }
}
```

This example will map all components with their `useCaseTemplateId` equal to "myUseCaseTemplateId" to the `StorageArea` class which is provided by the Framework. This way, the components will be parsed using a Storage Area class type, with their `specific` field configured using the `SpecificClassProvider` below. All other components that don't match the "myUseCaseTemplateId" value, will default to be parsed as Output Ports, since a default class was specified. If you don't specify a default class, your Tech Adapter will return an error for all components with a `useCaseTemplateId` different to the one specified.

`SpecificClassProvider` provides the `Specific` class used to parse a component's `specific` field, or a reverse provisioning `params` field. As mentioned above, is the tool used to inject the concrete type for the `Component` class. Classes defined to model the `specific` field or the reverse provisioning `params` field must implement the `Specific` interface, which is an empty interface.

Similar to the `ComponentClassProvider`, for the `SpecificClassProvider` we provide the `SpecificClassProviderImpl` implementation of the interface as a base implementation that you can use. It is based on a 1-1 mapping using a key-value map (one for each of the methods of the interface), where the key corresponds to the component useCaseTemplateId, and the value to the Specific class. It also provides an optional default class (one for each map) that, if set, is returned when the key-value map doesn't contain the received `useCaseTemplateId`.

```java
@Configuration
class ClassProviderConfiguration {
    @Bean
    public SpecificClassProvider specificClassProvider() {
      return SpecificClassProviderImpl.builder()
              .withReverseProvisionSpecificClass("myUseCaseTemplateId", MyReverseSpecific.class)
              .withSpecificClass("myUseCaseTemplateId", MySpecific.class)
              .withDefaultSpecificClass(Specific.class)
              .build();
    }
}
```

This example will configure specific classes both for the provision operations, and the reverse provision operations. It will map all components with their `useCaseTemplateId` equal to "myUseCaseTemplateId" to a `MySpecific` class for validate, provision, unprovision and update ACL operation, and to `MyReverseSpecific` class for reverse provision operations. This way, the components will be parsed using a class defined using the `ComponentClassProvider`, with their `specific` field equal to `MySpecific` class. All other components that don't match the "myUseCaseTemplateId" value, will default to their `specific` fields be parsed as the provided `Specific` class, since a default class was specified. For reverse provision operations, since a default class wasn't specified, your Tech Adapter will return an error for all reverse operation requests with a `useCaseTemplateId` different to the one specified.


### Business logic

To include the `ProvisionService` implementation onto the framework, a wrapper class called `ProvisionConfiguration` must be configured in order to set up the `ProvisionService` implementation for each of the kind of components your Tech Adapter will support. `ProvisionConfiguration` provides a builder with sensible defaults for the components you don't need to support in order to instantiate the class with your implementations of `ProvisionService` and then it should be injected as a Spring Bean. An example where we assume we have a class `DemoOutputPortProvisionService implements ProvisionService` which provides the business logic for provisioning components of kind `outputport` looks like the following:

```java
@Configuration
class DemoConfiguration {
    @Bean
    public ProvisionConfiguration provisionConfiguration() {
        return ProvisionConfiguration.builder()
                .outputPortProvisionService(new DemoOutputPortProvisionService())
                .build();
    }
}
```

The appropriate `ProvisionService` will be chosen according to the `Component` kind attribute, matching with `storage` for Storage Provision service, `workload` for Workload Provision service, and `outputport` for Output Port Provision service. For Reverse Provision operations, the kind attribute is fetched from the catalog info present on the reverse provisioning request. The kind value is expected to be under the `spec.mesh.kind` path as per the specification.

Non-configured provision services will return an error explaining to the user that the specific component is not supported by your Tech Adapter, so no need to implement your own services that return an error.

For business logic validation implemented with the `ComponentValidationService` interface, a wrapper class called `ValidationConfiguration` must be configured in the same fashion as the `ProvisionConfiguration`. An example where we assume we have a class `DemoOutputPortValidationService implements ProvisionService` which provides the business logic for validating components of kind `outputport` looks like the following:

```java
@Configuration
class DemoConfiguration {
    @Bean
    public ValidationConfiguration validationConfiguration() {
        return ValidationConfiguration.builder()
                .outputPortValidationService(new DemoOutputPortValidationService())
                .build();
    }
}
```

The appropriate `ValidationService` is chosen following the same logic as the provision service logic, using the `Component` kind attribute. Non configured validation services will return an error explaining to the user that the specific component is not supported by your Tech Adapter, so no need to implement your own services that return an error.

## Migrating from the old Java Scaffold project

Tech Adapters that have previously been created from the Java Scaffold project can easily migrate to use the Java Tech Adapter Framework, as both use Spring Boot, they follow the same version of the OpenAPI Specification, and the Framework model classes were created based on the Java scaffold.

The `api`, `common`, `controller`, `model`, `parser` and `service.validation`
  packages present on the Java Scaffold `it.agilelab.witboost.javascaffold` package become useless as their behaviour is abstracted on the Framework. The only exceptions are the `*Validation` classes which must be [migrated](#migrating-scaffold-component-validation-to-framework-componentvalidationservice) to implement the `ComponentValidationService` interface and injected on the framework as explained above.

Furthermore, you need to configure the `Main.java` class to include the framework classes on the Spring Boot scanning. See the [section above](#bootstrapping-your-tech-adapter) for more information.

### Migrating FailedOperation

`FailedOperation` is the class used by both the Scaffold and the Java Tech Adapter Framework. Indeed, the framework takes the Scaffold implementation as a base and built an improved version upon it. The main difference is the inclusion of a `message` attribute which maps to the `userMessage` field on a Witboost error, so it's intended to be shown to an end user, so it must be written on a user-friendly manner and not including technical details (which must be stored on the `problems` list attribute).

Furthermore, two optional fields are added to the `FailedOperation` class: `input` and `inputErrorField` which map to the Witboost error fields with the same name. Checkout the Witboost documentation for more information about these fields. 

### Implementing Class Providers

The Specific and Component class providers are two new additions to the framework not present on the Java Scaffold. These were introduced to support parsing of different component and `specific` or `params` object entities on the framework side instead of forcing the Tech Adapter developers to implement their own parsing of said objects onto each request. 

These class providers use the component's `useCaseTemplateId` attribute to link a particular component and/or its `specific` field to a Class (usually a POJO) to be used to parse a specific request. 

To implement this, take note of the `useCaseTemplateId`s your Tech Adapter supports and [configure](#class-providers) them. For 1-1 component mappings we provide the `ComponentClassProviderImpl.defaultComponentsImpl` method to provide an easy way to instantiate a Component class provider.

```java
@Configuration
class ClassProviderConfiguration {
    @Bean
    public ComponentClassProvider componentClassProvider() {
      return ComponentClassProviderImpl.defaultComponentsImpl(
              "storageUseCaseTemplateId", 
              "workloadUseCaseTemplateId", 
              "outputPortUseCaseTemplateId"
      );
    }
}
```

This example defines a `ComponentClassProvider` which supports parsing of all three default components: `StorageArea`, `Workload`, and `OutputPort`. Each of these component classes will be selected to parse the appropriate component whose `useCaseTemplateId` is equal to the one specified.

Please note regarding the `ComponentClassProvider` that if you use your own custom classes inheriting from `Component`, these **must** have one generic type parameter, as the injection of the class form the Specific field is done via the `SpecificClassProvider`, not by defining the type on the class declaration.

Also, for both Component and Specific class providers, we make available base implementations `ComponentClassProviderImpl` and `SpecificClassProviderImpl` with builder methods to easily instantiate these and use them on the framework. See the [section above](#class-providers) for more information.

### Migrating Scaffold Component Validation to Framework ComponentValidationService

The `*Validation` classes on the Java Scaffold define the following method:

```java
public Either<FailedOperation, Void> validate(DataProduct dataProduct, @Valid Component<? extends Specific> component);
```

This behaviour is transformed using the new `OperationRequest` model class, which encapsulates the Data Product and the Component objects. It also introduces an `OperationType` parameter to specify the operation being performed, as some Tech Adapters perform different validations depending on the operation being performed (e.g. validation v.s. provisioning). Thus, the interface to implement looks like the following:

```java
Either<FailedOperation, Void> validate(@Valid OperationRequest<?, ? extends Specific> operationRequest, OperationType operationType);
```

Please note that while the parameters are different, the return type is the same (using, of course, the new `FailedOperation` class provided by the framework which has [different signatures](#migrating-failedoperation) than the Scaffold one). Some things to take into account:

- `OperationRequest` stores the component as an `Optional<Component>` attribute, so before performing any validation, you need to validate that the component is present, that it's the appropriate component implementation, and that its `specific` field is of the appropriate implementation of the `Specific` class you defined on the [class provider interfaces](#class-providers).
- Depending on the type of operation, you might receive a specific type of `OperationRequest`. Currently, for provision and unprovision operations, a `ProvisionOperationRequest` is sent as parameter, and for update ACL operations a `AccessControlOperationRequest` is sent as parameter. If needed, you can cast the parameter to one of these class to leverage the extra functionalities they might provide.

### Migrating Scaffold ApiServiceImpl to Framework ProvisionService

On the Java Scaffold, the business logic is expected to be written on the `ApiServiceImpl` class, which depending on the Tech Adapter might contain the whole business logic, or only be used to parse, validate, and call other classes that actually perform the business logic. On the Framework, these two behaviours are detached, where the parsing and validation is done by the Framework itself, and the new `ProvisionService` interface already receives a `OperationRequest` with the descriptor and component to provision parsed following the classes provided on the [Class Providers](#class-providers).

Each `ProvisionService` implementation supports one kind of component and performs all operations supported for that kind of component. If your Tech Adapter supports more than one component with the same kind, both of these requests will be sent to the same [configured](#business-logic) `ProvisionService`, and is the business logic task to differentiate them based on the `Component` or `Specific` instance type.

The `ProvisionService` is expected to perform only synchronous tasks, leaving the asynchronous handling to the Framework itself. Thus, the interface is defined as follows:

```java
interface ProvisionService {
  Either<FailedOperation, ProvisionInfo> provision(ProvisionOperationRequest<?, ? extends Specific> operationRequest);
  Either<FailedOperation, ProvisionInfo> unprovision(ProvisionOperationRequest<?, ? extends Specific> operationRequest);
  Either<FailedOperation, ProvisionInfo> updateAcl(AccessControlOperationRequest<?, ? extends Specific> operationRequest);
  Either<FailedOperation, ReverseProvisionInfo> reverseProvision(ReverseProvisionOpRequest<? extends Specific> operationRequest);
}
```

Note that, depending on the type of operation, a different `OperationRequest` instance is sent as parameter, including the whole of the information needed to perform said operation. Some things to take into account:

- `*OperationRequest` signature stores the type using Java wildcards, so before performing any operation, you need to validate that the component is present and it's the appropriate component implementation (only for provision, unprovision and update ACL), and that its `specific` field is of the appropriate implementation of the `Specific` class you defined on the [class provider interfaces](#class-providers) (or the `params` field for reverse provision operations).
- Return types are used to return the information to be embedded on the response body of the operation. For provision, unprovision and update ACL these provide a way to send `publicInfo`, `privateInfo` and `logs`. Please note that the first two have been defined without any type restriction, but they must be objects which the Jackson library is capable of serializing into JSON (we recommend either POJOs or a Map).  