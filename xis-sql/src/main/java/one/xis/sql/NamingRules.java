package one.xis.sql;

public class NamingRules {

    public static String toSqlName(String javaName) {
        return null; // TODO
    }

    public static String underscoresToCamelCase(String sqlName) {
        return null; // TODO
    }

    public static String crossTableAccessorName(String tableName, String columnName) {
        return underscoresToCamelCase(String.format("one.xis.generated.%s_%sCrossTableAccessor", tableName, columnName));
    }
}
