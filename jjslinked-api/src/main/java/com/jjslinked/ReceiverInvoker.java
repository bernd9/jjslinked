package com.jjslinked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;

public abstract class ReceiverInvoker {

    private static ParameterProvider DEFAULT_PROVIDER = new MessageParameterProvider();
    private static final ParameterProviderRegistry PROVIDER_REGISTRY = getProviderRegistry();

    private final Class<?> beanClass;
    private final Method method;
    private final MethodContext methodContext;

    public ReceiverInvoker(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        this.beanClass = beanClass;
        this.method = getMethod(beanClass, name, parameterTypes);
        this.methodContext = MethodContext.builder()
                .annotations(Arrays.asList(method.getDeclaredAnnotations()))
                .declaringClass(beanClass)
                .parameterTypes(Arrays.asList(method.getParameterTypes()))
                .build();
    }


    public void onMessage(ClientMessage message, ApplicationContext applicationContext) throws Exception {
        if (beanClass.getName().equals(message.getTargetClass())
                && message.getMethodName().equals(method.getName())
                && message.getParameterTypes().equals(Arrays.asList(method.getParameterTypes()))) {
            invoke(message, applicationContext);
        }
    }

    private void invoke(ClientMessage message, ApplicationContext applicationContext) throws Exception {
        Object bean = applicationContext.getBean(beanClass);
        method.invoke(bean, prepareArgs(message, applicationContext));
    }

    private Object[] prepareArgs(ClientMessage message, ApplicationContext applicationContext) {
        Object[] parameters = new Object[method.getParameters().length];
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
            parameters[parameterIndex] = getParameter(parameterIndex, message, applicationContext);
        }
        return parameters;
    }

    private Method getMethod(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        Method method;
        try {
            method = beanClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true);
        return method;
    }

    private Object getParameter(int index, ClientMessage message, ApplicationContext applicationContext) {
        ParameterProvider provider = getParameterProvider(applicationContext, getParameterAnnotations(index));
        return provider.getParameter(getParameterContext(index), message);
    }

    private ParameterContext getParameterContext(int index) {
        // TODO May be it's better to write it during annotation processing phase ?
        return ParameterContext.builder()
                .parameterType(getParameterType(index))
                .annotations(getParameterAnnotations(index))
                .paramName(getParameterName(index))
                .methodContext(methodContext)
                .build();
    }

    private Parameter getParameter(int index) {
        return method.getParameters()[index];
    }

    private Collection<? extends Annotation> getParameterAnnotations(int index) {
        return Arrays.asList(getParameter(index).getAnnotations());
    }

    private String getParameterName(int index) {
        return getParameter(index).getName();
    }

    private Class<?> getParameterType(int index) {
        return getParameter(index).getType();
    }


    private ParameterProvider getParameterProvider(ApplicationContext applicationContext, Collection<? extends Annotation> annotationClasses) {
        Class<? extends ParameterProvider> providerClass = PROVIDER_REGISTRY.getProviderClass(annotationClasses);
        if (providerClass == null) {
            return DEFAULT_PROVIDER;
        }
        return applicationContext.getBean(providerClass);
    }

    private static ParameterProviderRegistry getProviderRegistry() {
        try {
            return (ParameterProviderRegistry) Class.forName("com.jjslinked.generated.ProviderRegistry").getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
