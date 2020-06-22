import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.parameter.UserId;

@Client("testClient")
public class Test {

    @LinkedMethod("add2")
    int add2(int i, @UserId String userId) {
        return i + 2;
    }

    @LinkedMethod("test")
    abstract void test();

}