package com.jjslinked.model;

import com.jjslinked.processor.util.AnnotationUtil;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.lang.model.element.VariableElement;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ParameterModel {
    String paramName;
    String qualifiedName;
    String packageName;
    String simpleName;
    Set<AnnotationModel> annotations;

    public static ParameterModel fromParameter(VariableElement e) {
        var qualifiedName = e.asType().toString();
        return ParameterModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }
}
