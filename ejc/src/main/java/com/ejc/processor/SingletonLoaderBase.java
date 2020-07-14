package com.ejc.processor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SingletonLoaderBase {

    private final String classname;

    Object load() {
        return InstanceUtils.createInstance(classname);
    }
}
