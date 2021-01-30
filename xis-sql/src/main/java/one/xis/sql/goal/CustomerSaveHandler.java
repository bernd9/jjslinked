package one.xis.sql.goal;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.Generated;

@Generated("xis-sql")
@RequiredArgsConstructor
public class CustomerSaveHandler {

    private final CustomerOrMapper orMapper;
    private final CustomerInsert customerInsert;
    private final CustomerInsert customerUpdate;
    //n:m
    private final AgentRepositoryImpl agentRepository;
    private final CustomerAgentCrossTable customerAgentCrossTable;
    private final OrderRepositoryImpl orderRepository;
    private final AddressRepositoryImpl addressRepository;

    void save(Customer customer) {
        if (customer instanceof CustomerImpl) {
            saveImpl((CustomerImpl) customer);
        } else {
            saveImpl(new CustomerImpl(customer));
        }
    }

    void saveImpl(CustomerImpl customerImpl) {
        // n:m
        customerAgentCrossTable.updateReferences(customerImpl, customerImpl.getAgents());
        // 1:1 key in customer
        addressRepository.save(customerImpl.getAddress());
        customerImpl.setAddressId(customerImpl.getAddress().getId());

        if (customerImpl.getPk() == null) {
            customerInsert.execute(customerImpl);
        } else {
            customerUpdate.execute(customerImpl);
        }
        // 1:n key in order
        if (customerImpl.getOrders() == null) {
            if (customerImpl.isOrphanDelete()) {
                orderRepository.deleteByCustomer(customerImpl);
            } else {

            }

        } else {
            for (Order order: customerImpl.getOrders()) {
                OrderImpl orderImpl = new OrderImpl(order);
                orderImpl.setCustomerId(customerImpl.getId());
                orderRepository.saveImpl(orderImpl);
            }
        }
    }

}
