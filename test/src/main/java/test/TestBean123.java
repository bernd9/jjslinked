package test;

import com.ejc.Singleton;

@Singleton
public class TestBean123 {

    public static void main(String[] args) throws Exception{
        Object o = Class.forName("com.injectlight.ApplicationContext").newInstance();
    }
 }
