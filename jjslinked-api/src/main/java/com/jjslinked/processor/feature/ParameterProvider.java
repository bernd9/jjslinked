package com.jjslinked.processor.feature;

import com.jjslinked.processor.RequestContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterProvider {

    Class<? extends Annotation> getAnnotation();

    void validate(Method method, int parameterIndex, Parameter parameter) throws FeatureValidationException;

    <T> T provide(RequestContext context, Class<T> type);

}
