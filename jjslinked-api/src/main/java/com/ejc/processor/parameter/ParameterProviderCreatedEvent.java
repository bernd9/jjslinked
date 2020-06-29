package com.ejc.processor.parameter;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class ParameterProviderCreatedEvent {
    ParameterProviderRegistryModel model;
}
