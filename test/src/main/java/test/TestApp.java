package test;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Singleton;

@Singleton
@Application
public class TestApp {

    public static void main(String[] args) throws Exception{
        long t0 = System.currentTimeMillis();
        ApplicationRunner.run(TestApp.class);
        System.out.println(System.currentTimeMillis()- t0);
    }
 }
