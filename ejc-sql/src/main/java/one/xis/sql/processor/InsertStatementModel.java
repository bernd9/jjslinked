package one.xis.sql.processor;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InsertStatementModel implements StatementModel {
    private final EntityModel entityModel;
    
    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder().append("INSERT INTO `")
                .append(entityModel.getTableName())
                .append("` (");
        sql.append(entityModel.getEntityFields().stream()
                .map(EntityFieldModel::getColumnName)
                .map(column -> String.format("`%d`", column))
                .collect(Collectors.joining(", ")));
        sql.append(") values (");
        sql.append(entityModel.getEntityFields().stream()
                .map(column -> "?")
                .collect(Collectors.joining(", ")));
        sql.append(")");
        return sql.toString();
    }

    @Override
    public List<EntityFieldModel> parameterFields() {
        return entityModel.getEntityFields();
    }
}
