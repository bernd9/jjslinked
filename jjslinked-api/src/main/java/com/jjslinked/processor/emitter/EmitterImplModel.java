package com.jjslinked.processor.emitter;

import com.jjslinked.model.ClassModel;
import com.jjslinked.model.MethodModel;
import com.jjslinked.template.JavaTemplateModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class EmitterImplModel implements JavaTemplateModel {
    ClassModel classModel;
    ClassModel superClass;
    MethodModel superClassConstructor; // TODO validate one class to have one constructor, only
    List<MethodModel> emitters;
    String inheritance;


    @Override
    public ClassModel getJavaClass() {
        return classModel;
    }
}
