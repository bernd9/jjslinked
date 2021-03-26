package one.xis.sql;

import com.ejc.AdviceClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@AdviceClass(SessionInvocationHandler.class)
public @interface Service {

}
