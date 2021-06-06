package one.xis.processor;

import one.xis.util.FieldUtils;
import com.squareup.javapoet.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityStatements;
import one.xis.sql.api.JdbcStatement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
class EntityStatementsWriter {

    private final EntityStatementsModel statementsModel;
    private final ProcessingEnvironment processingEnvironment;

    void write() throws IOException {
        TypeSpec.Builder builder = TypeSpec.classBuilder(statementsModel.getEntityStatementsSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityStatements.class),
                        entityTypeName(), entityIdTypeName()))
                .addOriginatingElement(statementsModel.getEntityModel().getType());

        writeTypeBody(builder);
        TypeSpec typeSpec = builder.build();

        JavaFile javaFile = JavaFile.builder(statementsModel.getEntityStatementsPackageName(), typeSpec)
                .skipJavaLangImports(true)
                .build();
        //System.out.println(javaFile.toString());
        javaFile.writeTo(processingEnvironment.getFiler());
    }

    private void writeTypeBody(TypeSpec.Builder builder) {
        implementMethods(builder);
    }

    private void implementMethods(TypeSpec.Builder builder) {
        builder.addMethod(implementGetInsertSql());
        builder.addMethod(implementGetSelectByIdSql());
        builder.addMethod(implementGetUpdateSql());
        builder.addMethod(implementGetDeleteSql());
        builder.addMethod(implementGetSelectAllSql());
        builder.addMethod(implementGetDeleteAllSql());
        builder.addMethod(implementGetUpdateColumnValuesToNullSql());
        builder.addMethod(implementGetSelectByColumnValueSql());
        builder.addMethod(implementGetCrossTableSelectSql());
        builder.addMethod(implementSetInsertSqlParameters());
        builder.addMethod(implementSetUpdateSqlParameters());
    }


    private MethodSpec implementGetInsertSql() {
        return implementSimpleStringGetter("getInsertSql", statementsModel.getInsertSql());
    }

    private MethodSpec implementGetSelectByIdSql() {
        return implementSimpleStringGetter("getSelectByIdSql", statementsModel.getSelectByIdSql());
    }

    private MethodSpec implementGetUpdateSql() {
        return implementSimpleStringGetter("getUpdateSql", statementsModel.getUpdateSql());
    }

    private MethodSpec implementGetDeleteSql() {
        return implementSimpleStringGetter("getDeleteSql", statementsModel.getDeleteSql());
    }

    private MethodSpec implementGetSelectAllSql() {
        return implementSimpleStringGetter("getSelectAllSql", statementsModel.getSelectAllSql());
    }

    private MethodSpec implementGetDeleteAllSql() {
        return implementSimpleStringGetter("getDeleteAllSql", statementsModel.getDeleteAllSql());
    }

    private MethodSpec implementGetUpdateColumnValuesToNullSql() {
        return MethodSpec.methodBuilder("getUpdateColumnValuesToNullByPkSql")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(String.class), "columnName")
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addStatement("return String.format(\"$L\", columnName)", statementsModel.getUpdateColumnValuesToNullByPkSqlPattern())
                .build();
    }

    private MethodSpec implementGetSelectByColumnValueSql() {
        return MethodSpec.methodBuilder("getSelectByColumnValueSql")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(String.class), "columnName")
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addStatement("return String.format(\"$L\", columnName)", statementsModel.getSelectByColumnValueSqlPattern())
                .build();
    }


    private MethodSpec implementGetCrossTableSelectSql() {
        return MethodSpec.methodBuilder("getCrossTableSelectSql")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(String.class), "crossTableName")
                .addParameter(ClassName.get(String.class), "entityTableRef")
                .addParameter(ClassName.get(String.class), "foreignTableRef")
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addCode("return new $T()\n", StringBuilder.class)
                .addCode(".append(\"SELECT \")\n")
                .addCode(".append(\"$L\")\n", statementsModel.getSelectSqlFieldList())
                .addCode(".append(\" FROM \")\n")
                .addCode(".append(\"$L\")\n", statementsModel.getEntityModel().getTableName())
                .addCode(".append(\" JOIN \")\n")
                .addCode(".append(crossTableName)\n")
                .addCode(".append(\" ON (\")\n")
                .addCode(".append(crossTableName)\n")
                .addCode(".append(\".\")\n")
                .addCode(".append(entityTableRef)\n")
                .addCode(".append(\"=$L\")\n", String.format("%s.%s", statementsModel.getEntityModel().getTableName(), statementsModel.getEntityModel().getIdField().getColumnName()))
                .addCode(".append(\") WHERE \")\n")
                .addCode(".append(crossTableName)\n")
                .addCode(".append(\".\")\n")
                .addCode(".append(foreignTableRef)\n")
                .addCode(".append(\"=?\")\n")
                .addCode(".toString();\n")
                .build();
    }

    private MethodSpec implementSimpleStringGetter(String methodName, String returnValue) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.get(String.class))
                .addStatement("return \"$L\"", returnValue)
                .build();
    }

    private MethodSpec implementSetInsertSqlParameters() {
        return MethodSpec.methodBuilder("setInsertSqlParameters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(TypeName.get(JdbcStatement.class), "st").build())
                .addParameter(ParameterSpec.builder(TypeName.get(statementsModel.getEntityModel().getType().asType()), "entity").build())
                .addCode(createParametersCodeBlock(statementsModel.getInsertSqlFields()))
                .build();
    }

    private MethodSpec implementSetUpdateSqlParameters() {
        return MethodSpec.methodBuilder("setUpdateSqlParameters")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(TypeName.get(JdbcStatement.class), "st").build())
                .addParameter(ParameterSpec.builder(TypeName.get(statementsModel.getEntityModel().getType().asType()), "entity").build())
                .addCode(createParametersCodeBlock(statementsModel.getUpdateSqlFields()))
                .build();
    }

    private CodeBlock createParametersCodeBlock(List<FieldModel> fieldModels) {
        CodeBlock.Builder builder = CodeBlock.builder();
        int paramIndex = 1;
        for (FieldModel fieldModel : fieldModels) {
            ColumnValueExtractorCodeFactory<?> extractorCodeFactory = null;
            if (fieldModel instanceof ForeignKeyFieldModel) {
                ForeignKeyFieldModel foreignKeyFieldModel = (ForeignKeyFieldModel) fieldModel;
                extractorCodeFactory = new ForeignKeyColumnValueExtractorCodeFactory(foreignKeyFieldModel, "entity");
            } else if (fieldModel instanceof SimpleEntityFieldModel) {
                // TODO more field types
                SimpleEntityFieldModel simpleEntityFieldModel = (SimpleEntityFieldModel) fieldModel;
                extractorCodeFactory = new SimpleColumnValueExtractorCodeFactory(simpleEntityFieldModel, "entity");
            }
            // TODO extractorCodeFactory can be null
            builder.addStatement("st.set($L, $L)", paramIndex++, extractorCodeFactory.getExtractorCode());
        }
        return builder.build();
    }

    private TypeName entityTypeName() {
        return TypeName.get(entityModel().getType().asType());
    }

    private TypeName entityIdTypeName() {
        return TypeName.get(entityModel().getIdField().getFieldType());
    }

    private EntityModel entityModel() {
        return statementsModel.getEntityModel();
    }

    /**
     * Class for code we need to get value from entity to execute an sql-statement.
     *
     * @param <M>
     */
    @Getter
    @RequiredArgsConstructor
    abstract static class ColumnValueExtractorCodeFactory<M extends FieldModel> {
        private final M model;
        private final String entityVariableName;

        abstract CodeBlock getExtractorCode();


    }

    /**
     * Reads simple field-value (not another  entity) from entity.
     * <p>
     * a.) by calling a getter
     * b.) by accessing the field
     */
    static class SimpleColumnValueExtractorCodeFactory extends ColumnValueExtractorCodeFactory<SimpleEntityFieldModel> {

        public SimpleColumnValueExtractorCodeFactory(SimpleEntityFieldModel model, String entityVariableName) {
            super(model, entityVariableName);
        }

        @Override
        CodeBlock getExtractorCode() {
            return getModel().getGetter().map(this::getExtractorCodeGetter).orElseGet(() -> getExtractorCodeFieldAccess());
        }

        private CodeBlock getExtractorCodeFieldAccess() {
            return CodeBlock.builder()
                    .add("$T.getFieldValue($L, \"$L\")", FieldUtils.class, getEntityVariableName(), getModel().getFieldName())
                    .build();
        }

        private CodeBlock getExtractorCodeGetter(ExecutableElement executableElement) {
            return CodeBlock.builder()
                    .add("$L.$L()", getEntityVariableName(), executableElement.getSimpleName())
                    .build();
        }
    }

    /**
     * Reads values for field annotated with {@link one.xis.sql.ForeignKey}. This means getting the primary key
     * from the file value (an entity).
     */
    static class ForeignKeyColumnValueExtractorCodeFactory extends ColumnValueExtractorCodeFactory<ForeignKeyFieldModel> {

        public ForeignKeyColumnValueExtractorCodeFactory(ForeignKeyFieldModel model, String entityVariableName) {
            super(model, entityVariableName);
        }

        @Override
        CodeBlock getExtractorCode() {
            return getModel().getGetter().map(this::getExtractorCodeGetter).orElseGet(() -> getExtractorCodeFieldAccess());
        }

        private CodeBlock getExtractorCodeFieldAccess() {
            return new CodeBlockBuilder("$T.getPk(($T)$T.getFieldValue($L, \"$L\"))")
                    .withVar(getFieldEntityUtilType())
                    .withVar(getFieldEntityType())
                    .withVar(FieldUtils.class)
                    .withVar(getEntityVariableName())
                    .withVar(getFieldName())
                    .build();
        }

        private CodeBlock getExtractorCodeGetter(ExecutableElement executableElement) {
            return new CodeBlockBuilder("$T.getPk($L.$L())")
                    .withVar(getFieldEntityUtilType())
                    .withVar(getEntityVariableName())
                    .withVar(executableElement.getSimpleName())
                    .build();
        }

        private String getFieldName() {
            return getModel().getFieldName().toString();
        }

        private TypeMirror getFieldEntityType() {
            return getModel().getFieldEntityModel().getType().asType();
        }


        private TypeMirror getFieldEntityIdType() {
            return getModel().getFieldEntityModel().getIdField().getFieldType();
        }

        private TypeName getFieldEntityUtilType() {
            return EntityUtilModel.getEntityUtilTypeName(getModel().getFieldEntityModel());
        }

    }


    private <T extends FieldModel> List<T> sortedByColumnName(Collection<T> values) {
        List<T> rv = new ArrayList<>(values);
        Collections.sort(rv, Comparator.comparing(FieldModel::getColumnName));
        return rv;
    }
}
