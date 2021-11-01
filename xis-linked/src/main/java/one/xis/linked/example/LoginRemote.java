package one.xis.linked.example;

import one.xis.linked.ClientMethod;
import one.xis.linked.ClientService;

@ClientService("LoginService")
public class LoginRemote {
    
    @ClientMethod
    String doLogin(String user, String password) {
        return "";
    }


}
