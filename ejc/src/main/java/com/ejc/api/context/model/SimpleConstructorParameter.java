package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SimpleConstructorParameter implements ConstructorParameter, SingletonCreationListener {
    private final ClassReference type;
    private final SingletonConstructor constructor;
    private Object value;
    private boolean satisfied;
    private SingletonCreationEventBus bus;

    @Override
    public void setEventBus(SingletonCreationEventBus bus) {
        this.bus = bus;
        bus.subscribe(this::onSingletonCreated);
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (type.isInstance(o)) {
            this.value = o;
            bus.unsubscribe(this::onSingletonCreated);
            constructor.onParameterSatisfied();
        }
    }
}
