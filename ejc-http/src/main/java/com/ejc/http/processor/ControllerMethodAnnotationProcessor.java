package com.ejc.http.processor;

import com.ejc.http.Get;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class ControllerMethodAnnotationProcessor extends AbstractProcessor {


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(Get.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    private void processMethod(ExecutableElement e) {


    }

}