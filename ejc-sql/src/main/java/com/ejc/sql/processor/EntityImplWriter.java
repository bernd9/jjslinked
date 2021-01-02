package com.ejc.sql.processor;

import com.ejc.javapoet.JavaWriter;
import com.ejc.sql.api.entity.EntityProxy;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.Optional;

import static java.util.Collections.singleton;

class EntityImplWriter extends JavaWriter {

    private final EntityModel entityModel;

    EntityImplWriter(String simpleName, Optional<String> packageName, Optional<TypeName> superClass, ProcessingEnvironment processingEnvironment, EntityModel entityModel) {
        super(simpleName, packageName, superClass, processingEnvironment, singleton(EntityProxy.class));
        this.entityModel = entityModel;
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {
        writeWrappedEntityField(builder);
    }


    private void writeWrappedEntityField(TypeSpec.Builder builder) {

    }

    private void writeSimpleFieldSetters(TypeSpec.Builder builder) {
        this.entityModel.getSimpleFields()
                .stream()
                .filter(entityField -> entityField.getSetter().isPresent())
                .forEach(entityField -> writeSimpleFieldSetter(entityField.getSetter().get(), builder));
    }

    private void writeSimpleFieldSetter(ExecutableElement setter, TypeSpec.Builder builder) {
        //MethodSpec.overriding(setter)
        //      .addStatement()
    }


}
