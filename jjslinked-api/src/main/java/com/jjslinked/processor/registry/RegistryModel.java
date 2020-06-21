package com.jjslinked.processor.registry;

import com.jjslinked.ast.ClassNode;
import com.jjslinked.template.JavaTemplateModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.Map;

@Builder
@Getter
class RegistryModel implements JavaTemplateModel {

    private ClassNode registryClass;

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
