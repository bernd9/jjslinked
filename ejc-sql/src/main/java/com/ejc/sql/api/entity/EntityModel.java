package com.ejc.sql.api.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public interface EntityModel {

    String getTableName();

    String getEntityName();

    List<FieldModel> getIdFields();

    List<FieldModel> getColumnFields();

    default PreparedStatement prepareInsert(Connection con) {
        return null;
    }

}
