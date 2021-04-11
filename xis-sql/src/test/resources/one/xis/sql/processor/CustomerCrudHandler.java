package one.xis.sql.processor;

import java.util.Collection;
import one.xis.sql.api.CrossTableFieldHandler;
import one.xis.sql.api.EntityCrudHandler;
import one.xis.sql.api.EntityCrudHandlerSession;
import one.xis.sql.api.ReferredFieldHandler;

class CustomerCrudHandler extends EntityCrudHandler<Customer, Long> {

    private static AddressCrudHandler addressCrudHandler = new one.xis.sql.processor.AddressCrudHandler();

    private static OrdersFieldHandler ordersFieldHandler = new one.xis.sql.processor.CustomerCrudHandler.OrdersFieldHandler();

    private static AgentsFieldHandler agentsFieldHandler = new one.xis.sql.processor.CustomerCrudHandler.AgentsFieldHandler();

    CustomerCrudHandler() {
        super(new CustomerTableAccessor(), new CustomerFunctions());
    }

    @Override
    protected void doSave(Customer entity, EntityCrudHandlerSession session) {
        addressCrudHandler.save(CustomerUtil.getInvoiceAddress(entity), session);
        session.addSaveAction(entity, getEntityTableAccessor(), getEntityFunctions());
        ordersFieldHandler.updateFieldValues(entity, CustomerUtil.getOrders(entity), session);
        agentsFieldHandler.updateFieldValues(entity, CustomerUtil.getAgents(entity), session);
    }

    private static class OrdersFieldHandler extends ReferredFieldHandler<Customer, Long, Order, Long> {

        OrdersFieldHandler() {
            super(new OrderTableAccessor(), new OrderFunctions(), Customer.class, Order.class);
        }

        @Override
        protected Long getEntityPk(Customer entity) {
            return CustomerUtil.getPk(entity);
        }

        @Override
        protected void setFieldValueFk(Order fieldValue, Customer entity) {
            OrderUtil.setCustomer(fieldValue, entity);
        }

        @Override
        protected void handleUnlinkedFieldValues(Long entityId, Collection<Order> fieldValues,
           EntityCrudHandlerSession crudHandlerSession) {
         unlinkBySetFkToNull(fieldValues, crudHandlerSession);
        }

        @Override
        protected Collection<Order> getFieldValues(Customer entity) {
         return CustomerUtil.getOrders(entity);
        }
    }

    private static class AgentsFieldHandler extends CrossTableFieldHandler<Customer, Long, Agent, Long> {

        AgentsFieldHandler() {
            super(new AgentTableAccessor(), new CustomerAgentCrossTableAccessor(), new AgentFunctions(), Customer.class, Agent.class);
        }

        @Override
        protected Long getFieldValuePk(Agent fieldValue) {
          return AgentUtil.getPk(fieldValue);
        }

        @Override
        protected Collection<Agent> getFieldValues(Customer entity) {
          return CustomerUtil.getAgents(entity);
        }

        @Override
        protected Long getEntityPk(Customer entity) {
          return CustomerUtil.getPk(entity);
        }
    }
}