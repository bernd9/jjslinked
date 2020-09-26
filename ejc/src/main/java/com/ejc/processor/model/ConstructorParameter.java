package com.ejc.processor.model;

import com.ejc.api.context.ClassReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ConstructorParameter {
    private final ClassReference type;
    private final SingletonConstructor constructor;
    private Object value;
    private boolean satisfied;
    private SingletonCreationEventBus bus;

    void setEventBus(SingletonCreationEventBus bus) {
        this.bus = bus;
        bus.subscribe(this::singletonCreated);
    }

    void singletonCreated(Object o) {
        if (type.isInstance(o)) {
            this.value = o;
            bus.unsubscribe(this::singletonCreated);
            constructor.onParameterSatisfied();
        }
    }


}
