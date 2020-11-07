package com.ejc.context2;

interface Parameter extends SingletonCreationListener {

    boolean isSatisfied(SingletonProviders providers);

    Object getValue();
}
