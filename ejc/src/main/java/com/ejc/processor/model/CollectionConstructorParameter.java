package com.ejc.processor.model;

import com.ejc.api.context.ClassReference;
import com.ejc.util.ClassUtils;
import lombok.Data;

import java.util.Collection;

@Data
public class CollectionConstructorParameter implements ConstructorParameter, SingletonCreationListener {
    private final Class<? extends Collection> collectionType;
    private final ClassReference genericType;
    private final Collection<Object> value;
    private SingletonCreationEventBus bus;

    public CollectionConstructorParameter(Class<? extends Collection> collectionType, ClassReference genericType) {
        this.collectionType = collectionType;
        this.genericType = genericType;
        this.value = ClassUtils.createInstance(collectionType);
    }

    @Override
    public void setEventBus(SingletonCreationEventBus bus) {
        this.bus = bus;
        bus.subscribe(this::onSingletonCreated);
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public void onSingletonCreated(Object o) {
        if (genericType.isInstance(o)) {
            value.add(o);
        }
    }
}
