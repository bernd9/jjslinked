package com.ejc.sql.api;

import com.ejc.api.context.ClassReference;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class EntityFieldMapper {
    private final ClassReference declaringClass;
    private final Class<?> fieldType;
    private final int sqlType;
    private final String fieldName;

    protected void updateFieldValue(Object o, ResultSet rs, int columnIndex) throws Exception {
        Class<?> c = o.getClass();
        while (!c.equals(Object.class)) {
            if (c.equals(declaringClass.getReferencedClass())) {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(o, fromRs(rs, columnIndex));
                break;
            }
            c = c.getSuperclass();
        }
    }

    public void updateStatement(PreparedStatement statement, Object o, int columnIndex) throws Exception {
        Object value = getFieldValue(o);
        statement.setObject(columnIndex, value, sqlType);
    }

    private Object getFieldValue(Object o) throws Exception {
        Class<?> c = o.getClass();
        while (!c.equals(Object.class)) {
            if (c.equals(declaringClass.getReferencedClass())) {
                Field field = c.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(o);
            }
            c = c.getSuperclass();
        }
        return null;
    }

    private Object fromRs(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex, fieldType);
    }
}
