package one.xis.sql;

import com.ejc.AdviceClass;
import com.ejc.Singleton;

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
