package com.jjslink.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;


class TypeElements {
    private final ProcessingEnvironment environment;

    TypeElements(ProcessingEnvironment environment) {
        this.environment = environment;
    }

    TypeElement getByName(String name) {
        return environment.getElementUtils().getTypeElement(name);
    }
}
