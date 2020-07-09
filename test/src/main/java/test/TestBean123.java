package test;

import com.ejc.ApplicationContext;
import com.ejc.Singleton;

@Singleton
public class TestBean123 {

    public static void main(String[] args) throws Exception{
        ApplicationContext context = ApplicationContext.getInstance();
        System.out.println(context.getBeans(CalculatorService.class).size());

    }
 }
