package com.ejaf.processor.parameter;

import com.jjslinked.template.JavaTemplateModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ParameterProviderRegistryModel implements JavaTemplateModel {

    @Delegate
    private final Map<String, String> providerMapping;

    @Override
    public String getJavaClassQualifiedName() {
        return "com.ejaf.generated.ParameterProviderRegistry";
    }
}
