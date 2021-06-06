package one.xis.processor.context.single.indirect;

import one.xis.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Singleton
public @interface TestAnnotation {
}
