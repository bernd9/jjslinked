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

    void doSetClean();

    Map<String, Supplier<?>> suppliers();

    default E entity() {
        return (E) this;
    }

    @SuppressWarnings("unchecked")
    default <T> T load(String fieldName) {
        return (T) suppliers().get(fieldName).get();
    }

    default void addSupplier(String fieldName, Supplier<?> supplier) {
        suppliers().put(fieldName, supplier);
    }
}
