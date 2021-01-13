package com.ejc.sql.processor;

import com.ejc.javapoet.JavaWriter;
import com.ejc.sql.Repository;
import com.ejc.sql.api.DaoImpl;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collections;
import java.util.Optional;

class RepositoryWriter extends JavaWriter {

    private final EntityModel entityModel;

    RepositoryWriter(String simpleName, Optional<String> packageName, ProcessingEnvironment processingEnvironment, EntityModel entityModel) {
        super(simpleName, packageName, Optional.of(TypeName.get(DaoImpl.class)), processingEnvironment, Collections.singleton(Repository.class));
        this.entityModel = entityModel;
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {
        super.writeTypeBody(builder);
    }
}
