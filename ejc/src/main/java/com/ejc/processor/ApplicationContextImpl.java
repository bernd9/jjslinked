package com.ejc.processor;

import com.ejc.ApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContextImpl extends ApplicationContext {

    private final Set<Object> beans = new HashSet<>();

    public ApplicationContextImpl() {
        beans.add(this);
    }

    void addBean(Object bean) {
        beans.add(bean);
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
        try {
            return (T) getBean(BeanUtils.classForName(c));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Set<T> getBeans(Class<T> c) {
        return beans.stream()
                .filter(e -> c.isAssignableFrom(e.getClass()))
                .map(c::cast)
                .collect(Collectors.toSet());
    }

}
