package one.xis.sql.processor;

import java.util.List;

interface StatementModel {

    String toSql();

    List<EntityFieldModel> parameterFields();

}
