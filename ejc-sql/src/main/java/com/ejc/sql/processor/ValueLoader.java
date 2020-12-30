package com.ejc.sql.processor;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;

abstract class ValueLoader<T> {

    abstract T load(ResultSet rs);

    @RequiredArgsConstructor
    static class Simple<T> extends ValueLoader<T> {

        private final String columnName;
        private final ResultSetReader<T> reader;

        @Override
        T load(ResultSet rs) {
            return reader.read(rs, columnName);
        }
    }

    static class SingleEntity<E> extends ValueLoader<E> {

        @Override
        E load(ResultSet rs) {
            return null;
        }
    }
}
