package com.ejc.api.context;

import com.ejc.ApplicationContext;
import com.ejc.api.context.model.SingletonCreationEventBus;
import com.ejc.api.context.model.SingletonModel;
import com.ejc.api.context.model.Singletons;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextInitializer {
    private final Set<Singletons> modules;
    private ApplicationContext applicationContext;
    private final SingletonCreationEventBus eventBus = new SingletonCreationEventBus();

    public void initialize() {
        // TODO
        eventBus.subscribe(this::singletonCreated);
        Set<SingletonModel> singletonModels = modules.stream()
                .map(Singletons::getSingletonModels)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
// TODO FILTER replacers
        
        singletonModels.stream()
                .filter(SingletonModel::isCreatable)
                .forEach(SingletonModel::create);
    }

    void singletonCreated(Object singleton) {

    }

    private void replacementPostProcess() {

    }
}
