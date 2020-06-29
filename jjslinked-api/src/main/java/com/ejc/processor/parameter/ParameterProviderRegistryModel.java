package com.ejc.processor.parameter;

import com.injectlight.template.JavaTemplateModel;
import com.jjslinked.ast.ClassNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ParameterProviderRegistryModel implements JavaTemplateModel {

    @Delegate
    private final Map<String, String> providerMapping;
    private ClassNode javaClass;
}
