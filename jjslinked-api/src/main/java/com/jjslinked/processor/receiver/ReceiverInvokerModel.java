package com.jjslinked.processor.receiver;

import com.jjslinked.ast.ClassNode;
import com.jjslinked.ast.MethodNode;
import com.jjslinked.template.JavaTemplateModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReceiverInvokerModel implements JavaTemplateModel {
    ClassNode invoker;
    MethodNode methodToInvoke;

    @Override
    public ClassNode getJavaClass() {
        return invoker;
    }

}
