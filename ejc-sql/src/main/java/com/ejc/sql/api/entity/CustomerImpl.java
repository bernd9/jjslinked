package com.ejc.sql.api.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerImpl extends Customer {
    private boolean updated;
    private final Customer customer;

    private Long getPk() {
        return null;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }


}
