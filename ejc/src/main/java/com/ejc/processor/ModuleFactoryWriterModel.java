package com.ejc.processor;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
class ModuleFactoryWriterModel {

    private final Set<SingletonElement> singletonElements;
    private final Map<TypeElement, TypeElement> classReplacements;
    private final String applicationClass;
}
