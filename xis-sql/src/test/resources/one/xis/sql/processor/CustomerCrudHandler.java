package one.xis.sql.processor;

import one.xis.sql.api.EntityCrudHandlerSession;

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
        protected void unlinkFieldValues(Collection<Order> fieldValues, EntityCrudHandlerSession crudHandlerSession) {
            unlinkBySetFkToNull(fieldValues, crudHandlerSession);
        }
    }

    private static AddressCrudHandler addressCrudHandler = new AddressCrudHandler();
    private static OrdersFieldHandler ordersFieldHandler = new OrdersFieldHandler();

    public CustomerCrudHandler() {
        super(new CustomerTableAccessor());
    }

    @Override
    protected void doSave(Customer entity, EntityCrudHandlerSession session) {
        addressCrudHandler.save(CustomerUtil.getInvoiceAddress(entity), session);
        session.addSaveAction(entity, getEntityTableAccessor());
        ordersFieldHandler.updateFieldValues(CustomerUtil.getPk(entity), CustomerUtil.getOrders(entity), session);
    }
}