package one.xis.sql.processor;

public class CustomerUtil {

    private CustomerUtil() {
    }

    public static Long getPk(Customer entity) {
        return entity.getId();
    }

    public static void setPk(Customer entity, Long pk) {
        entity.setId(pk);
    }
}