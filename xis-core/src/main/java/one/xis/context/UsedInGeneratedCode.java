package one.xis.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface UsedInGeneratedCode {
    Class<?> value() default UndefinedClass.class;
}
