package com.ejc.api.context.model;

import java.util.function.Consumer;

// TODO refactoring -> package-protected for model herer
public class SingletonCreationEvents {

    public void singletonCreated(Object o) {
    }

    public void subscribe(Consumer<SingletonModel> listener) {

    }


    public void unsubscribe(Consumer<Object> listener) {

    }

}
