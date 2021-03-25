package one.xis.sql.processor;

import com.ejc.util.ObjectUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerUtil {

    private CustomerUtil() {
    }

    public static Long getPk(Customer entity) {
        return entity.getId();
    }

    public static void setPk(Customer entity, Long pk) {
        entity.setId(pk);
    }

    public static Stream<Long> getPks(Collection<Customer> collection) {
        return collection.stream().map(CustomerUtil::getPk);
    }

    public static Map<Long,Customer> mapByPk(Collection<Customer> entities) {
        return entities.stream().collect(Collectors.toMap(CustomerUtil::getPk, Function.identity()));
    }

    public static boolean compareColumnValues(Customer entity1, Customer entity2) {
        if (!ObjectUtils.equals(getFirstName(entity1), getFirstName(entity2))) {
            return false;
        }
        if (!ObjectUtils.equals(getId(entity1), getId(entity2))) {
            return false;
        }
        if (!ObjectUtils.equals(AddressUtil.getPk(getInvoiceAddress(entity1)), AddressUtil.getPk(getInvoiceAddress(entity2)))) {
            return false;
        }
        if (!ObjectUtils.equals(getLastName(entity1), getLastName(entity2))) {
            return false;
        }
        return true;
    }

    public static Customer doClone(Customer o) {
        Customer rv = new Customer();
        rv.setFirstName(o.getFirstName());
        rv.setId(o.getId());
        rv.setInvoiceAddress(AddressUtil.doClone(o.getInvoiceAddress()));
        rv.setLastName(o.getLastName());
        rv.setOrders(OrderUtil.doClone(o.getOrders()));
        return rv;
    }

    public static Set<Customer> doClone(Set<Customer> coll) {
        Set<Customer> rv = new HashSet<>();
        coll.stream().map(CustomerUtil::doClone).forEach(rv::add);
        return rv;
    }

    public static List<Customer> doClone(List<Customer> coll) {
        List<Customer> rv = new LinkedList<>();
        coll.stream().map(CustomerUtil::doClone).forEach(rv::add);
        return rv;
    }

    public static String getFirstName(Customer entity) {
        return entity.getFirstName();
    }

    public static void setFirstName(Customer entity, String value) {
        entity.setFirstName(value);
    }

    public static Long getId(Customer entity) {
        return entity.getId();
    }

    public static void setId(Customer entity, Long value) {
        entity.setId(value);
    }

    public static Address getInvoiceAddress(Customer entity) {
        return entity.getInvoiceAddress();
    }

    public static void setInvoiceAddress(Customer entity, Address value) {
        entity.setInvoiceAddress(value);
    }

    public static String getLastName(Customer entity) {
        return entity.getLastName();
    }

    public static void setLastName(Customer entity, String value) {
        entity.setLastName(value);
    }

    public static List<Order> getOrders(Customer entity) {
        return entity.getOrders();
    }

    public static void setOrders(Customer entity, List<Order> value) {
        entity.setOrders(value);
    }
}