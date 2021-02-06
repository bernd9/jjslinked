package one.xis.sql;

import javax.lang.model.type.TypeMirror;

public class NamingRules {

    public static String toSqlName(String javaName) {
        return null; // TODO
    }

    public static String toForeignKeyName(TypeMirror referencedType) {
        return toSqlName(referencedType.toString()) + "_id";
    }

    public static String underscoresToCamelCase(String sqlName) {
        return null; // TODO
    }

    public static String crossTableAccessorName(String tableName, String columnName) {
        return underscoresToCamelCase(String.format("one.xis.generated.%s_%sCrossTableAccessor", tableName, columnName));
    }
}
