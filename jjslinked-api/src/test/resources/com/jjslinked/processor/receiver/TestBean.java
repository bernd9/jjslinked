package com.jjslinked.processor.receiver;

import com.jjslinked.Receiver;

import javax.validation.constraints.NotNull;

public interface TestBean {

    @Receiver
    public void test(@NotNull String xyz);
}
