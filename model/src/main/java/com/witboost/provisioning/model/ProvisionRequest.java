package com.witboost.provisioning.model;

public record ProvisionRequest<T>(DataProduct dataProduct, Component<T> component, Boolean removeData) {}
