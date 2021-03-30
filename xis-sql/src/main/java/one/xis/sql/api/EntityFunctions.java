package one.xis.sql.api;

public interface EntityFunctions<E, EID> {

    boolean compareColumnValues(E e1, E e2);

    EID getPk(E entity);

    void setPk(E entity, EID pk);

    E doClone(E entity);
    
}
