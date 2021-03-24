package one.xis.sql.processor;

public class CustomerCrudHandler extends EntityCrudHandler<Customer, Long, CustomerProxy> {

    private static class OrdersFieldHandler extends ReferredFieldHandler<Long, Order, Long, OrderProxy> {

        OrdersFieldHandler() {
            super(new OrderCrudHandler(), "customer_id");
        }

        @Override
        protected Long getFieldValuePk(Order fieldValue) {
            return order.getId();
        }

        @Override
        protected void unlinkFieldValues(Collection<FID> fieldPks) {
            unlinkBySetFkToNull(fieldPks);
        }

        @Override
        protected void setFk(Order fieldValue, Long fk) {
            fieldValue.setId(fk);
        }
    }


    public CustomerCrudHandler() {
        super(new CustomerTableAccessor());
    }
}