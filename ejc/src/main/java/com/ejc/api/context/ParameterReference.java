package com.ejc.api.context;

import com.ejc.processor.ModuleFactoryWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParameterReference {
    private final ClassReference classReference;
    private final String name;

    @UsedInGeneratedCode(ModuleFactoryWriter.class)
    public static ParameterReference getRef(ClassReference classReference, String name) {
        return new ParameterReference(classReference, name);
    }
}
