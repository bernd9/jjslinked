package one.xis.sql.processor;

import java.util.Collection;
import java.util.stream.Collectors;

public class AddressUtil {

    private AddressUtil() {
    }

    public static Long getPk(Address entity) {
        return entity.getId();
    }

    public static void setPk(Address entity, Long pk) {
        entity.setId(pk);
    }

    public static Stream<Long> getPks(Collection<Address> collection) {
        return collection.stream().map(AddressUtil::getPk);
    }
}