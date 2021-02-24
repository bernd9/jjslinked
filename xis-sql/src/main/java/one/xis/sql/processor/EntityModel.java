package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import com.ejc.util.JavaModelUtils;
import com.ejc.util.StringUtils;
import lombok.Getter;
import one.xis.sql.CrossTable;
import one.xis.sql.Entity;
import one.xis.sql.Id;
import one.xis.sql.NamingRules;
import one.xis.util.Pair;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
class EntityModel {

    private final TypeElement type;
    //private final List<EntityFieldModel> entityFields;
    private final EntityFieldModel idField;
    private final Set<CrossTableFieldModel> crossTableFields;


    private final String tableName;
    private final Map<VariableElement, ExecutableElement> getters;
    private final Map<VariableElement, ExecutableElement> setters;

    private static final Set<EntityModel> ENTITY_MODELS = new HashSet<>();


    EntityModel(TypeElement type, Types types) {
        this.type = type;
        this.tableName = tableName(type);
        Set<VariableElement> fields = type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toSet());

        GettersAndSetters gettersAndSetters = new GettersAndSetters(type, types);
        crossTableFields = fields.stream()
                .filter(field -> field.getAnnotation(CrossTable.class) != null)
                .map(field -> new CrossTableFieldModel(this, field, gettersAndSetters.getGetter(field), gettersAndSetters.getSetter(field)))
                .collect(Collectors.toUnmodifiableSet());
        idField = fields.stream()
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(field -> new EntityFieldModel(this, field, gettersAndSetters.getGetter(field), gettersAndSetters.getSetter(field)))
                .collect(CollectorUtils.toOnlyElement("@Id in " + type));
        // TODO other field types
        getters = gettersAndSetters.getGetters();
        setters = gettersAndSetters.getSetters();
        ENTITY_MODELS.add(this);
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

    public Collection<EntityFieldModel> getEntityFields() {
        Set<EntityFieldModel> set = new HashSet<>();
        set.add(idField);
        set.addAll(crossTableFields);
        return set; // TODO collect all types of field here
    }
}
