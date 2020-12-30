package com.ejc.sql.api.dao;

import com.ejc.sql.api.DaoImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DaoImplementations {

    public static <E> DaoImpl<E> getDao(Class<E> entityClass) {
        return null;
    }
}
