package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Wrapper;

@RequiredArgsConstructor
public abstract class EntityResultSet<E> implements ResultSet {

    @Delegate(types = {ResultSet.class, Wrapper.class})
    private final ResultSet resultSet;

    public abstract E getEntity() throws SQLException;

}
