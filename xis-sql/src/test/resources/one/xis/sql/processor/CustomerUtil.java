package one.xis.sql.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CustomerUtil {

    private CustomerUtil() {
    }

    static Long getPk(Customer entity) {
        return entity.getId();
    }

    static void setPk(Customer entity, Long pk) {
        entity.setId(pk);
    }

    static Stream<Long> getPks(Collection<Customer> collection) {
        return collection.stream().map(CustomerUtil::getPk);
    }

    static Map<Long,Customer> mapByPk(Collection<Customer> entities) {
        return entities.stream().collect(Collectors.toMap(CustomerUtil::getPk, Function.identity()));
    }

    static Customer doClone(Customer o) {
        Customer rv = new Customer();
        rv.setFirstName(o.getFirstName());
        rv.setId(o.getId());
        rv.setInvoiceAddress(AddressUtil.doClone(o.getInvoiceAddress()));
        rv.setLastName(o.getLastName());
        rv.setOrders(OrderUtil.doClone(o.getOrders()));
        return rv;
    }

    static Set<Customer> doClone(Set<Customer> coll) {
        Set<Customer> rv = new HashSet<>();
        coll.stream().map(CustomerUtil::doClone).forEach(rv::add);
        return rv;
    }

    static List<Customer> doClone(List<Customer> coll) {
        List<Customer> rv = new LinkedList<>();
        coll.stream().map(CustomerUtil::doClone).forEach(rv::add);
        return rv;
    }
}