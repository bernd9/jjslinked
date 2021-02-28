package one.xis.sql.processor;


import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

@Getter
class EntityStatementsModel implements Comparator<FieldModel> {
    private final EntityModel entityModel;
    private final TreeSet<FieldModel> nonPkColumnFields;
    private final ProcessingEnvironment processingEnvironment;

    EntityStatementsModel(EntityModel entityModel, ProcessingEnvironment processingEnvironment) {
        this.entityModel = entityModel;
        this.processingEnvironment = processingEnvironment;
        nonPkColumnFields = new TreeSet<>(this);
        nonPkColumnFields.addAll(entityModel.getNonComplexFields().values());
        nonPkColumnFields.addAll(entityModel.getForeignKeyFields());
        nonPkColumnFields.addAll(entityModel.getJsonFields());
        nonPkColumnFields.remove(entityModel.getIdField());
        // Cross-table-fields, collection-table-fields and referenced field to not have a column in this table
    }

    String getEntityStatementsSimpleName() {
        return getEntityStatementsSimpleName(entityModel);
    }

    String getEntityStatementsPackageName() {
        return entityModel.getPackageName();
    }

    static String getEntityStatementsSimpleName(EntityModel entityModel) {
        return entityModel.getSimpleName() + "Statements";
    }

    String getPkColumnName() {
        return entityModel.getIdField().getColumnName();
    }

    String getInsertSql() {
        List<FieldModel> fieldModels = getInsertSqlFields();
        return new StringBuilder()
                .append("INSERT INTO ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .append(" (")
                .append(columnList(fieldModels))
                .append(") VALUES (")
                .append(questionMarks(fieldModels))
                .append(")")
                .toString();
    }

    List<FieldModel> getInsertSqlFields() {
        // TODO optimize performance to move this to a field and init init in constructor
        List<FieldModel> fieldModels = new ArrayList<>();
        fieldModels.add(entityModel.getIdField());
        fieldModels.addAll(nonPkColumnFields);
        return fieldModels;
    }

    /*
    String getInsertSqlSetParameterMethod(int index) {
        FieldModel fieldModel = getInsertSqlFields().get(index - 1);
        if (isString(fieldModel.getFieldType())) {
            return "setString";
        }

        switch (fieldModel.getFieldType().getKind()) {

            case BOOLEAN:
                return "setBoolean";
            case BYTE:
                return "setByte";
            case SHORT:
                return "setShort";
            case INT:
                return "setInt";
            case LONG:
                return "setLong";
            case CHAR:
                return "setChar";
            case FLOAT:
                return "setFloat";
            case DOUBLE:
                return "setDouble";

        }
        if (isPrimitve(fieldModel.getFieldType())) {
            fieldModel.getFieldType().getKind() == TypeKind.ERROR
        }

    }
    */


    private boolean isString(TypeMirror typeMirror) {
        // TODO make "string" static ?
        TypeMirror string = processingEnvironment.getElementUtils().getTypeElement(String.class.getName()).asType();
        return processingEnvironment.getTypeUtils().isSameType(string, typeMirror);
    }

    private boolean isPrimitve(TypeMirror typeMirror) {
        return typeMirror.getKind().isPrimitive();
    }

    String getInsertSqlFieldGetter(int index) {
        return null;
    }

    String getUpdateSql() {
        StringBuilder update = new StringBuilder()
                .append("UPDATE ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .append(" SET ");
        Iterator<FieldModel> fieldModelIterator = nonPkColumnFields.iterator();
        while (fieldModelIterator.hasNext()) {
            update.append("`")
                    .append(fieldModelIterator.next().getColumnName())
                    .append("`")
                    .append("=?");
            if (fieldModelIterator.hasNext()) {
                update.append(",");
            }
        }
        return update
                .append(" WHERE ")
                .append("`")
                .append(getPkColumnName())
                .append("`")
                .append("=?")
                .toString();

    }

    List<FieldModel> getUpdateSqlFields() {
        List<FieldModel> fieldModels = new ArrayList<>();
        fieldModels.addAll(nonPkColumnFields);
        fieldModels.add(entityModel.getIdField());
        return fieldModels;
    }

    String getDeleteSql() {
        return new StringBuilder()
                .append("DELETE FROM ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .append(" WHERE ")
                .append("`")
                .append(getPkColumnName())
                .append("`")
                .append("=?")
                .toString();
    }

    String getDeleteAllSql() {
        return new StringBuilder()
                .append("DELETE FROM ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .toString();
    }

    String getSelectByIdSql() {
        List<FieldModel> fieldModels = getSelectByIdSqlFields();
        return new StringBuilder()
                .append("SELECT ")
                .append(columnList(fieldModels))
                .append(" FROM ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .append(" WHERE ")
                .append("`")
                .append(getPkColumnName())
                .append("`")
                .append("=?")
                .toString();
    }

    List<FieldModel> getSelectByIdSqlFields() {
        List<FieldModel> fieldModels = new ArrayList<>();
        fieldModels.add(entityModel.getIdField());
        fieldModels.addAll(nonPkColumnFields);
        return fieldModels;
    }

    String getSelectAllSql() {
        List<FieldModel> fieldModels = getSelectByIdSqlFields();
        return new StringBuilder()
                .append("SELECT ")
                .append(columnList(fieldModels))
                .append(" FROM ")
                .append("`")
                .append(entityModel.getTableName())
                .append("`")
                .toString();
    }
    
    private String columnList(List<FieldModel> fieldModels) {
        return fieldModels.stream()
                .map(FieldModel::getColumnName)
                .map(col -> String.format("`%s`", col))
                .collect(Collectors.joining(","));
    }

    private String questionMarks(List<FieldModel> fieldModels) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < fieldModels.size(); i++) {
            s.append("?");
            if (i < fieldModels.size() - 1) {
                s.append(",");
            }
        }
        return s.toString();
    }

    @Override
    public int compare(FieldModel o1, FieldModel o2) {
        return o1.getColumnName().compareTo(o2.getColumnName());
    }
}
