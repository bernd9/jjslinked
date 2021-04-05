package one.xis.sql.processor;

public class CustomerCrudRepositoryImpl  {

    private final CustomerTableAccessor customerTableAccessor;
    private final AddressTableAccessor addressTableAccessor;

    public CustomerCrudRepositoryImpl() {
        customerTableAccessor = new CustomerTableAccessor();
    }

    public void save(Customer customer) {
        customerTableAccessor.save(customer);
    }

}