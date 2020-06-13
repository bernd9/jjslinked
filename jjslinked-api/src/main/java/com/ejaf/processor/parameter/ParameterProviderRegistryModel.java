package com.ejaf.processor.parameter;

import com.jjslinked.model.ClassModel;
import com.jjslinked.template.JavaTemplateModel;
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
    private ClassModel javaClass;
}
