package com.ejc.processor.context.config;

import com.ejc.Singleton;
import com.ejc.Value;
import lombok.Getter;

@Singleton
class Test {

    @Getter
    @Value(value = "string")
    private String string;
}