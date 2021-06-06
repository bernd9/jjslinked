package one.xis.sql;

import one.xis.AdviceClass;
import one.xis.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Singleton
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@AdviceClass(value = ServiceAdvice.class, priority = 2000)
public @interface Service {

}
