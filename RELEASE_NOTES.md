# Changelog

All notable changes to this project will be documented in this file.

## v2.2.0

### Commits

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

- **Add README**
