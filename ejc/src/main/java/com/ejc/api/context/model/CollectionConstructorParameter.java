package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import com.ejc.util.ClassUtils;
import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
public class CollectionConstructorParameter implements ConstructorParameter, SingletonCreationListener {
    private final Class<? extends Collection> collectionType;
    private final ClassReference genericType;
    private final Collection<Object> value;
    private SingletonCreationEvents events;

    public CollectionConstructorParameter(Class<? extends Collection> collectionType, ClassReference genericType) {
        this.collectionType = collectionType;
        this.genericType = genericType;
        this.value = ClassUtils.createInstance(collectionType);
    }

    public void setAllSingletonTypes(Set<Class<?>> types) {

    }

    @Override
    public void setEvents(SingletonCreationEvents events) {
        this.events = events;
        // events.subscribe(this::onSingletonCreated);
    }

    @Override
    public Class<?> getType() {
        return collectionType;
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
