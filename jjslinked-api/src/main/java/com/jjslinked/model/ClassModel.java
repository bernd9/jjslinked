package com.jjslinked.model;

import com.jjslinked.processor.util.AnnotationUtil;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.lang.model.element.TypeElement;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassModel {
    String qualifiedName;
    String packageName;
    String simpleName;
    Set<AnnotationModel> annotations;

    public static ClassModel fromElement(TypeElement e) {
        var qualifiedName = e.getQualifiedName().toString();
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }


}
