package one.xis.sql.api;

/**
 * Do not add Getters or Setter here. This might lead to conflict
 * with methods of the entity.
 */
public interface EntityProxy<E, EID> {

    EID pk();

    E entity();

    boolean dirty();

    void doSetClean();

    void doSetStored();

    void pk(EID id);

    boolean stored();

}
