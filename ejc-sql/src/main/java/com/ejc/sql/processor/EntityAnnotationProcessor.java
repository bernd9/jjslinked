package com.ejc.sql.processor;

import com.ejc.sql.*;
import com.ejc.sql.api.ORNameMapper;
import com.ejc.util.JavaModelUtils;
import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private AccessMethodUtil accessMethodUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        accessMethodUtil = new AccessMethodUtil(processingEnv);
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
        Entity entityAnnotation = entityElement.getAnnotation(Entity.class);
        EntityModel entityModel = EntityModel.builder()
                .tableName(entityAnnotation.value())
                .entityType(entityElement)
                .primaryKeyFields(primaryKeyFields(entityElement))
                .simpleFields(simpleFields(entityElement))
                .singleComplexEntityFields(singleComplexEntityField(entityElement))
                .singleComplexEntityFieldsForeignTable(singleComplexEntityFieldsForeignTable(entityElement))
                .complexEntityCollectionFieldsForeignTable(complexEntityCollectionFieldsForeignTable(entityElement))
                .complexEntityCollectionFieldsCrossTable(complexEntityCollectionFieldsCrossTable(entityElement))
                .build();
        writeEntityImpl(entityModel);
    }

    private void writeEntityImpl(EntityModel entityModel) {
        String simpleName = JavaModelUtils.getSimpleName(entityModel.getEntityType());
        String packageName = JavaModelUtils.getPackageName(entityModel.getEntityType());
        Optional<String> packageOptional = packageName.isEmpty() ? Optional.empty() : Optional.of(packageName);
        try {
            new EntityImplWriter(simpleName, packageOptional, Optional.of(ClassName.get(entityModel.getEntityType())), processingEnv, entityModel).write();
        } catch (IOException e) {
            reportError(e);
        }

    }


    private Set<EntityField> primaryKeyFields(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(Id.class) != null)
                .peek(this::validateNonComplex)
                .map(e -> new EntityField(e, getTableName(entity), getColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private Set<EntityField> simpleFields(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(Id.class) == null)
                .filter(EntityAnnotationProcessor::isNonComplex)
                .map(e -> new EntityField(e, getTableName(entity), getColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private Set<EntityField> singleComplexEntityField(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(ForeignKeyRef.class) != null)
                .filter(e -> isLocalForeignKey(entity, e))
                .peek(this::validateIsNoCollectionField)
                .peek(this::validateIsEntityField)
                .map(e -> new EntityField(e, getForeignKeyTableName(e), getForeignKeyColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private Set<EntityField> singleComplexEntityFieldsForeignTable(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(ForeignKeyRef.class) != null)
                .filter(e -> !isLocalForeignKey(entity, e))
                .filter(e -> !isCollectionField(e))
                .peek(this::validateIsEntityField)
                .map(e -> new EntityField(e, getForeignKeyTableName(e), getForeignKeyColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private Set<EntityCollectionField> complexEntityCollectionFieldsForeignTable(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(ForeignKeyRef.class) != null)
                .filter(e -> !isLocalForeignKey(entity, e))
                .filter(e -> isCollectionField(e))
                .map(e -> new EntityCollectionField(e, getForeignKeyTableName(e), getForeignKeyColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private Set<EntityCollectionField> complexEntityCollectionFieldsCrossTable(TypeElement entity) {
        return entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .filter(e -> e.getAnnotation(CrossTableRef.class) != null)
                .filter(e -> isCollectionField(e))
                .map(e -> new EntityCollectionField(e, getForeignKeyTableName(e), getForeignKeyColumnName(e), getGetter(e), getSetter(e)))
                .collect(Collectors.toSet());
    }

    private boolean isLocalForeignKey(TypeElement owner, VariableElement field) {
        return getTableName(owner).equals(getForeignKeyTableName(field));
    }

    private static String getColumnName(VariableElement e) {
        Column column = e.getAnnotation(Column.class);
        if (column == null || column.name().isEmpty()) {
            return ORNameMapper.toSqlName(e.getSimpleName().toString());
        }
        return column.name();
    }

    private Optional<ExecutableElement> getGetter(VariableElement field) {
        return accessMethodUtil.getGetter(field);
    }

    private Optional<ExecutableElement> getSetter(VariableElement field) {
        return accessMethodUtil.getSetter(field);
    }

    private static String getForeignKeyColumnName(VariableElement e) {
        return e.getAnnotation(ForeignKeyRef.class).column();
    }

    private static String getForeignKeyTableName(VariableElement e) {
        return e.getAnnotation(ForeignKeyRef.class).table();
    }

    private String getTableName(TypeElement entityType) {
        return entityType.getAnnotation(Entity.class).value();
    }

    private void validateNonComplex(VariableElement e) {
        if (!isNonComplex(e)) {
            throw new IllegalStateException(e + " must be non-complex");
        }
    }

    private static boolean isNonComplex(VariableElement e) {
        return JavaModelUtils.isNonComplex(e.asType());
    }

    private void validateIsEntityField(VariableElement e) {
        if (!isEntityField(e)) {
            throw new IllegalStateException(e + " must be an entity-field");
        }
    }

    private boolean isEntity(TypeMirror mirror) {
        return mirror.getAnnotation(Entity.class) != null;
    }

    private boolean isEntityField(VariableElement e) {
        return e.getAnnotation(Entity.class) != null;
    }

    private void validateIsNoCollectionField(VariableElement e) {
        if (JavaModelUtils.isCollection(e)) {
            throw new IllegalStateException(e + " can not be collection");
        }
    }

    private boolean isCollectionField(VariableElement e) {
        return JavaModelUtils.isCollection(e);
    }


    protected void reportError(Exception e) {
        ProcessorLogger.reportError(this, processingEnv, e);
    }


}
