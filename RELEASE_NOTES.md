# Changelog

All notable changes to this project will be documented in this file.

## v1.1.0-2.2.0

### Commits

- **Resolve WIT-4091 "Java tech adapter framework tomcat vulnerability"**
  > 
  > ##### New features and improvements
  > 
  > * Updated Spring Boot to version 3.4.4 to fix CVE-2025-24813
  > * Update dependency check plugin to 12.1.0
  > * Enables automatic publishing to Maven Central after artifact upload
  > 
  > ##### Related issue
  > 
  > Closes WIT-4091
  > 
  > 

- **Updated README**

## v1.0.0-2.2.0 - 2024-09-12

### Commits

- **[WIT-3064] Tech Adapter Framework autoconfiguration is not set up correctly**
  > 
  > ##### Bug fixes
  > 
  > * Fixes a bug where autoconfiguration would not be scanned by Spring Boot following the recommended usage
  > 
  > ##### Related issue
  > 
  > Closes WIT-3064
  > 
  > 

- **[WIT-2944] Publish java tech adapter framework to maven central**
  > 
  > ##### Bug fixes
  > 
  > Fixes on Central publish
  > 
  > ##### Related issue
  > 
  > Closes WIT-2944
  > 
  > 

- **[WIT-2944] Publish java tech adapter framework to maven central**
  > 
  > ##### New features and improvements
  > 
  > * Added Maven profile to publish artifacts on Sonatype Central with all necessary plugins
  > * Reintroduces the publish step on the CI
  > 
  > ##### Related issue
  > 
  > Closes WIT-2944
  > 
  > 

- **[WIT-2983] Add support for data contract and tags on java framework**
  > 
  > ##### New features and improvements
  > 
  > * Introduces Data Contract and Tag model classes to better parse the input entities
  > * Introduces the field `additionalProperties` to most model classes to store the non-parsed fields (e.g. custom fields added to a certain base Component)
  > 
  > ##### Related issue
  > 
  > Closes WIT-2983
  > 
  > 

- **[WIT-2829] Implement the sync java tech adapter framework**
  > 
  > ##### New features and improvements
  > 
  > * Implements support for synchronous validation, provisioning, unprovisioning, update ACL and reverse provisioning on the framework
  > * Provides autoconfiguration for ProvisionConfiguration and ValidationConfiguration classes with a failure behaviour, overridable by the user.
  > * Provides `ComponentClassProviderImpl` and `SpecificClassProviderImpl` as base implementations of their interfaces to easily bootstrap simple use-cases Tech Adapters.
  > * Provides a guide on how to bootstrap a brand new Tech Adapter, and how to migrate existing Tech Adapters that are based on the old Java Scaffold project.
  > 
  > ##### Related issue
  > 
  > Closes WIT-2829
  > 
  > 

- **[WIT-2828] Bootstrap Java Tech Adapter Framework**
  > 
  > ##### New features and improvements
  > 
  > * Bootstraps the Framework by using the Java Scaffold code
  > * Separates the project into two modules, core and model
  > 
  > ##### Related issue
  > 
  > Closes WIT-2828
  > 
  > 

- **[WIT-2827] Java Tech Adapter Framework LLD**
  > 
  > ##### New features and improvements
  > 
  > * Adds the Low Level Design documentation
  > 
  > ##### Related issue
  > 
  > Closes WIT-2827
  > 
  > 

- **[WIT-2826] Java Tech Adapter Framework HLD**
  > 
  > ##### New features and improvements
  > 
  > * Added framework HLD
  > 
  > ##### Related issue
  > 
  > Closes WIT-2826
  > 
  > 
