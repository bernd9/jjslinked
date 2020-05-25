import com.jjslink.annotations.Client;
import com.jjslink.annotations.LinkedMethod;
import com.jjslink.annotations.UserId;

@com.jjslink.annotations.Client
public class Test {

    @LinkedMethod
    int add2(int i, @UserId String userId) {
        return i + 2;
    }

}