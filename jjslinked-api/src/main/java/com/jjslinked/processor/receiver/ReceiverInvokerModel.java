package com.jjslinked.processor.receiver;

import com.jjslinked.model.ClassModel;
import com.jjslinked.model.MethodModel;
import com.jjslinked.template.JavaTemplateModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)

public class ReceiverInvokerModel implements JavaTemplateModel {
    ClassModel invoker;
    MethodModel methodToInvoke;

    @Override
    public ClassModel getJavaClass() {
        return invoker;
    }
}
