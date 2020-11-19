package com.ejc;

import com.ejc.api.context.ApplicationContext;
import com.ejc.api.context.ApplicationContextFactory;
import com.ejc.api.context.SingletonProcessor;
import com.ejc.util.ClassUtils;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class ApplicationRunner {

    public static void run(Class<?> applicationClass) {
        if (!applicationClass.isAnnotationPresent(Application.class)) {
            throw new IllegalStateException(applicationClass + " is not annotated with @" + Application.class.getName());
        }
        try {
            ApplicationContextFactory factory = new ApplicationContextFactory(applicationClass);
            factory.createApplicationContext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void run(Optional<SingletonProcessor> singletonProcessor) {
        ApplicationClassHolder holder = (ApplicationClassHolder) ClassUtils.createInstance(ApplicationContext.APPLICATION_CLASS_HOLDER_NAME);
        ApplicationContextFactory factory = new ApplicationContextFactory(ClassUtils.classForName(holder.getCurrentAppClassName()));
        singletonProcessor.ifPresent(factory::addSingletonProcessor);
        factory.createApplicationContext();
    }

    public static void main(String[] arg) {
        run(Optional.empty());
    }
}



