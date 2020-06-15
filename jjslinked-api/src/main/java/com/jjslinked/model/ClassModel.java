package com.jjslinked.model;

import com.jjslinked.processor.util.AnnotationUtil;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.Set;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassModel {
    String qualifiedName;
    String packageName;
    String simpleName;
    Set<AnnotationModel> annotations;

    public static ClassModel from(TypeElement e) {
        var qualifiedName = e.getQualifiedName().toString();
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }

    public static ClassModel from(TypeMirror e) {
        var qualifiedName = stripAnnotions(e.toString());
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e.getAnnotationMirrors()))
                .build();
    }

    public static ClassModel from(String qualifiedName) {
        qualifiedName = stripAnnotions(qualifiedName);
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(Collections.emptySet())
                .build();
    }

    public static ClassModel from(String qualifiedName, Set<AnnotationModel> annotations) {
        qualifiedName = stripAnnotions(qualifiedName);
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(annotations)
                .build();
    }

    public static ClassModel from(VariableElement e) {
        var qualifiedName = stripAnnotions(e.asType().toString());
        return ClassModel.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }

    private static String stripAnnotions(String type) {
        int lastIndex = type.lastIndexOf(' ');
        return lastIndex == -1 ? type : type.substring(lastIndex + 1);
    }
}