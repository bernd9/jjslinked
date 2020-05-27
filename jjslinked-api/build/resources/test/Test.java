import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.annotations.UserId;

@Client
public class Test {

    @LinkedMethod
    int add2(int i, @UserId String userId) {
        return i + 2;
    }

}