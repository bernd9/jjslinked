package com.ejc;

import com.ejc.api.context.ApplicationContextInitializer;
import com.ejc.api.context.ModuleLoader;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationRunner {

    public static void run(Class<?> applicationClass) {
        if (!applicationClass.isAnnotationPresent(Application.class)) {
            throw new IllegalStateException(applicationClass + " is not annotated with @" + Application.class.getName());
        }
        try {
            createContext(applicationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createContext(Class<?> applicationClass) {
        ModuleLoader moduleLoader = new ModuleLoader(applicationClass);
        moduleLoader.load();
        ApplicationContextInitializer initializer = new ApplicationContextInitializer(moduleLoader.getActualModule(), moduleLoader.getModules());
        initializer.initialize();
    }
}
