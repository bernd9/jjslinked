package com.ejc;

// TODO: Wird eingentlich nicht gebraucht. @Bean in @Singelton genügt
public @interface Configuration {
    String profile() default "";
}
