package com.ejc.sql.processor;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.TypeElement;
import java.util.Set;

@Getter
@Builder
class EntityModel {
    private final TypeElement entityType;
    private final String tableName;
    private final Set<EntityField> primaryKeyFields;
    private final Set<EntityField> simpleFields;
    private final Set<EntityField> singleEntityFields;
    private final Set<EntityField> singleComplexEntityFields;
    private final Set<EntityField> singleComplexEntityFieldsForeignTable;
    private final Set<EntityCollectionField> complexEntityCollectionFieldsForeignTable;
    private final Set<EntityCollectionField> complexEntityCollectionFieldsCrossTable;
}
