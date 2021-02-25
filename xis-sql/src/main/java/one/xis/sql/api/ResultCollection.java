package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Function;

@RequiredArgsConstructor
abstract class ResultCollection<T, C extends Collection<T>> implements Collection<T> {

    private final ResultSet rs;

    private final Function<ResultSet, T> loaderFunction;

    private C data;

    private boolean dirty;

    protected synchronized void ensureLoaded() {
        if (data == null) {
            try {
                data = load();
            } catch (SQLException e) {
                throw new JdbcException("loading failed", e);
            }
        }
    }

    private C load() throws SQLException {
        C coll = createInternalCollection();
        while (rs.next()) {
            coll.add(loaderFunction.apply(rs));
        }
        return coll;
    }

    protected abstract C createInternalCollection();

    protected void setDirty() {
        dirty = true;
    }
}
