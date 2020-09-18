package com.ejc.sql.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SQLTypeMapper {
    private static Map<Class<?>, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(short.class, Types.SMALLINT);
        MAPPING.put(java.lang.Short.class, Types.SMALLINT);
        MAPPING.put(int.class, Types.INTEGER);
        MAPPING.put(java.lang.Integer.class, Types.INTEGER);
        MAPPING.put(float.class, Types.FLOAT);
        MAPPING.put(java.lang.Float.class, Types.FLOAT);
        MAPPING.put(long.class, Types.BIGINT);
        MAPPING.put(java.lang.Long.class, Types.BIGINT);
        MAPPING.put(BigInteger.class, Types.BIGINT);
        MAPPING.put(double.class, Types.DECIMAL);
        MAPPING.put(java.lang.Double.class, Types.DECIMAL);
        MAPPING.put(java.math.BigDecimal.class, Types.DECIMAL);
        MAPPING.put(BigDecimal.class, Types.DECIMAL);
        MAPPING.put(Integer.class, Types.DECIMAL);
        MAPPING.put(boolean.class, Types.SMALLINT);
        MAPPING.put(java.lang.Boolean.class, Types.SMALLINT);
        MAPPING.put(Date.class, Types.TIMESTAMP);
        MAPPING.put(char.class, Types.VARCHAR);
        MAPPING.put(java.lang.Character.class, Types.VARCHAR);
        MAPPING.put(byte.class, Types.CHAR);
        MAPPING.put(java.lang.Byte.class, Types.CHAR);
        MAPPING.put(java.lang.String.class, Types.VARCHAR);
        MAPPING.put(String.class, Types.VARCHAR);
        MAPPING.put(java.lang.Object.class, Types.JAVA_OBJECT);
    }

    public static int getSqlTypeFor(Class<?> c) {
        if (!MAPPING.containsKey(c)) throw new IllegalArgumentException("type: " + c);
        return MAPPING.get(c);
    }
}
