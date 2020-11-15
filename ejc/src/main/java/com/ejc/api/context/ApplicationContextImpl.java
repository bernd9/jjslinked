package com.ejc.api.context;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ApplicationContextImpl extends ApplicationContext {

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Set<Object> beans;

    @Override
    public <T> T getBean(Class<T> c) {
        List<Object> result = new ArrayList<>(getBeans(c));
        switch (result.size()) {
            case 0:
                throw new IllegalArgumentException("no bean of type " + c.getName());
            case 1:
                return (T) result.get(0);
            default:
                throw new IllegalStateException("not unique: " + c.getName());
        }
    }

    @Override
    public <T> T getBean(String c) {
        List<Object> result = new ArrayList<>(getBeans(c));
        switch (result.size()) {
            case 0:
                throw new IllegalArgumentException("no bean of type " + c);
            case 1:
                return (T) result.get(0);
            default:
                throw new IllegalStateException("not unique: " + c);
        }
    }

    @Override
    public <T> Set<T> getBeans(Class<T> c) {
        return beans.stream()
                .filter(e -> c.isAssignableFrom(e.getClass()))
                .map(c::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public <T> Set<T> getBeans(String c) {
        return beans.stream()
                .filter(e -> e.getClass().getName().equals(c))
                .map(e -> (T) e)
                .collect(Collectors.toSet());
    }

}
