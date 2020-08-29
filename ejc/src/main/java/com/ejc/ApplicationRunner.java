package com.ejc;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationRunner {

    public static void run(Class<?> applicationClass) {
        if (!applicationClass.isAnnotationPresent(Application.class)) {
            throw new IllegalStateException(applicationClass + " is not annotated with @" + Application.class.getName());
        }
        String contextClass = new StringBuilder(applicationClass.getPackageName()).append(".").append("ApplicationContextFactory").toString();
        try {
            Class<ApplicationContextFactory> factoryClass = (Class<ApplicationContextFactory>) Class.forName(contextClass);
            ApplicationContextFactory factory = factoryClass.getConstructor().newInstance();
            factory.createContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
