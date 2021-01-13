package com.ejc.sql.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

class EntityModelNode {
    private TypeElement entity;
    private ProcessingEnvironment processingEnvironment;

    EntityModelNode(TypeElement entity, ProcessingEnvironment processingEnvironment) {
        this.entity = entity;
        this.processingEnvironment = processingEnvironment;
    }

}
