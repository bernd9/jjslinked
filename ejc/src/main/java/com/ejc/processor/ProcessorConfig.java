package com.ejc.processor;


import com.ejc.MethodAdvice;
import com.ejc.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorConfig {

    @Getter
    private static Set<Class<? extends Annotation>> singletonAnnotations = new HashSet<>();

    @Getter
    private static Map<Class<? extends Annotation>, MethodAdvice<?>> methodAnnotations = new HashMap();

    static {
        singletonAnnotations.add(Singleton.class);
    }

    public static void addSingletonAnnotation(Class<? extends Annotation> c) {
        validateTarget(ElementType.TYPE, c);
        singletonAnnotations.add(c);
    }

    public static void addMethodHandlerAnnotation(Class<? extends Annotation> c, MethodAdvice<?> invocationHandler) {
        validateTarget(ElementType.METHOD, c);
        methodAnnotations.put(c, invocationHandler);
    }

    private static void validateTarget(ElementType expected, Class<? extends Annotation> c) {
        if (!c.isAnnotationPresent(Target.class)) {
            throw new IllegalStateException(c + " must have target-annotation");
        }
        List<ElementType> targets = Arrays.asList(c.getAnnotation(Target.class).value());
        if (targets.size() != 1 || !targets.contains(expected)) {
            throw new IllegalStateException(c + " must have target " + expected + ", only");
        }
    }
}
