package com.jjslinked.ast;

import com.jjslinked.processor.util.AnnotationUtil;
import com.jjslinked.processor.util.CodeGeneratorUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

public class ClassNodeBuilder {
    public static ClassNode from(TypeElement e) {
        var qualifiedName = e.getQualifiedName().toString();
        return ClassNode.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e))
                .build();
    }

    public static ClassNode from(TypeMirror e) {
        var qualifiedName = stripAnnotions(e.toString());
        return ClassNode.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(AnnotationUtil.getAnnotations(e.getAnnotationMirrors()))
                .build();
    }

    // use model class instead
    @Deprecated
    public static ClassNode from(String qualifiedName, Set<AnnotationModel> annotations) {
        qualifiedName = stripAnnotions(qualifiedName);
        return ClassNode.builder()
                .qualifiedName(qualifiedName)
                .packageName(CodeGeneratorUtils.getPackageName(qualifiedName))
                .simpleName(CodeGeneratorUtils.getSimpleName(qualifiedName))
                .annotations(annotations)
                .build();
    }

    public static ClassNode from(VariableElement e) {
        var qualifiedName = stripAnnotions(e.asType().toString());
        return ClassNode.builder()
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
