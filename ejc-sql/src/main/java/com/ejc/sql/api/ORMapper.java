package com.ejc.sql.api;

import com.ejc.Singleton;
import com.ejc.api.context.ClassReference;
import com.ejc.util.ClassUtils;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Singleton
public class ORMapper {

    private final ClassReference declaringClass;
    private final Map<String, EntityFieldMapper> entityFieldMapperMap = new HashMap<>();

    public ORMapper(ClassReference declaringClass) {
        this.declaringClass = declaringClass;
    }

    public void addField(String fieldName, Class<?> fieldType) {
        if (entityFieldMapperMap.containsKey(fieldName))
            throw new IllegalStateException(fieldName + " : regardless any inheritance mapped fieldnames must be unique");
        entityFieldMapperMap.put(fieldName, new EntityFieldMapper(declaringClass, fieldType, SQLTypeMapper.getSqlTypeFor(fieldType), fieldName));
    }

    public Object mapFormResultSet(ResultSet rs, List<String> fieldNames) throws Exception {
        Object entity = ClassUtils.createInstance(declaringClass.getClass());
        int columnIndex = 0;
        for (String name : fieldNames) {
            columnIndex++;
            EntityFieldMapper mapper = entityFieldMapperMap.get(name);
            mapper.updateFieldValue(entity, rs, ++columnIndex);
        }
        return entity;
    }

    public void mapIntoStatement(Object o, PreparedStatement statement, List<String> fieldNames) throws Exception {
        int columnIndex = 0;
        for (String name : fieldNames) {
            EntityFieldMapper mapper = entityFieldMapperMap.get(name);
            mapper.updateStatement(statement, o, ++columnIndex);
        }
    }

}
