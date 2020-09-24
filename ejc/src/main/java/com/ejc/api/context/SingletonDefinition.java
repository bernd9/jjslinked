package com.ejc.api.context;

import com.ejc.context2.ConfigValueField;
import com.ejc.context2.DependencyField;
import com.ejc.context2.InitMethod;
import com.ejc.context2.SingletonConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
class SingletonDefinition {
    private final SingletonConstructor constructor;
    private final Set<InitMethod> initMethods;
    private final Set<ConfigValueField> configValueFields;
    private final Set<DependencyField> dependencyFields;

    void onSingletonCreated(Object o) {
        constructor.onSingletonCreated(o);
        dependencyFields.forEach(field -> field.onSingletonCreated(o));
    }

    void injectConfigValues(Object o) {
        configValueFields.stream().forEach(field -> field.doInject(o));
    }


}
