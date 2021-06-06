package one.xis.processor.context.value;

import one.xis.Singleton;
import one.xis.Value;
import lombok.Getter;

@Singleton
class Test {

    @Getter
    @Value(value = "string")
    private String string;
}