package com.ejc.sql;

import com.ejc.AdviceClass;
import com.ejc.sql.api.TransactionalMethodHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@AdviceClass(TransactionalMethodHandler.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    int isolationLevel() default -1;
}
