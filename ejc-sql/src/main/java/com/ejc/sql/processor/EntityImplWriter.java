package com.ejc.sql.processor;

import com.ejc.javapoet.JavaWriter;
import com.ejc.sql.api.entity.EntityProxy;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Optional;

import static java.util.Collections.singleton;

class EntityImplWriter extends JavaWriter {

    private EntityModel entityModel;

    EntityImplWriter(String simpleName, Optional<String> packageName, Optional<TypeName> superClass, ProcessingEnvironment processingEnvironment, EntityModel entityModel) {
        super(simpleName, packageName, superClass, processingEnvironment, singleton(EntityProxy.class));
        this.entityModel = entityModel;
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {
        //entityModel.getSimpleFields().forEach();
    }
}
