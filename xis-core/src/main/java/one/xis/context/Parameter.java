package one.xis.context;

interface Parameter {

    boolean isSatisfied(SingletonProviders providers);

    Object getValue();

    ClassReference getParameterType();

    void onSingletonCreated(Object o);
}
