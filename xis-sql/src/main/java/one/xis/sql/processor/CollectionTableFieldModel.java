package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import one.xis.sql.CollectionTable;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

// TODO table-accessor-class for this
public class CollectionTableFieldModel extends SimpleEntityFieldModel {

    private final CollectionTable collectionTable;

    CollectionTableFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(entityModel, field, gettersAndSetters);
        collectionTable = field.getAnnotation(CollectionTable.class);
    }

    String getCollectionTable() {
        return collectionTable.tableName();
    }

    String getForeignKeyColumnName() {
        return collectionTable.foreignColumnName();
    }

    TypeMirror getCollectionType() {
        return field.asType();
    }

    TypeMirror getGenericType() {
        return JavaModelUtils.getGenericCollectionType(field);
    }
}
