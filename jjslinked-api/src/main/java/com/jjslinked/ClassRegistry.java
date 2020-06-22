package com.jjslinked;

import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

public class ClassRegistry {

    @Delegate
    private final Map<String, Class<?>> classesMap = new HashMap<>();

}
