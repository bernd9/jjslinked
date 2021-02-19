package one.xis.sql.api;

public abstract class EntitySaveHandler<E, P, ID> {

    public void save(E entity) {
        if (entity instanceof EntityProxy) {

        }
    }


    protected abstract EntityProxy<E, ID> createProxy(E entity);

    protected abstract void saveImpl(P entityProxy);

}
