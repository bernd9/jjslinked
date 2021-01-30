package one.xis.sql;

import javax.lang.model.type.TypeMirror;

public class NamingRules {

    public static String toSqlName(String javaName) {
        return null; // TODO
    }

    public static String toForeignKeyName(TypeMirror referencedType) {
        return toSqlName(referencedType.toString()) + "_id";
    }
}
