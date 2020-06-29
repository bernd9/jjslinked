package com.jjslinked.processor.registry;

import com.ejc.util.IOUtils;
import com.google.auto.service.AutoService;
import com.jjslinked.ast.ClassNode;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.Registry")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class RegistryAnnotationProcessor extends AbstractProcessor {

    private final RegistryTemplate registryTemplate = new RegistryTemplate();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!roundEnv.processingOver()) {
                Map<String, RegistryMapping> registries = new HashMap<>();
                roundEnv.getElementsAnnotatedWith(com.jjslinked.Registry.class)
                        .stream()
                        .map(e -> (TypeElement) e)
                        .forEach(e -> addRegistration(e, registries));
                registries.values().forEach(this::process);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void addRegistration(TypeElement e, Map<String, RegistryMapping> registries) {
        com.jjslinked.Registry registry = e.getAnnotation(com.jjslinked.Registry.class);
        String registryName = registry.name();
        String key = registry.key();
        String value = e.getQualifiedName().toString();
        registries.computeIfAbsent(registryName, name -> new RegistryMapping(name, registry.superClass())).getItems().put(key, value);
    }

    private void process(RegistryMapping registryMapping) {
        log("*** processing registryMapping %s", registryMapping);
        Map<String, String> items = new HashMap<>(existingItems(registryMapping));
        items.putAll(registryMapping.getItems());
        writeTextFile(registryMapping);
        writeRegistry(registryMapping);

    }

    private void writeRegistry(RegistryMapping registryMapping) {
        registryTemplate.write(registryClass(registryMapping), processingEnv.getFiler());
    }

    private RegistryModel registryClass(RegistryMapping registryMapping) {
        ClassNode registryClass = ClassNode.builder()
                .packageName(CodeGeneratorUtils.getPackageName(registryMapping.getName()))
                .simpleName(CodeGeneratorUtils.getSimpleName(registryMapping.getName()))
                .qualifiedName(registryMapping.getName())
                .build();

        return RegistryModel.builder()
                .registryClass(registryClass)
                .registrySuperClass(registryMapping.getSuperClass())
                .registryItems(registryMapping.getItems())
                .build();
    }


    private void writeTextFile(RegistryMapping registryMapping) {
        IOUtils.write(registryMapping.getItems().entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.toSet()), processingEnv.getFiler(), registryMapping.getName());

    }

    private Map<String, String> existingItems(RegistryMapping registryMapping) {
        return IOUtils.read(processingEnv.getFiler(), registryMapping.getName()).stream()
                .map(line -> line.split(":"))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

    @Getter
    @RequiredArgsConstructor
    class RegistryMapping {

        private final String name;
        private final String superClass;

        private Map<String, String> items = new HashMap<>();
    }
}