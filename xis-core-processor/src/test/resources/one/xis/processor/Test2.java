package one.xis.processor;

import one.xis.Inject;
import one.xis.Singleton;
import one.xis.SystemProperty;

@Singleton
class Test2 {

    @Inject
    private Test1 testValue;

    @SystemProperty(defaultValue = "localhost", name = "host")
    private String host;
}