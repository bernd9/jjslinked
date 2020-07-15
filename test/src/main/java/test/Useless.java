package test;

import com.ejc.AdviceClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@AdviceClass(UselessAdvice.class)
public @interface Useless {
}
