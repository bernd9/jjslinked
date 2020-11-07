package com.ejc.context2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class SingletonCreationContext {
    private final SingletonProviders singletonProviders = new SingletonProviders();
    private final SingletonEvents singletonEvents = new SingletonEvents();
}
