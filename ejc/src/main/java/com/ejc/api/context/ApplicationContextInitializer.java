package com.ejc.api.context;

import com.ejc.ApplicationContext;
import com.ejc.api.context.model.SingletonCreationEvents;
import com.ejc.api.context.model.SingletonModel;
import com.ejc.api.context.model.Singletons;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationContextInitializer {
    private final Set<Singletons> modules;
    private ApplicationContext applicationContext;
    private final SingletonCreationEvents events = new SingletonCreationEvents();
    private Map<ClassReference, SingletonModel> singletonModels;

    public void initialize() {
        singletonModels = new HashMap<>(modules.stream()
                .map(Singletons::getSingletonModels)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(SingletonModel::getType, Functions.identity())));
        doReplacement();
        bindEvents();
        startInitializationQueue();
    }


    void doReplacement() {
        SingletonReplacer singletonReplacer = new SingletonReplacer(singletonModels);
        singletonReplacer.doReplacement();
    }

    private void bindEvents() {
        singletonModels.values().forEach(model -> model.bindEvents(events));
    }

    private void startInitializationQueue() {
        singletonModels.values().stream()
                .filter(SingletonModel::isCreatable)
                .forEach(SingletonModel::create);
    }
}


