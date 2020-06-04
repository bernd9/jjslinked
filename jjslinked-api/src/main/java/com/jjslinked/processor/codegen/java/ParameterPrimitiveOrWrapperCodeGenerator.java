package com.jjslinked.processor.codegen.java;

import com.jjslinked.validation.Validator;

import javax.tools.FileObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ParameterPrimitiveOrWrapperCodeGenerator extends JavaCodeGenerator<ParameterPrimitiveOrWrapperCodeTemplate, ParameterPrimitiveOrWrapperModel> {

    private Set<Class<? extends Validator>> validators;

    ParameterPrimitiveOrWrapperCodeGenerator() {
        super(new ParameterPrimitiveOrWrapperCodeTemplate());
    }

    @Override
    void write(ParameterPrimitiveOrWrapperModel context, FileObject fileObject) throws IOException {
        this.validators = context.getValidators();
        super.write(context, fileObject);
    }

    @Override
    Set<ImportModel> getImports() {
        Set<ImportModel> importModels = new HashSet<>(super.getImports());
        importModels.addAll(validators.stream().map(ImportModel::new).collect(Collectors.toSet()));
        return importModels;
    }
}
