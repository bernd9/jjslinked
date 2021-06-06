package one.xis.test;

import java.lang.annotation.Repeatable;

@Repeatable(InjectConfigValues.class)
public @interface InjectConfigValue {
    String name();

    String value();
}

