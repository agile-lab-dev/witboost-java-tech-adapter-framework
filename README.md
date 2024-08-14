<p align="center">
    <a href="https://www.agilelab.it/witboost">
        <img src="docs/img/witboost_logo.svg" alt="witboost" width=600 >
    </a>
</p>

Designed by [Agile Lab](https://www.agilelab.it/), Witboost is a versatile platform that addresses a wide range of sophisticated data engineering challenges. It enables businesses to discover, enhance, and productize their data, fostering the creation of automated data platforms that adhere to the highest standards of data governance. Want to know more about Witboost? Check it out [here](https://www.witboost.com) or [contact us!](https://www.witboost.com/contact-us).

This repository is part of our [Starter Kit](https://github.com/agile-lab-dev/witboost-starter-kit) meant to showcase Witboost integration capabilities and provide a "batteries-included" product.

# Java Tech Adapter Framework

- [Overview](#overview)
- [Using this library](#using-this-library)
- [Building](#building)
- [API specification](docs/API.md)
- [License](#license)


## Overview

The Java Tech Adapter Framework is a Spring Boot based framework to streamline the experience of creating Witboost tech adapters by standardizing common features, namely the API layer, descriptor parsing, base configuration, etc. This also allows to introduce new features and fix issues faster and without the need to propagate the feature or fix to all developed Java tech adapters.

### What's a Tech Adapter?

A Tech Adapter is a microservice which is in charge of deploying components that use a specific technology. When the deployment of a Data Product is triggered, the platform generates it descriptor and orchestrates the deployment of every component contained in the Data Product. For every such component the platform knows which Tech Adapter is responsible for its deployment, and can thus send a provisioning request with the descriptor to it so that the Tech Adapter can perform whatever operation is required to fulfill this request and report back the outcome to the platform.

You can learn more about how the Tech Adapters fit in the broader picture [here](https://docs.witboost.com/docs/p2_arch/p1_intro/#deploy-flow).

### Software stack

This library is written in Java 17, using SpringBoot for the HTTP layer. Project is built and publish via Apache Maven Central, consisting of two modules:

- `java-tech-adapter-framework-model`: contains the framework model, including classes representing descriptor, components, request and response classes, error handling, and more. It also includes Jackson parsing utilities.
- `java-tech-adapter-framework-core`: core library containing the provisioning flow, including the API layer, input validation and where the interfaces to be implemented are defined.

### Git hooks

Hooks are programs you can place in a hooks directory to trigger actions at certain points in git’s execution. Hooks that don’t have the executable bit set are ignored.

The hooks are all stored in the hooks subdirectory of the Git directory. In most projects, that’s `.git/hooks`.

Out of the many available hooks supported by Git, we use `pre-commit` hook in order to check the code changes before each commit. If the hook returns a non-zero exit status, the commit is aborted.


#### Setup Pre-commit hooks

In order to use `pre-commit` hook, you can use [**pre-commit**](https://pre-commit.com/) framework to set up and manage multi-language pre-commit hooks.

To set up pre-commit hooks, follow the below steps:

- Install pre-commit framework either using pip (or) using homebrew (if your Operating System is macOS):

    - Using pip:
      ```bash
      pip install pre-commit
      ```
    - Using homebrew:
      ```bash
      brew install pre-commit
      ```

- Once pre-commit is installed, you can execute the following:

```bash
pre-commit --version
```

If you see something like `pre-commit 3.3.3`, your installation is ready to use!


- To use pre-commit, create a file named `.pre-commit-config.yaml` inside the project directory. This file tells pre-commit which hooks needed to be installed based on your inputs. Below is an example configuration:

```bash
repos:
  - repo: https://github.com/pre-commit/pre-commit-hooks
    rev: v4.4.0
    hooks:
      - id: trailing-whitespace
```

The above configuration says to download the `pre-commit-hooks` project and run its trailing-whitespace hook on the project.


- Run the below command to install pre-commit into your git hooks. pre-commit will then run on every commit.

```bash
pre-commit install
```

## Using this library

Add the following dependency on your `pom.xml`:

```xml
<dependency>
    <groupId>com.witboost.provisioning</groupId>
    <artifactId>java-tech-adapter-framework-core</artifactId>
    <version>${env.FRAMEWORK_VERSION}</version>
</dependency>
```

Where `${env.FRAMEWORK_VERSION}` is the library version.

To be able to use Spring Boot, you need to set Spring Boot 2.3.2 as a parent on your `pom.xml` and setup the actuator dependencies:

```xml
<project ...>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Implementing server logic

The Java Tech Adapter Framework provides four interfaces to be implemented in order to plug your business logic into the provisioning workflow. These are:

`ProvisionService`: Provides the business logic for component provision, unprovision, update access control, and reverse provisioning.
`ComponentValidationService`: Provides the business logic for component validation, executed for validation and (un)provisioning operations.
`ComponentClassProvider`: Interface that maps a component's `useCaseTemplateId` with a Class that represents the Component model, allowing to use extensions of the provided Components.
`SpecificClassProvider`: Interface that maps a component's `useCaseTemplateId` with a Class that represents the Specific model.

As of the current version, all four interfaces are mandatory, providing an failure default implementation for each method. To understand the details to implement these interfaces, check the guide. 

## Building

**Requirements:**

- Java 17
- Apache Maven 3.9+

**Version:** the version is set dynamically via an environment variable, `FRAMEWORK_VERSION`. Make sure you have it exported, even for local development. Example:

```bash
export FRAMEWORK_VERSION=0.0.0-SNAPHSOT
```

**Build:**

The scaffold uses the `openapi-generator` Maven plugin to generate the API endpoints from the interface specification located in `core/src/main/resources/interface-specification.yml`. For more information on the documentation, check [API docs](docs/API.md).

```bash
mvn compile
```

**Type check:** is handled by Checkstyle:

```bash
mvn checkstyle:check
```

**Bug checks:** are handled by SpotBugs:

```bash
mvn spotbugs:check
```

**Tests:** are handled by JUnit:

```bash
mvn test
```

**Artifacts & Docker image:** the project leverages Maven for packaging. Build artifacts (normal and fat jar) with:

```bash
mvn package spring-boot:repackage
```

The Docker image can be built with:

```bash
docker build .
```

More details can be found [here](docs/docker.md).

*Note:* when running in the CI/CD pipeline the version for the project is automatically computed using information gathered from Git, using branch name and tags. Unless you are on a release branch `1.2.x` or a tag `v1.2.3` it will end up being `0.0.0`. You can follow this branch/tag convention or update the version computation to match your preferred strategy. When running locally if you do not care about the version (ie, nothing gets published or similar) you can manually set the environment variable `FRAMEWORK_VERSION` to avoid warnings and oddly-named artifacts; as an example you can set it to the build time like this:
```bash
export FRAMEWORK_VERSION=$(date +%Y%m%d-%H%M%S);
```

**CI/CD:** the pipeline is based on GitLab CI as that's what we use internally. It's configured by the `.gitlab-ci.yaml` file in the root of the repository. You can use that as a starting point for your customizations.

## License

This project is available under the [Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0); see [LICENSE](LICENSE) for full details.

## About us

<p align="center">
    <a href="https://www.agilelab.it">
        <img src="docs/img/agilelab_logo.svg" alt="Agile Lab" width=600>
    </a>
</p>

Agile Lab creates value for its Clients in data-intensive environments through customizable solutions to establish performance driven processes, sustainable architectures, and automated platforms driven by data governance best practices.

Since 2014 we have implemented 100+ successful Elite Data Engineering initiatives and used that experience to create Witboost: a technology-agnostic, modular platform, that empowers modern enterprises to discover, elevate and productize their data both in traditional environments and on fully compliant Data mesh architectures.

[Contact us](https://www.agilelab.it/contacts) or follow us on:
- [LinkedIn](https://www.linkedin.com/company/agile-lab/)
- [Instagram](https://www.instagram.com/agilelab_official/)
- [YouTube](https://www.youtube.com/channel/UCTWdhr7_4JmZIpZFhMdLzAA)
- [Twitter](https://twitter.com/agile__lab)
