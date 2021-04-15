package one.xis.sql.processor;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import lombok.Getter;

import javax.annotation.processing.ProcessingEnvironment;
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
        nonPkColumnFields.addAll(entityModel.getNonComplexFields());
        nonPkColumnFields.addAll(entityModel.getForeignKeyFields());
        nonPkColumnFields.addAll(entityModel.getJsonFields());
        nonPkColumnFields.remove(entityModel.getIdField());
        // Cross-table-fields, collection-table-fields and referenced field to not have a column in this table
    }

    static TypeName getEntityStatementsTypeName(EntityModel entityModel) {
        return ClassName.get(entityModel.getPackageName(), getEntityStatementsSimpleName(entityModel));
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
                .append(entityModel.getTableName())
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

    String getUpdateSql() {
        StringBuilder update = new StringBuilder()
                .append("UPDATE ")
                .append(entityModel.getTableName())
                .append(" SET ");
        Iterator<FieldModel> fieldModelIterator = nonPkColumnFields.iterator();
        while (fieldModelIterator.hasNext()) {
            update.append(fieldModelIterator.next().getColumnName())
                    .append("=?");
            if (fieldModelIterator.hasNext()) {
                update.append(",");
            }
        }
        return update
                .append(" WHERE ")
                .append(getPkColumnName())
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
                .append(entityModel.getTableName())
                .append(" WHERE ")
                .append(getPkColumnName())
                .append("=?")
                .toString();
    }

    String getDeleteAllSql() {
        return new StringBuilder()
                .append("DELETE FROM ")
                .append(entityModel.getTableName())
                .toString();
    }

    String getSelectByColumnValueSqlPattern() {
        List<FieldModel> fieldModels = getSelectSqlFields();
        return new StringBuilder()
                .append("SELECT ")
                .append(columnList(fieldModels))
                .append(" FROM ")
                .append(entityModel.getTableName())
                .append(" WHERE %s=?")
                .toString();
    }

    String getUpdateColumnValuesToNullByPkSqlPattern() {
        return new StringBuilder()
                .append("UPDATE ")
                .append(entityModel.getTableName())
                .append(" SET %s=NULL")
                .append(" WHERE ")
                .append(entityModel.getIdField().getColumnName())
                .append("=?")
                .toString();
    }

    String getSelectByIdSql() {
        List<FieldModel> fieldModels = getSelectSqlFields();
        return new StringBuilder()
                .append("SELECT ")
                .append(columnList(fieldModels))
                .append(" FROM ")
                .append(entityModel.getTableName())
                .append(" WHERE ")
                .append(getPkColumnName())
                .append("=?")
                .toString();
    }

    List<FieldModel> getSelectSqlFields() {
        List<FieldModel> fieldModels = new ArrayList<>();
        fieldModels.add(entityModel.getIdField());
        fieldModels.addAll(nonPkColumnFields);
        return fieldModels;
    }

    String getSelectAllSql() {
        List<FieldModel> fieldModels = getSelectSqlFields();
        return new StringBuilder()
                .append("SELECT ")
                .append(columnList(fieldModels))
                .append(" FROM ")
                .append(entityModel.getTableName())
                .toString();
    }

    private String columnList(List<FieldModel> fieldModels) {
        return fieldModels.stream()
                .map(FieldModel::getColumnName)
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
