package com.ejc.api.context;

import com.ejc.ApplicationContext;
import com.ejc.api.context.model.SingletonCreationEventBus;
import com.ejc.api.context.model.Singletons;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ApplicationContextInitializer {
    private final Singletons actualSingletons;
    private final Set<Singletons> modules;
    private ApplicationContext applicationContext;
    private final SingletonCreationEventBus eventBus = new SingletonCreationEventBus();

    public void initialize() {
        // TODO
        eventBus.subscribe(this::singletonCreated);
    }

    void singletonCreated(Object singleton) {

    }

    private void replacementPostProcess() {

    }
}
