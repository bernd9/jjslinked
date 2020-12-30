package com.ejc.sql.api.dao;

import com.ejc.sql.api.entity.Customer;
import com.ejc.sql.api.entity.CustomerImpl;
import com.ejc.sql.api.entity.EntityProxy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CustomerDaoImpl {
    private final AddressDaoImpl addressDao;
    private final AgentDaoImpl agentDao;

    public void save(Customer customer) {
        CustomerImpl customerImpl;
        if (customer.getAddress() == null) {
            
        }
        addressDao.save(customer.getAddress());
        if (customer instanceof EntityProxy) {

        } else {

        }
        afterSaveCustomer(customer);

    }

    Object doSave(Customer customer) {

        // FK in customer-table. Address must be stored first


        // FK in
        if (customer.getAgents() != null) {

        }
        return null;
    }

    private void beforeSaveCustomer(Customer customer) {

        // cross table
        if (customer.getAgents() != null) {
            customer.getAgents().forEach(agentDao::save);
        }
    }

    private void afterSaveCustomer(Customer customer) {
        // FK in agents

    }

}
