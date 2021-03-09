package one.xis.sql.processor;

import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;

@RequiredArgsConstructor
class EntityUtilWriter {
    private final EntityUtilModel entityUtilModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(ClassName.OBJECT)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT) // TODO remove abstract
                .addOriginatingElement(entityUtilModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(entityUtilModel.getEntityModel().getPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();

        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        createContructor(builder);
        builder.addMethod(implementCopyAttributes());
    }

    private MethodSpec implementCopyAttributes() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("copyAttributes")
                .addParameter(entityUtilModel.getEntityModel().getTypeName(), "source")
                .addParameter(entityUtilModel.getEntityModel().getTypeName(), "target");
        for (FieldModel fieldModel : entityUtilModel.getAllFields()) {

        }
        return builder.build();
    }

    private void createContructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());
    }

}

@RequiredArgsConstructor
class SetFieldValueCodeSimple {
    private final String entityVarName;
    private final FieldModel fieldModel;

    CodeBlock createCode(CodeBlock valueSupplierCode) {
        return fieldModel.getSetter().map(setter -> useSetter(setter, valueSupplierCode))
                .orElseGet(() -> useFieldUtils(valueSupplierCode));
    }

    private CodeBlock useSetter(ExecutableElement setter, CodeBlock valueSupplierCode) {
        return CodeBlock.builder()
                .addStatement("$L.$L($L)", entityVarName, setter.getSimpleName(), valueSupplierCode)
                .build();
    }

    private CodeBlock useFieldUtils(CodeBlock valueSupplierCode) {
        return CodeBlock.builder()
                .addStatement("$T.setFieldValue($L, \"$L\", $L)", entityVarName, fieldModel.getFieldName(), valueSupplierCode)
                .build();
    }
}


@RequiredArgsConstructor
class GetFieldValueCodeSimple {
    private final String entityVarName;
    private final FieldModel fieldModel;

    CodeBlock createCode() {
        return fieldModel.getGetter().map(this::useGetter).orElseGet(this::useFieldUtils);
    }

    private CodeBlock useGetter(ExecutableElement getter) {
        return CodeBlock.builder()
                .addStatement("$L.$L()", entityVarName, getter.getSimpleName())
                .build();
    }

    private CodeBlock useFieldUtils() {
        return CodeBlock.builder()
                .addStatement("$T.getFieldValue($L, \"$L\")", entityVarName, fieldModel.getFieldName())
                .build();
    }
}
