package one.xis.sql.processor;

import lombok.Getter;

import javax.lang.model.element.VariableElement;

class SimpleEntityFieldModel extends FieldModel {
    @Getter
    protected final EntityModel entityModel;


    SimpleEntityFieldModel(EntityModel entityModel, VariableElement field, GettersAndSetters gettersAndSetters) {
        super(field, gettersAndSetters);
        this.entityModel = entityModel;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return entityModel.getTableName() + "." + getColumnName();
    }
}
