package one.xis.sql.processor;

public class CustomerCrudHandler extends EntityCrudHandler<Customer, Long, CustomerProxy> {

    public CustomerCrudHandler() {
        super(new CustomerTableAccessor(new CustomerStatements(), Long.class));
    }
}