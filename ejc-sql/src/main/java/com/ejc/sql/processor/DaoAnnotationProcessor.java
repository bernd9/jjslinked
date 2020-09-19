package com.ejc.sql.processor;

import com.ejc.sql.CrudRepository;
import com.ejc.sql.SqlDao;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class DaoAnnotationProcessor extends AbstractProcessor {

    private TypeMirror crudRepositoryMirror;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        crudRepositoryMirror = processingEnv.getElementUtils().getTypeElement(CrudRepository.class.getName()).asType();
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(SqlDao.class).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("entity-processing ");
        if (!roundEnv.processingOver()) {

        }
        return true;
    }

    private void processElements(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SqlDao.class)
                .stream().map(TypeElement.class::cast)
                .forEach(this::processElement);
    }

    private void processElement(TypeElement dao) {
        DaoClassWriter writer = DaoClassWriter.builder()
                .crudRepository(implementsCrudRespository(dao))
                .dao(dao)
                .processingEnvironment(processingEnv)
                .build();
        try {
            writer.write();
        } catch (Exception e) {
            ProcessorLogger.reportError(this, processingEnv, e);
        }
    }

    private boolean implementsCrudRespository(TypeElement dao) {
        return dao.getInterfaces().stream()
                .anyMatch(interf -> processingEnv.getTypeUtils().isAssignable(interf, crudRepositoryMirror));
    }

    private String randomClassName() {
        return "Invoker_" + UUID.randomUUID().toString().replace("-", "");
    }

    protected void log(String message, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + ": " + String.format(message, args));
    }


}