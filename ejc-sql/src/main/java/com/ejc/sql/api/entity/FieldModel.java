package com.ejc.sql.api.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FieldModel {
    private String fieldName;
    private Class<?> fieldType;
    private String columnName;
    private int sqlType;

}
