package com.jjslinked.processor.registry;

import com.google.auto.service.AutoService;
import com.jjslinked.ast.ClassNode;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.jjslinked.Registry")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class RegistryAnnotationProcessor extends AbstractProcessor {

    private final Map<String, Registry> registries = new HashMap<>();
    private final RegistryTemplate registryTemplate = new RegistryTemplate();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        registries.clear();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (roundEnv.processingOver()) {
                log("number of registries : %d", registries.size());
                registries.values().forEach(this::process);
            } else {
                roundEnv.getElementsAnnotatedWith(com.jjslinked.Registry.class)
                        .stream()
                        .map(e -> (TypeElement) e)
                        .forEach(this::addRegistration);
            }
        } catch (Exception e) {
            reportError(e);
        }
        return true;
    }

    private void addRegistration(TypeElement e) {
        com.jjslinked.Registry registry = e.getAnnotation(com.jjslinked.Registry.class);
        String registryName = registry.name();
        String key = registry.key();
        String value = e.getQualifiedName().toString();
        registries.computeIfAbsent(registryName, RegistryAnnotationProcessor.Registry::new).getItems().put(key, value);
    }

    private void process(Registry registry) {
        log("*** processing registry %s", registry);
        Map<String, String> items = new HashMap<>(existingItems(registry.getName()));
        items.putAll(registry.getItems());
        writeTextFile(registry);
        writeRegistry(registry);

    }

    private void writeRegistry(Registry registry) {
        registryTemplate.write(registryClass(registry), processingEnv.getFiler());
    }

    private RegistryModel registryClass(Registry registry) {
        ClassNode registryClass = ClassNode.builder()
                .packageName(CodeGeneratorUtils.getPackageName(registry.getName()))
                .simpleName(CodeGeneratorUtils.getSimpleName(registry.getName()))
                .qualifiedName(registry.getName())
                .build();

        return RegistryModel.builder()
                .registryClass(registryClass)
                .registryItems(registry.getItems())
                .build();
    }


    private void writeTextFile(Registry registry) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(registryTextFileForWrite(registry.getName()).openOutputStream()))) {
            registry.getItems().entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).forEach(writer::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> existingItems(String registry) {
        Map<String, String> items = new HashMap<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(registryTextFileForRead(registry).openInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] pair = line.split(":");
                items.put(pair[0], pair[1]);
            }
            return items;
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    private FileObject registryTextFileForRead(String registry) {
        try {
            return processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", registry + ".registry");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileObject registryTextFileForWrite(String registry) {
        try {
            return processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", registry + ".registry");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reportError(Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
    }

    private void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

    @Getter
    @RequiredArgsConstructor
    class Registry {

        private final String name;

        private Map<String, String> items = new HashMap<>();
    }
}