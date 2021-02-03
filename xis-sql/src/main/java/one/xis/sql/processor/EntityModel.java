package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import com.ejc.util.StringUtils;
import lombok.Getter;
import one.xis.sql.Entity;
import one.xis.sql.NamingRules;
import one.xis.util.Pair;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.stream.Collectors;

@Getter
class EntityModel {

    private final TypeElement type;
    private final List<EntityFieldModel> entityFields;
    private final String tableName;
    private final Map<VariableElement, ExecutableElement> getters;
    private final Map<VariableElement, ExecutableElement> setters;

    private static final Map<String, EntityModel> entityModelsByTableName = new HashMap<>();


    EntityModel(TypeElement type, Types types) {
        this.type = type;
        this.tableName = tableName(type);
        GettersAndSetters gettersAndSetters = new GettersAndSetters(type, types);
        this.entityFields = type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .map(field -> new EntityFieldModel(field, gettersAndSetters.getGetter(field), gettersAndSetters.getSetter(field)))
                .collect(Collectors.toUnmodifiableList());
        getters = gettersAndSetters.getGetters();
        setters = gettersAndSetters.getSetters();
        entityModelsByTableName.put(this.tableName, this);
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
        return null;
        //return entityModelsByTableName.get(tableName);
    }

    static Collection<EntityModel> allEntityModels() {
        return entityModelsByTableName.values();
    }

    static Pair<EntityModel,EntityModel> getEntityModelsByCrossTable(String crossTable) {
        Pair<EntityModel,EntityModel> models = new Pair<>();
        // TODO
        return models;
    }

    List<EntityFieldModel> getIdFields() {
        return entityFields.stream()
                .filter(EntityFieldModel::isId)
                .collect(Collectors.toList());
    }

    List<EntityFieldModel> getNonIdFields() {
        return entityFields.stream()
                .filter(EntityFieldModel::isId)
                .collect(Collectors.toList());
    }

    String getVarName() {
        return StringUtils.firstToLowerCase(getType().getSimpleName().toString());
    }

    String getImplVarName() {
        return StringUtils.firstToLowerCase(getType().getSimpleName().toString()) + "Impl";
    }

}
