package com.ejc.sql.processor;

import com.ejc.sql.Entity;
import com.ejc.util.JavaModelUtils;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private EntityModelFactory entityModelFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        entityModelFactory = new EntityModelFactory(processingEnv);
        super.init(processingEnv);
    }

    private Set<String> ANNOTATIONS = Set.of(Entity.class).stream().map(Class::getName).collect(Collectors.toSet());

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ANNOTATIONS;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(Entity.class)
                .stream()
                .map(TypeElement.class::cast)
                .forEach(this::processEntity);
        return false;
    }

    private void processEntity(TypeElement entityElement) {
        EntityModel entityModel = entityModelFactory.createEntityModel(entityElement);
        writeEntityProxy(entityModel);
        writeRepository(entityModel);
    }

    private void writeEntityManager(EntityModel entityModel) {
        String simpleName = JavaModelUtils.getSimpleName(entityModel.getEntityType() + "EntityManager");
        String packageName = JavaModelUtils.getPackageName(entityModel.getEntityType());
        Optional<String> packageOptional = packageName.isEmpty() ? Optional.empty() : Optional.of(packageName);
        try {
            new EntityProxyWriter(simpleName, packageOptional, Optional.of(ClassName.get(entityModel.getEntityType())), processingEnv, entityModel).write();
        } catch (IOException e) {
            reportError(e);
        }
    }

    private void writeEntityProxy(EntityModel entityModel) {
        String simpleName = JavaModelUtils.getSimpleName(entityModel.getEntityType() + "Proxy");
        String packageName = JavaModelUtils.getPackageName(entityModel.getEntityType());
        Optional<String> packageOptional = packageName.isEmpty() ? Optional.empty() : Optional.of(packageName);
        try {
            new EntityProxyWriter(simpleName, packageOptional, Optional.of(ClassName.get(entityModel.getEntityType())), processingEnv, entityModel).write();
        } catch (IOException e) {
            reportError(e);
        }
    }

    private void writeRepository(EntityModel entityModel) {
        String simpleName = JavaModelUtils.getSimpleName(entityModel.getEntityType() + "Repository");
        String packageName = JavaModelUtils.getPackageName(entityModel.getEntityType());
        Optional<String> packageOptional = packageName.isEmpty() ? Optional.empty() : Optional.of(packageName);
        try {
            new RepositoryWriter(simpleName, packageOptional, processingEnv, entityModel).write();
        } catch (IOException e) {
            reportError(e);
        }
    }

    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }


}
