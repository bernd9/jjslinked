package com.ejc.processor;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
class ModuleWriterModel {

    private final Set<SingletonElement> singletonElements;
    private final String applicationClass;
}
