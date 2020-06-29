package com.jjslinked.processor.emitter;

import com.jjslinked.ast.ClassNode;
import com.jjslinked.ast.MethodNode;
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
    ClassNode classNode;
    ClassNode superClass;
    MethodNode superClassConstructor; // TODO validate one class to have one constructor, only
    List<MethodNode> emitters;
    String inheritance;


    @Override
    public ClassNode getJavaClass() {
        return classNode;
    }
}
