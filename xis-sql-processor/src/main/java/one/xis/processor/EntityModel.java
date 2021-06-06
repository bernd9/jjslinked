package one.xis.processor;

import one.xis.util.JavaModelUtils;
import one.xis.util.StringUtils;
import com.squareup.javapoet.TypeName;
import lombok.*;
import one.xis.sql.*;
import one.xis.util.Pair;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;

@Data
class EntityModel {
    private TypeElement type;
    private TypeName typeName;
    private SimpleEntityFieldModel idField;
    private Set<CrossTableFieldModel> crossTableFields;
    private Set<ForeignKeyFieldModel> foreignKeyFields;
    private Set<ReferencedFieldModel> referredFields;
    private Set<CollectionTableFieldModel> collectionTableFields;
    private Set<SimpleEntityFieldModel> nonComplexFields;
    private Set<JsonFieldModel> jsonFields;
    private String tableName;

    private static final Set<EntityModel> ENTITY_MODELS = new HashSet<>();

    EntityModel() {
        ENTITY_MODELS.add(this);
    }


    <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return type.getAnnotation(annotationType);
    }

    String getProxySimpleName() {
        return getSimpleName() + "Proxy";
    }

    String getSimpleName() {
        return JavaModelUtils.getSimpleName(type);
    }

    String getPackageName() {
        return JavaModelUtils.getPackageName(type);
    }

    private static String tableName(TypeElement entityType) {
        String nameInAnnotation = entityType.getAnnotation(Entity.class).tableName();
        return nameInAnnotation.isEmpty() ? NamingRules.toSqlName(entityType.getSimpleName().toString()) : nameInAnnotation;
    }

    static EntityModel getEntityModel(TypeMirror entityMirror) {
        return ENTITY_MODELS.stream()
                .filter(model -> model.getType().asType().equals(entityMirror))
                .findFirst().orElseThrow();
    }

    static Collection<EntityModel> allEntityModels() {
        return ENTITY_MODELS;
    }

    static Pair<EntityModel, EntityModel> getEntityModelsByCrossTable(String crossTable) {
        Pair<EntityModel, EntityModel> models = new Pair<>();
        // TODO
        return models;
    }

    String getVarName() {
        return StringUtils.firstToLowerCase(getType().getSimpleName().toString());
    }

    String getImplVarName() {
        return StringUtils.firstToLowerCase(getType().getSimpleName().toString()) + "Impl";
    }

    public Collection<? extends FieldModel> getAllFields() {
        Set<FieldModel> set = new HashSet<>();
        set.addAll(nonComplexFields); // contains @Id field
        set.addAll(foreignKeyFields);
        set.addAll(referredFields);
        set.addAll(crossTableFields);
        set.addAll(collectionTableFields);
        return set;
    }
    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), getType());
    }
}
