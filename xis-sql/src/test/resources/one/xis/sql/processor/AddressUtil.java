package one.xis.sql.processor;

public class AddressUtil {

    private AddressUtil() {
    }

    public static Long getPk(Address entity) {
        return entity.getId();
    }

    public static void setPk(Address entity, Long pk) {
        entity.setId(pk);
    }
}