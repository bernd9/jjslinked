package com.ejc.sql.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SQLTypeMapper {
    private static Map<Class<?>, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put(short.class, Types.SMALLINT);
        MAPPING.put(Short.class, Types.SMALLINT);
        MAPPING.put(int.class, Types.INTEGER);
        MAPPING.put(Integer.class, Types.INTEGER);
        MAPPING.put(float.class, Types.FLOAT);
        MAPPING.put(Float.class, Types.FLOAT);
        MAPPING.put(long.class, Types.BIGINT);
        MAPPING.put(Long.class, Types.BIGINT);
        MAPPING.put(BigInteger.class, Types.BIGINT);
        MAPPING.put(double.class, Types.DECIMAL);
        MAPPING.put(Double.class, Types.DECIMAL);
        MAPPING.put(BigInteger.class, Types.BIGINT);
        MAPPING.put(BigDecimal.class, Types.DECIMAL);
        MAPPING.put(Integer.class, Types.DECIMAL);
        MAPPING.put(boolean.class, Types.SMALLINT);
        MAPPING.put(Boolean.class, Types.SMALLINT);
        MAPPING.put(Date.class, Types.TIMESTAMP);
        MAPPING.put(char.class, Types.VARCHAR);
        MAPPING.put(Character.class, Types.VARCHAR);
        MAPPING.put(byte.class, Types.CHAR);
        MAPPING.put(Byte.class, Types.CHAR);
        MAPPING.put(CharSequence.class, Types.VARCHAR);
        MAPPING.put(String.class, Types.VARCHAR);
        MAPPING.put(Object.class, Types.JAVA_OBJECT);
        MAPPING.put(AtomicBoolean.class, Types.SMALLINT);
        MAPPING.put(AtomicInteger.class, Types.INTEGER);
        MAPPING.put(AtomicLong.class, Types.BIGINT);
        MAPPING.put(new byte[0].getClass(), Types.BLOB);
    }

    public static int getSqlTypeFor(Class<?> c) {
        if (!MAPPING.containsKey(c)) throw new IllegalArgumentException("type: " + c);
        return MAPPING.get(c);
    }
}
