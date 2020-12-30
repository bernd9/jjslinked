package com.ejc.sql.api.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressImpl extends Address {
    private boolean updated;
    private final Address address;

    Long getId() {
        return null;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
