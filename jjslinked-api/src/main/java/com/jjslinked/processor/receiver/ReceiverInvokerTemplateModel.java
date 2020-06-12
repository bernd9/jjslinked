package com.jjslinked.processor.receiver;

import com.jjslinked.model.ClassModel;
import com.jjslinked.template.JavaTemplateModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReceiverInvokerTemplateModel implements JavaTemplateModel {
    ClassModel javaClass;
}
