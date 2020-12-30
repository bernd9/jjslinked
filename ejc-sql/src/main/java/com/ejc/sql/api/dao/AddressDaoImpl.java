package com.ejc.sql.api.dao;

import com.ejc.sql.api.entity.Address;
import com.ejc.sql.api.entity.AddressImpl;

public class AddressDaoImpl {


    AddressImpl save(Address address) {
        AddressImpl addressImpl;
        if (address instanceof AddressImpl) {
            addressImpl = (AddressImpl) address;
            if (addressImpl.isUpdated()) {
                update(addressImpl);
            }
        } else {
            addressImpl = new AddressImpl(address);
        }
        return addressImpl;
    }


    private void update(AddressImpl addressIml) {

    }

    private AddressImpl insert(Address address) {

        return null;
    }
}
