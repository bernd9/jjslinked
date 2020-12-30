package com.ejc.sql;

public @interface CrossTableRef {
    String table();

    String entityColumn();

    String attributeColumn();

}
