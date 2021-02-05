package one.xis.sql;

public enum ForeignKeyAction {
    SET_NULL_DBMS,
    CASCADE_DBMS,
    SET_NULL_API,
    CASCADE_API,
    NOOP
}
