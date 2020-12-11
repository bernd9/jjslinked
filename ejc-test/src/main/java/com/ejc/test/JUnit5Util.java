package com.ejc.test;

import com.ejc.util.ClassUtils;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.extension.ExtensionContext;

@UtilityClass
public class JUnit5Util {
    public Object getTestInstance(ExtensionContext context) {
        return context.getTestInstance().orElseGet(() -> ClassUtils.createInstance(context.getTestClass().orElseThrow()));
    }

}
