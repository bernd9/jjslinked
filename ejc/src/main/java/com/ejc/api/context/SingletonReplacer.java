package com.ejc.api.context;

import com.ejc.api.context.model.SingletonModel;
import com.google.common.base.Functions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class SingletonReplacer {
    private final Map<ClassReference, SingletonModel> singletonModels;
    private Map<ClassReference, SingletonModel> replacements;

    void doReplacement() {
        collectReplacements();
        runReplaceLoop();
    }

    private void collectReplacements() {
        replacements = new HashMap<>(singletonModels.values().stream()
                .filter(model -> model.getReplaceClass().isPresent())
                .collect(Collectors.toMap(model -> model.getReplaceClass().get(), Functions.identity())));
    }

    private void runReplaceLoop() {
        while (!replacements.isEmpty()) {
            Set<ClassReference> matches = replacements.keySet().stream()
                    .filter(singletonModels.keySet()::contains)
                    .collect(Collectors.toSet());
            if (matches.isEmpty()) {
                if (!replacements.isEmpty()) {
                    throw new IllegalStateException("can not find singleton(s) to replace: "
                            + matches.stream().map(ClassReference::getClassName).collect(Collectors.joining(", ")));
                }
                return;
            }
            matches.forEach(classReference -> {
                @NonNull SingletonModel replacement = replacements.remove(classReference);
                singletonModels.put(classReference, replacement);
            });
        }
    }
}
