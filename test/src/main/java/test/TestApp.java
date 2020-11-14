package test;

import com.ejc.Application;
import com.ejc.ApplicationRunner;
import com.ejc.Singleton;
import com.ejc.processor.CustomSingletonAnnotationProvider;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Application
public class TestApp {

    public static void main(String[] args) {
        ServiceLoader<CustomSingletonAnnotationProvider> loader = ServiceLoader.load(CustomSingletonAnnotationProvider.class);
        Set set = loader.stream().map(ServiceLoader.Provider::get)
                .map(CustomSingletonAnnotationProvider::getAnnotationClass)
                .collect(Collectors.toSet());
        long t0 = System.currentTimeMillis();
        ApplicationRunner.run(TestApp.class);
        System.out.println(System.currentTimeMillis()- t0);
    }
    
 }