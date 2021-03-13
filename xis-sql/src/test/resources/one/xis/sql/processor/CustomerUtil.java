package one.xis.sql.processor;

class CustomerUtil {

    private CustomerUtil() {
    }

    static Long getPk(Customer entity) {
        return entity.getId();
    }

    static void setPk(Customer entity, Long pk) {
        entity.setId(pk);
    }
}