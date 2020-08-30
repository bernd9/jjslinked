package com.ejc.processor;

import com.ejc.ApplicationContext;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextImpl extends ApplicationContext {

    @Getter
    private final Set<Object> beans = new HashSet<>();

    public ApplicationContextImpl() {
        beans.add(this);
    }

    public ApplicationContextImpl(Set<Object> beansToAdd) {
        this();
        beans.addAll(beansToAdd);
    }


    public <T> void replaceBean(Class<T> t, T bean) {
        try {
            T old = getBean(t);
            beans.remove(old);
            beans.add(bean);
        } catch (IllegalArgumentException e) {
            // We accept bean is not found
        }
    }

    void addBean(Object bean) {
        beans.add(bean);
    }

    void addBeans(Collection<Object> beans) {
        beans.addAll(beans);
    }

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
