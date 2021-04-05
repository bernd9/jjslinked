package one.xis.sql.api;

public interface EntityFunctions<E, EID> {

    boolean compareColumnValues(E e1, E e2);

    // TODO replace util-use-cases in classes with functions
    EID getPk(E entity);

    void setPk(E entity, EID pk);

    E doClone(E entity);
    
}
