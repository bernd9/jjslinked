package com.jjslinked.spring;

import com.ejaf.processor.InternalEvents;
import com.ejaf.processor.parameter.ParameterProviderCreatedEvent;
import com.ejaf.processor.parameter.ParameterProvidersProcessedEvent;
import org.springframework.stereotype.Component;

@Component
class ParameterProviderConfigWriter {

    ParameterProviderConfigWriter() {
        InternalEvents.onEvent(this::parameterProviderCreated, ParameterProviderCreatedEvent.class);
        InternalEvents.onEvent(this::parameterProvidersProcessed, ParameterProvidersProcessedEvent.class);
    }

    private void parameterProviderCreated(ParameterProviderCreatedEvent e) {

    }

    private void parameterProvidersProcessed(ParameterProvidersProcessedEvent e) {

    }

}
