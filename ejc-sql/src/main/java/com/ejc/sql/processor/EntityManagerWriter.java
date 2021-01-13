package com.ejc.sql.processor;

import com.ejc.javapoet.JavaWriter;
import com.ejc.sql.api.entity.EntityProxy;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.util.Optional;

import static java.util.Collections.singleton;

class EntityManagerWriter extends JavaWriter {
    private final EntityModel entityModel;
    private static final String FIELD_NAME_INSERT = "INSERT";
    private static final String FIELD_NAME_UPDATE = "UPDATE";
    private static final String FIELD_NAME_DELETE = "DELETE";

    EntityManagerWriter(String simpleName, Optional<String> packageName, Optional<TypeName> superClass, ProcessingEnvironment processingEnvironment, EntityModel entityModel) {
        super(simpleName, packageName, superClass, processingEnvironment, singleton(EntityProxy.class));
        this.entityModel = entityModel;
    }

    @Override
    protected void writeTypeBody(TypeSpec.Builder builder) {
        writeInsertStatementConstantField(builder);
        writeUpdateStatementConstantField(builder);
        writeDeleteStatementConstantField(builder);
    }

    private void writeInsertStatementConstantField(TypeSpec.Builder builder) {

    }

    private void writeUpdateStatementConstantField(TypeSpec.Builder builder) {

    }

    private void writeDeleteStatementConstantField(TypeSpec.Builder builder) {
        //builder.addField(TypeName.get(String.class), FIELD_NAME_DELETE, )
    }


    @Override
    protected void writeConstructor(MethodSpec.Builder constructorBuilder) {

    }


    private static CodeBlock createSuperMethodCall(ExecutableElement method) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if (method.getReturnType().getKind() != TypeKind.VOID) {
            codeBlockBuilder.add("return ");
        }
        codeBlockBuilder
                .add("super.$L(", method.getSimpleName())
                .add(JavaModelUtils.parameterNameList(method))
                .add(")");
        return codeBlockBuilder.build();
    }

}
