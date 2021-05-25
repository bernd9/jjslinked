package one.xis.sql;

import org.junit.jupiter.api.Test;

public class StringTest {

    class TestClass {


        String append(int s) {
            return new StringBuilder().append("123").append(s).append("123").append(s).append("123").append(s).append("123").append(s).append("123").append(s).append("123").append(s).toString();
        }

        String appends(int s) {
            return String.format("bla bla bla bla %s  bla bla %d", "blub", s);
        }
    }

    @Test
    void instances() {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            new TestClass();
        }
        long t1 = System.currentTimeMillis();
        System.out.println("instances: " + (t1 - t0));
    }

    @Test
    void append() {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            new TestClass().append(i);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("append: " + (t1 - t0));
    }

    @Test
    void append2() {
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            new TestClass().appends(i);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("append2: " + (t1 - t0));
    }
}
