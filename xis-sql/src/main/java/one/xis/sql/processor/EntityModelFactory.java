
package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import com.ejc.util.JavaModelUtils;
import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class EntityModelFactory {
    private final ProcessingEnvironment processingEnvironment;


    EntityModel createEntityModel(TypeElement type) {
        EntityModelProducer producer = new EntityModelProducer(type);
        return producer.produce();
    }

    void postsAssignReferredFields(EntityModel entityModel, Set<ForeignKeyFieldModel> allForeignKeyFields) {
        EntityReferredFieldFinder fieldFinder = new EntityReferredFieldFinder(entityModel, allForeignKeyFields);
        entityModel.setReferredFields(fieldFinder.findReferredFields());
    }

    private Types getTypeUtils() {
        return processingEnvironment.getTypeUtils();
    }

    private Elements getElementUtils() {
        return processingEnvironment.getElementUtils();
    }

    private Set<VariableElement> fields(TypeElement type) {
        return type.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    @RequiredArgsConstructor
    class EntityReferredFieldFinder {
        private final EntityModel entityModel;
        private final Set<ForeignKeyFieldModel> allForeignKeyFields;

        Set<ReferencedFieldModel> findReferredFields() {
            return referredFields(fields(entityModel.getType()), new GettersAndSetters(entityModel.getType(), getTypeUtils()));
        }

        private Set<ReferencedFieldModel> referredFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return Stream.concat(referredFieldsByAnnotation(fields, gettersAndSetters),
                    referredFieldsByUniqueness(fields, gettersAndSetters)
            ).collect(Collectors.toUnmodifiableSet());
        }

        private Stream<ReferencedFieldModel> referredFieldsByAnnotation(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(Referenced.class) != null && !field.getAnnotation(Referenced.class).externalColumnName().isEmpty())
                    .map(field -> new ReferencedFieldModel(entityModel, field, gettersAndSetters, getMatchingForeignKeyByColumnNameInAnnotation(field)));
        }


        private Stream<ReferencedFieldModel> referredFieldsByUniqueness(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(this::isEntityField)
                    .filter(this::hasNoEntityFieldAnnotations)
                    .map(field -> new ReferencedFieldModel(entityModel, field, gettersAndSetters, getMatchingForeignKeyByType(field)));
        }

        private ForeignKeyFieldModel getMatchingForeignKeyByType(VariableElement referredField) {
            TypeMirror referredFieldType = fieldElementType(referredField);
            return allForeignKeyFields.stream().filter(field -> getTypeUtils().isAssignable(field.getFieldType(), referredFieldType)).collect(uniqueForeignKeyByFieldType(referredField, referredFieldType));
        }

        private ForeignKeyFieldModel getMatchingForeignKeyByColumnNameInAnnotation(VariableElement referredField) {
            String foreignKeyColumnName = referredField.getAnnotation(Referenced.class).externalColumnName();
            return allForeignKeyFields.stream().filter(field -> field.getColumnName().equals(foreignKeyColumnName)).collect(uniqueForeignKeyByForeignKeyColumnName(referredField, foreignKeyColumnName));
        }

        private Collector<ForeignKeyFieldModel, ?, ForeignKeyFieldModel> uniqueForeignKeyByFieldType(VariableElement referredField, TypeMirror referringEntityType) {
            String fieldDescription = referredField.getEnclosingElement().toString() + "." + referredField.getSimpleName();
            return Collectors.collectingAndThen(Collectors.toList(), list -> {
                switch (list.size()) {
                    case 0: throw new IllegalStateException(String.format("No foreign key field found for %s. Possibly field annotated with @ForeignKey is missing in %s.", fieldDescription, referringEntityType));
                    case 1: return list.get(0);
                    default: throw new IllegalStateException(String.format("Foreign key reference %s -> %s is ambiguous. Add a @ReferringColumn to %s to fix.", referringEntityType, fieldDescription, fieldDescription));
                }
            });
        }

        private Collector<ForeignKeyFieldModel, ?, ForeignKeyFieldModel> uniqueForeignKeyByForeignKeyColumnName(VariableElement referredField, String foreignKeyColumnName) {
            String fieldDescription = referredField.getEnclosingElement().toString() + "." + referredField.getSimpleName();
            TypeMirror referringEntityType = fieldElementType(referredField);
            return Collectors.collectingAndThen(Collectors.toList(), list -> {
                switch (list.size()) {
                    case 0: throw new IllegalStateException(String.format("No foreign key field found with name %s for %s. Possibly field annotated with @ForeignKey is missing in %s or column name in @ForeignKey is incorrect", foreignKeyColumnName,  fieldDescription, referringEntityType));
                    case 1: return list.get(0);
                    default: throw new IllegalStateException(String.format("Foreign key column name %s for reference %s -> %s is not unique. You are trying to use the same column name for two foreign keys", foreignKeyColumnName, referringEntityType, fieldDescription));
                }
            });
        }


        private boolean isEntityField(VariableElement e) {
            return fieldElementType(e).getAnnotation(Entity.class) != null;
        }

        private TypeMirror fieldElementType(VariableElement element) {
            if (JavaModelUtils.isCollection(element)) {
                return JavaModelUtils.getGenericCollectionType(element);
            }
            return element.asType();
        }

        private boolean hasNoEntityFieldAnnotations(VariableElement e) {
            return e.getAnnotation(ForeignKey.class) == null
                    && e.getAnnotation(Referenced.class) == null
                    && e.getAnnotation(Json.class) == null
                    && e.getAnnotation(CollectionTable.class) == null
                    && e.getAnnotation(CrossTable.class) == null;

        }
    }

    @RequiredArgsConstructor
    private class EntityModelProducer {
        @Getter
        private final EntityModel entityModel = new EntityModel();
        private final TypeElement type;

        EntityModel produce() {
            Set<VariableElement> fields = fields(type);
            GettersAndSetters gettersAndSetters = new GettersAndSetters(type, getTypeUtils());
            entityModel.setType(type);
            entityModel.setTypeName(TypeName.get(type.asType()));
            entityModel.setTableName(tableName(type));
            entityModel.setIdField(idField(fields, gettersAndSetters));
            entityModel.setNonComplexFields(nonComplexFields(fields, gettersAndSetters));
            entityModel.setForeignKeyFields(foreignKeyFields(fields, gettersAndSetters));
            entityModel.setCrossTableFields(crossTableFields(fields, gettersAndSetters));
            entityModel.setCollectionTableFields(collectionTableFields(fields, gettersAndSetters));
            entityModel.setJsonFields(jsonFields(fields, gettersAndSetters));
            return entityModel;
        }

        private String tableName(TypeElement entityType) {
            String nameInAnnotation = entityType.getAnnotation(Entity.class).tableName();
            return nameInAnnotation.isEmpty() ? NamingRules.toSqlName(entityType.getSimpleName().toString()) : nameInAnnotation;
        }

        private SimpleEntityFieldModel idField(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(Id.class) != null)
                    .map(field -> new SimpleEntityFieldModel(entityModel, field, gettersAndSetters))
                    .collect(CollectorUtils.toOnlyElement("@Id in " + type));
        }

        private Set<JsonFieldModel> jsonFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(Json.class) != null)
                    .map(field -> new JsonFieldModel(entityModel, field, gettersAndSetters))
                    .collect(Collectors.toUnmodifiableSet());
        }

        private Set<SimpleEntityFieldModel> nonComplexFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> JavaModelUtils.isNonComplex(field.asType()))
                    .filter(field -> !JavaModelUtils.isCollection(field))
                    .filter(field -> field.getAnnotation(Json.class) == null)
                    .map(field -> new SimpleEntityFieldModel(entityModel, field, gettersAndSetters))
                    .collect(Collectors.toUnmodifiableSet());
        }

        private Set<ForeignKeyFieldModel> foreignKeyFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(ForeignKey.class) != null)
                    .map(field -> new ForeignKeyFieldModel(entityModel, field, gettersAndSetters))
                    .collect(Collectors.toUnmodifiableSet());
        }

        private Set<CrossTableFieldModel> crossTableFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(CrossTable.class) != null)
                    .map(field -> new CrossTableFieldModel(entityModel, field, gettersAndSetters))
                    .collect(Collectors.toUnmodifiableSet());
        }

        private Set<CollectionTableFieldModel> collectionTableFields(Set<VariableElement> fields, GettersAndSetters gettersAndSetters) {
            return fields.stream()
                    .filter(field -> field.getAnnotation(CollectionTable.class) != null)
                    .map(field -> new CollectionTableFieldModel(entityModel, field, gettersAndSetters))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }


}
