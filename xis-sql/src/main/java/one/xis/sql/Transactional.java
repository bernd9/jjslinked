package one.xis.sql;


import com.ejc.AdviceClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) // checked at runtime !
@AdviceClass(value = TransactionalAdvice.class, priority = 1000)
public @interface Transactional {
    int isolationLevel() default Connection.TRANSACTION_NONE;
}
