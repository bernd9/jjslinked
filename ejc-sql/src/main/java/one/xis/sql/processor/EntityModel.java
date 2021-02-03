package one.xis.sql.processor;

import com.ejc.sql.Entity;
import lombok.Getter;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
        this.tableName = type.getAnnotation(Entity.class).value();
        this.entityFields = type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .map(field -> new EntityFieldModel(this, field, types))
                .collect(Collectors.toUnmodifiableList());
        entityModelsByTableName.put(this.tableName, this);
    }

    static EntityModel getEntityModelForTable(String tableName) {
        return entityModelsByTableName.get(tableName);
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
