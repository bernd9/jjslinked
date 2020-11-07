package com.ejc.api.context;

import lombok.Getter;

@Getter
class DependencySource {
    private ClassReference expectedType;

    private final SingletonProvider provider;

    DependencySource(SingletonProvider provider) {
        this.provider = provider;
        init();
    }

    private void init() {
        expectedType = provider.getType();
    }


}
