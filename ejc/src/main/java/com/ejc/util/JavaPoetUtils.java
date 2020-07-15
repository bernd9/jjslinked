package com.ejc.util;

import com.squareup.javapoet.CodeBlock;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Iterator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaPoetUtils {

    /**
     * The signature of this method is "signature(javax.lang.model.element.ExecutableElement)"
     * Does not contian the return type.
     *
     * @param e
     * @return
     */
    public static CodeBlock signatureBlock(ExecutableElement e) {
        CodeBlock.Builder builder = CodeBlock.builder()
                .add(e.getSimpleName().toString())
                .add("(");
        addClassNameList(e, builder);
        return builder.add(")")
                .build();
    }

    /**
     * List of parameter-types written as classes. Intended to use
     * to obtain a declared method:
     * <p>
     * Class#getMethod(Object,Class..types)
     * </p>
     *
     * @param e
     * @return
     */
    public static CodeBlock parameterTypeListBlock(ExecutableElement e) {
        CodeBlock.Builder builder = CodeBlock.builder();
        Iterator<? extends VariableElement> iterator = e.getParameters().iterator();
        while (iterator.hasNext()) {
            builder.add("$T.class", iterator.next().asType());
            if (iterator.hasNext()) {
                builder.add(",");
            }
        }
        return builder.build();
    }

    private static void addClassNameList(ExecutableElement e, CodeBlock.Builder builder) {
        Iterator<? extends VariableElement> iterator = e.getParameters().iterator();
        while (iterator.hasNext()) {
            builder.add("$T", iterator.next().asType());
            if (iterator.hasNext()) {
                builder.add(",");
            }
        }
    }


}
