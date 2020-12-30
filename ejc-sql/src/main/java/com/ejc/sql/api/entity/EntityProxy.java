package com.ejc.sql.api.entity;

public interface EntityProxy {

    boolean requiresUpdate();

    Object getEntity();
}
