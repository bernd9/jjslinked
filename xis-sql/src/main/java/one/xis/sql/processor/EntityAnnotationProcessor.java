package one.xis.sql.processor;

import com.ejc.util.ProcessorLogger;
import com.google.auto.service.AutoService;
import one.xis.sql.Entity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private Map<TypeMirror, TypeElement> entities;
    private Boolean processed;
    private EntityModelFactory entityModelFactory;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processed = false;
        entityModelFactory = new EntityModelFactory(processingEnv);
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        Set<TypeElement> entities = findEntities(roundEnv);
        if (!entities.isEmpty()) {
            if (processed) {
                // We have to be sure, that this will never happen
                throw new IllegalStateException("processor does not support multiple processing-rounds");
            }
            processed = true;
            Set<EntityModel> allModels = entities.stream().map(this::createEntityModel).collect(Collectors.toSet());
            mapReferredFields(allModels);
            mapCrossTableField(allModels);
            allModels.forEach(model -> processEntityModel(model, allModels));
        }
        return true;
    }


    private Set<TypeElement> findEntities(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(Entity.class)
                .stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toSet());
    }


    private EntityModel createEntityModel(TypeElement entityType) {
        return entityModelFactory.createEntityModel(entityType);
    }

    private void mapReferredFields(Set<EntityModel> allEntityModels) {
        Set<ForeignKeyFieldModel> foreignKeyFields  = allForeignKeyFields(allEntityModels);
        allEntityModels.forEach(entityModel -> entityModelFactory.postsAssignReferredFields(entityModel, foreignKeyFields));
    }


    private void mapCrossTableField(Set<EntityModel> allEntityModels) {
        Map<String, List<CrossTableFieldModel>> fieldModelByCrossTable = new HashMap<>();
        allCrossTableFields(allEntityModels).forEach(model -> fieldModelByCrossTable.computeIfAbsent(model.getCrossTable(), tableName -> new ArrayList<>()).add(model));
        fieldModelByCrossTable.values().forEach(list -> {
            switch (list.size()) {
                case 0: throw new IllegalStateException(); // will never occur
                case 1: throw new IllegalStateException("No corresponding n:m-field for cross-table " + list.get(0).getCrossTable() +". Found single relation in " + list.get(0).getField());
                case 2:
                    list.get(0).setCorrespondingCrossTableField(list.get(1));
                    list.get(1).setCorrespondingCrossTableField(list.get(0));
                    break;
                default: throw new IllegalStateException("Conflicting cross-table relations: Found " + list.size()+" occurrences of cross-table "+ list.get(0).getCrossTable());
            }
        });
    }

    private Set<ForeignKeyFieldModel> allForeignKeyFields(Set<EntityModel> allEntityModels) {
        return allEntityModels.stream()
            .map(EntityModel::getForeignKeyFields)
            .flatMap(Set::stream)
            .collect(Collectors.toUnmodifiableSet());
    }

    private Set<CrossTableFieldModel> allCrossTableFields(Set<EntityModel> allEntityModels) {
        return allEntityModels.stream()
                .map(EntityModel::getCrossTableFields)
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void processEntityModel(EntityModel entityModel, Set<EntityModel> allModels) {
        try {
            writeEntityUtil(entityModel);
            writeEntityFunctions(entityModel);
            writeEntityProxy(entityModel, allModels);
            writeEntityStatements(entityModel);
            writeEntityResultSet(entityModel);
            writeEntityTableAccessor(entityModel, allModels);
            writeRepositoryImpl(entityModel, allModels);
            writeEntityCrudHandler(entityModel, allModels);
        } catch (ModelValidationException | IOException e) {
            ProcessorLogger.reportError(this, processingEnv, e);
        }
    }

    private void writeEntityUtil(EntityModel entityModel) throws IOException {
        EntityUtilModel model = new EntityUtilModel(entityModel); // TODO Validation
        EntityUtilWriter writer = new EntityUtilWriter(model, processingEnv);
        writer.write();
    }

    private void writeEntityFunctions(EntityModel entityModel) throws IOException {
        EntityFunctionsModel model = new EntityFunctionsModel(entityModel);
        EntityFunctionsWriter writer = new EntityFunctionsWriter(model, processingEnv);
        writer.write();
    }

    private void writeEntityProxy(EntityModel entityModel, Set<EntityModel> allModels) throws IOException {
        EntityProxyModel model = new EntityProxyModel(entityModel); // TODO Validation
        EntityProxyWriter writer = new EntityProxyWriter(model, processingEnv);
        writer.write();
    }

    private void writeEntityStatements(EntityModel entityModel) throws ModelValidationException, IOException {
        EntityStatementsModel model = new EntityStatementsModel(entityModel, processingEnv);
        new EntityStatementsValidator(model).validate();
        new EntityStatementsWriter(model, processingEnv).write();
    }


    private void writeEntityResultSet(EntityModel entityModel) throws ModelValidationException, IOException {
        EntityResultSetModel model = new EntityResultSetModel(entityModel, processingEnv);
        new EntityResultSetValidator(model).validate();
        new EntityResultSetWriter(model, processingEnv).write();
    }
    
    private void writeEntityTableAccessor(EntityModel entityModel, Set<EntityModel> allModels) throws ModelValidationException, IOException {
        EntityTableAccessorModel model = new EntityTableAccessorModel(entityModel);
        new EntityTableAccessorModelValidator(model, allModels).validate();
        new EntityTableAccessorWriter(model, processingEnv).write();
    }

    private void writeEntityCrudHandler(EntityModel entityModel, Set<EntityModel> allModels) throws ModelValidationException, IOException {
        EntityCrudHandlerModel model = new EntityCrudHandlerModel(entityModel);
        new EntityCrudHandlerValidator(model).validate();
        new EntityCrudHandlerWriter(model, processingEnv).write();
    }


    private void writeRepositoryImpl(EntityModel entityModel, Set<EntityModel> allModels) {

    }

}
