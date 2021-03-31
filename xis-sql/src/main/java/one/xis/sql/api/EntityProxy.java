package one.xis.sql.api;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Do not add Getters or Setter here. This might lead to conflict
 * with methods of the entity.
 */
public interface EntityProxy<E, EID> {

    EID pk();

    void pk(EID id);

    boolean dirty();

    boolean readOnly();

    void doSetClean();

}
