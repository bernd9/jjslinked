package com.jjslinked.receiver;

import com.ejaf.MethodAdvice;
import com.ejaf.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

class ReceiverMethodAdvice implements MethodAdvice {

    @Override
    public Object invoke(Method method, Set<? extends Annotation> annotations, List<MethodParameter> parameters) throws Throwable {
        return null;
    }

    @Override
    public boolean invoke() {
        return false;
    }
}
