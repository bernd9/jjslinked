package one.xis.processor;

import one.xis.Singleton;
import one.xis.SystemProperty;

@Singleton
class Test1 {

    @SystemProperty(defaultValue = "3306", name = "port")
    private int port;

}