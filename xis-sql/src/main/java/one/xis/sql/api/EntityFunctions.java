package one.xis.sql.api;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityFunctions<E, EID> {

    boolean compareColumnValues(E e1, E e2);

    // TODO replace util-use-cases in classes with functions
    EID getPk(E entity);

    void setPk(E entity, EID pk);

    E doClone(E entity);

    E toEntityProxy(ResultSet rs) throws SQLException;
}
