package one.xis.sql.processor;

import java.util.Map;
import java.util.Stream;
import java.util.function.Function;
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

    static Map<Long,Address> mapByPk(Collection<Address> entities) {
        return entities.stream().collect(Collectors.toMap(AddressUtil::getPk, Function.identity()));
    }
}