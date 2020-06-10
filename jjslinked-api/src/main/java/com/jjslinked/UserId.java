package com.jjslinked;

import com.ejaf.Provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Provider(UserIdParameterProvider.class)
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface UserId {
}
