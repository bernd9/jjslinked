package one.xis.sql.processor;

public class CustomerCrudHandler extends EntityCrudHandler<Customer, Long, CustomerProxy> {

    private static class OrdersFieldHandler extends ReferredFieldHandler<Long, Order, Long, OrderProxy> {

        OrdersFieldHandler() {
            super(new OrderCrudHandler(), "customer_id");
        }

        @Override
        protected Long getFieldValuePk(Order fieldValue) {
            return OrderUtil.getPk(fieldValue);
        }

        @Override
        protected void unlinkFieldValues(Collection<FID> fieldPks) {
            unlinkBySetFkToNull(fieldPks);
        }

        @Override
        protected void setFk(Order fieldValue, Long fk) {
            OrderUtil.setFk(fk);
        }
    }

    private static OrdersFieldHandler ordersFieldHandler = new OrdersFieldHandler();

    public CustomerCrudHandler() {
        super(new CustomerTableAccessor());
    }

    @Override
    public void save(Customer entity) {
        ordersFieldHandler.updateFieldValues(CustomerUtil.getPk(entity), CustomerUtil.getOrders(entity));
    }
}