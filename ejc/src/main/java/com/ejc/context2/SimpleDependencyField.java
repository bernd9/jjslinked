package com.ejc.context2;

public class SimpleDependencyField implements SingletonCreationListener {

    @Override
    public void onSingletonCreated(Object o) {

    }

    void setFieldValue(Object owner) {

    }

    boolean isSatisfied() {
        return true;
    }
}
