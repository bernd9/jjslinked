package one.xis.sql.processor;

import lombok.Data;
import one.xis.sql.goal.CustomerImpl;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class EnetitImplSpeedTest {

    @Test
    void arrayInheritance() {
        String[] s = new String[0];
        for (String s1: s) {

        }
        System.out.println(s.getClass().getSimpleName());
    }

    @Test
    void test1() throws Exception{
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Customer customer = new Customer();
            customer.setFirstName("bla");
            customer.getFirstName();
        }
        long t1 = System.currentTimeMillis();
        System.out.println(t1-t0);
    }

    @Test
    void test2() throws Exception{
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Customer customer = new CustomerImpl1();
            customer.setFirstName("bla");
            customer.getFirstName();
        }
        long t1 = System.currentTimeMillis();
        System.out.println(t1-t0);
    }
}

@Data
class Customer {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postal;
    private String country;
}


class CustomerImpl1 extends Customer  {
  private final Map<String,Object> map = new HashMap<>();

    @Override
    public String getFirstName() {
        return (String) map.get("firstName");
    }

    @Override
    public String getLastName() {
        return (String) map.get("lastName");
    }

    @Override
    public String getStreet() {
        return (String) map.get("street");
    }

    @Override
    public String getCity() {
        return (String) map.get("city");
    }

    @Override
    public String getPostal() {
        return (String) map.get("postal");
    }

    @Override
    public String getCountry() {
        return (String) map.get("country");
    }

    @Override
    public void setFirstName(String firstName) {
        map.put("firstName", firstName);
    }

    @Override
    public void setLastName(String lastName) {
        map.put("lastName", lastName);
    }

    @Override
    public void setStreet(String street) {
        map.put("street", street);
    }

    @Override
    public void setCity(String city) {
        map.put("city", city);
    }

    @Override
    public void setPostal(String postal) {
        map.put("postal", postal);
    }

    @Override
    public void setCountry(String country) {
        map.put("country", country);
    }
}


class CustomerImpl2 extends Customer  {
    private Customer customer = new Customer();
}
