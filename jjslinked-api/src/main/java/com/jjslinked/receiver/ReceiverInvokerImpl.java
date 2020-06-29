package com.jjslinked.receiver;

import com.ejc.ApplicationContextBase;
import com.jjslinked.ClientMessage;
import com.jjslinked.MethodContext;
import com.jjslinked.parameter.MessageParameterProvider;
import com.jjslinked.parameter.ParameterContext;
import com.jjslinked.parameter.ParameterProvider;
import com.jjslinked.parameter.ParameterProviderRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;

public abstract class ReceiverInvokerImpl implements ReceiverInvoker {

    private static Class<? extends ParameterProvider> DEFAULT_PROVIDER_CLASS = MessageParameterProvider.class;

    private final Class<?> beanClass;
    private final Method method;
    private final MethodContext methodContext;

    public ReceiverInvokerImpl(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        this.beanClass = beanClass;
        this.method = getMethod(beanClass, name, parameterTypes);
        this.methodContext = MethodContext.builder()
                .annotations(Arrays.asList(method.getDeclaredAnnotations()))
                .declaringClass(beanClass)
                .parameterTypes(Arrays.asList(method.getParameterTypes()))
                .build();
    }

    public String getBeanClass() {
        return beanClass.getName();
    }

    @Override
    public Object onMessage(ClientMessage message, ApplicationContextBase applicationContext) throws Exception {
        if (beanClass.getName().equals(message.getTargetClass())
                && message.getMethodName().equals(method.getName())
                && message.getParameterTypes().equals(Arrays.asList(method.getParameterTypes()))) {
            return invoke(message, applicationContext);
        }
        return null;
    }

    private Object invoke(ClientMessage message, ApplicationContextBase applicationContext) throws Exception {
        Object bean = applicationContext.getBean(beanClass);
        Object returnValue = method.invoke(bean, prepareArgs(message, applicationContext));
        return returnsVoid(method) ? Void.TYPE : returnValue;
    }

    private boolean returnsVoid(Method method) {
        return Void.TYPE.isAssignableFrom(method.getReturnType()) || Void.class.isAssignableFrom(method.getReturnType());
    }

    private Object[] prepareArgs(ClientMessage message, ApplicationContextBase applicationContext) {
        Object[] parameters = new Object[method.getParameters().length];
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
            parameters[parameterIndex] = getParameter(parameterIndex, message, applicationContext);
        }
        return parameters;
    }

    private Method getMethod(Class<?> beanClass, String name, Class<?>... parameterTypes) {
        Method method;
        try {
            method = beanClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true);
        return method;
    }

    private Object getParameter(int index, ClientMessage message, ApplicationContextBase applicationContext) {
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


    private ParameterProvider getParameterProvider(ApplicationContextBase applicationContext, Collection<? extends Annotation> annotationClasses) {
        ParameterProviderRegistry parameterProviderRegistry = applicationContext.getBean(ParameterProviderRegistry.class);
        Class<? extends ParameterProvider> providerClass = parameterProviderRegistry.getProviderClass(annotationClasses);
        if (providerClass == null) {
            providerClass = DEFAULT_PROVIDER_CLASS;
        }
        return applicationContext.getBean(providerClass);
    }
}
