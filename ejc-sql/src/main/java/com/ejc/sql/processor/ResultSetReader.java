package com.ejc.sql.processor;

import java.sql.ResultSet;

interface ResultSetReader<T> {
    T read(ResultSet rs, String columnName);
}
