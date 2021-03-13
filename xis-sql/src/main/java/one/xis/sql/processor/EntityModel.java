package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import com.ejc.util.JavaModelUtils;
import com.ejc.util.StringUtils;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import one.xis.sql.*;
import one.xis.util.Pair;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
class EntityModel {

    private final TypeElement type;
    private final TypeName typeName;
    private final SimpleEntityFieldModel idField;
    private final Set<CrossTableFieldModel> crossTableFields;
    private final Set<ForeignKeyFieldModel> foreignKeyFields;
    private final Set<ReferredFieldModel> referredFields;
    private final Set<CollectionTableFieldModel> collectionTableFields;
    private final Set<SimpleEntityFieldModel> nonComplexFields;
    private final Set<JsonFieldModel> jsonFields;

    private final String tableName;

    private static final Set<EntityModel> ENTITY_MODELS = new HashSet<>();

    EntityModel(TypeElement typeElement, Types types) {
        type = typeElement;
        tableName = tableName(type);
        Set<VariableElement> fields = fields(type);
        GettersAndSetters gettersAndSetters = new GettersAndSetters(type, types);
        idField = idField(fields, gettersAndSetters);
        nonComplexFields = nonComplexFields(fields, gettersAndSetters);
        foreignKeyFields = foreignKeyFields(fields, gettersAndSetters);
        referredFields = referredFields(fields, gettersAndSetters);
        crossTableFields = crossTableFields(fields, gettersAndSetters);
        collectionTableFields = collectionTableFields(fields, gettersAndSetters);
        jsonFields = jsonFields(fields, gettersAndSetters);
        typeName = TypeName.get(type.asType());
        ENTITY_MODELS.add(this);
    }

    <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return type.getAnnotation(annotationType);
    }

    private Set<VariableElement> fields(TypeElement type) {
        return type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    private SimpleEntityFieldModel idField(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(field -> new EntityFieldModel(this, field, gettersAndSetters))
                .collect(CollectorUtils.toOnlyElement("@Id in " + type));
    }

    private Set<JsonFieldModel> jsonFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(Json.class) != null)
                .map(field -> new JsonFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<SimpleEntityFieldModel> nonComplexFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> JavaModelUtils.isNonComplex(field.asType()))
                .filter(field -> !JavaModelUtils.isCollection(field))
                .filter(field -> field.getAnnotation(Json.class) == null)
                .map(field -> new SimpleEntityFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<ForeignKeyFieldModel> foreignKeyFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(ForeignKey.class) != null)
                .map(field -> new ForeignKeyFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<ReferredFieldModel> referredFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(Referred.class) != null)
                .map(field -> new ReferredFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<CrossTableFieldModel> crossTableFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(CrossTable.class) != null)
                .map(field -> new CrossTableFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<CollectionTableFieldModel> collectionTableFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
        return fields.stream()
                .filter(field -> field.getAnnotation(CollectionTable.class) != null)
                .map(field -> new CollectionTableFieldModel(this, field, gettersAndSetters))
                .collect(Collectors.toUnmodifiableSet());
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

    public Collection<? extends SimpleEntityFieldModel> getAllFields() {
        Set<SimpleEntityFieldModel> set = new HashSet<>();
        set.addAll(nonComplexFields); // contains @Id field
        set.addAll(foreignKeyFields);
        set.addAll(referredFields);
        set.addAll(crossTableFields);
        set.addAll(collectionTableFields);
        return set;
    }

    public List<? extends SimpleEntityFieldModel> getAllFieldsInAlphabeticalOrder() {
        List<? extends SimpleEntityFieldModel> list = new ArrayList<>(getAllFields());
        Collections.sort(list, Comparator.comparing(f -> f.getFieldName().toString()));
        return list;

    }
}
