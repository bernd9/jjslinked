package one.xis.processor;

import com.ejc.Inject;
import com.ejc.Singleton;
import com.ejc.SystemProperty;

@Singleton
class Test2 {

    @Inject
    private Test1 testValue;

    @SystemProperty(defaultValue = "localhost", name = "host")
    private String host;
}