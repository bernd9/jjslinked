package one.xis.sql.processor;

public class CustomerCrudHandler extends EntityCrudHandler<Customer, Long> {

    private static class OrdersFieldHandler extends ReferredFieldHandler<Customer, Long, Order, Long> {

        OrdersFieldHandler() {
            super(new OrderCrudHandler(), "customer_id");
        }

        @Override
        protected Long getEntityPk(Customer entity) {
            return CustomerUtil.getPk(entity);
        }

        @Override
        protected Long getFieldValuePk(Order fieldValue) {
            return OrderUtil.getPk(fieldValue);
        }

        @Override
        protected void setFieldValueFk(Order fieldValue, Customer entity) {
            OrderUtil.setCustomer(fieldValue, entity);
        }

        @Override
        protected void unlinkFieldValues(Collection<Long> fieldPks) {
            unlinkBySetFkToNull(fieldPks);
        }


    }

    private static AddressCrudHandler addressCrudHandler = new AddressCrudHandler();
    private static OrdersFieldHandler ordersFieldHandler = new OrdersFieldHandler();

    public CustomerCrudHandler() {
        super(new CustomerTableAccessor());
    }

    @Override
    protected void doSave(Customer entity) {
        addressCrudHandler.save(CustomerUtil.getInvoiceAddress(entity));
        getEntityTableAccessor().save(entity);
        ordersFieldHandler.updateFieldValues(CustomerUtil.getPk(entity), CustomerUtil.getOrders(entity));
    }
}