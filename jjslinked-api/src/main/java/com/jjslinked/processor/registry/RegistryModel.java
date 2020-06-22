package com.jjslinked.processor.registry;

import com.injectlight.template.JavaTemplateModel;
import com.jjslinked.ast.ClassNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.Map;

@Builder
@Getter
class RegistryModel implements JavaTemplateModel {

    private ClassNode registryClass;
    private String registrySuperClass;

    @Singular
    private Map<String, String> registryItems;

    @Override
    public ClassNode getJavaClass() {
        return registryClass;
    }

    public Collection<Map.Entry<String, String>> getEntries() {
        return registryItems.entrySet();
    }
}
