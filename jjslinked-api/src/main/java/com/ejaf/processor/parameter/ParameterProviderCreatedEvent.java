package com.ejaf.processor.parameter;

import lombok.Getter;
import lombok.Value;

@Getter
@Value
public class ParameterProviderCreatedEvent {
    ParameterProviderModel model;
}
