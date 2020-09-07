package com.ejc;

import com.ejc.api.context.ModuleLoader;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationRunner {

    public static void run(Class<?> applicationClass) {
        if (!applicationClass.isAnnotationPresent(Application.class)) {
            throw new IllegalStateException(applicationClass + " is not annotated with @" + Application.class.getName());
        }
        try {
            ApplicationContextFactory factory = actualFactory(applicationClass);
            loadModules(factory);
            factory.createContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String contextFactory(Class<?> applicationClass) {
        return new StringBuilder(applicationClass.getPackageName()).append(".").append(ApplicationContextFactory.IMPLEMENTATION_SIMPLE_NAME).toString();
    }

    private static ApplicationContextFactory actualFactory(Class<?> applicationClass) throws Exception {
        Class<ApplicationContextFactory> factoryClass = (Class<ApplicationContextFactory>) Class.forName(contextFactory(applicationClass));
        return factoryClass.getConstructor().newInstance();
    }


    private static void loadModules(ApplicationContextFactory factory) {
        ModuleLoader moduleLoader = new ModuleLoader(factory);
        moduleLoader.addModules();
    }
}
