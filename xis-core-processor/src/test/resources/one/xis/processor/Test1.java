package one.xis.processor;

import com.ejc.Singleton;
import com.ejc.SystemProperty;

@Singleton
class Test1 {

    @SystemProperty(defaultValue = "3306", name = "port")
    private int port;

}