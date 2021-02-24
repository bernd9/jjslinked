package one.xis.sql.api;

public abstract class EntitySaveHandler<E, ID> {

    public void save(E entity) {
        if (entity instanceof EntityProxy) {

        }
    }


    // protected abstract EntityProxy<E, ID> createProxy(E entity);


}
