package one.xis.sql.processor;

import lombok.Getter;
import one.xis.sql.Entity;
import one.xis.util.Pair;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
class EntityModel {

    private final TypeElement type;
    private final List<EntityFieldModel> entityFields;
    private final String tableName;

    private static final Map<String, EntityModel> entityModelsByTableName = new HashMap<>();


    EntityModel(TypeElement type, Types types) {
        this.type = type;
        this.tableName = type.getAnnotation(Entity.class).tableName(); // TODO tableName might be empty
        this.entityFields = type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .map(field -> new EntityFieldModel(this, field, types))
                .collect(Collectors.toUnmodifiableList());
        entityModelsByTableName.put(this.tableName, this);
    }

    static EntityModel getEntityModel(TypeMirror entityMirror) {
        return null;
        //return entityModelsByTableName.get(tableName);
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


}
