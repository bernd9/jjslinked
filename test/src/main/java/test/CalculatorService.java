package test;

import com.jjslinked.annotations.Client;
import com.jjslinked.annotations.LinkedMethod;

@Client("calculator")
public class CalculatorService {

    @LinkedMethod("square")
    int square(int i) {
        return i*i;
    }
}
