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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private Map<TypeMirror, TypeElement> entities;
    private Boolean processed;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processed = false;
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
        return new EntityModel(entityType, processingEnv.getTypeUtils());
    }

    private void processEntityModel(EntityModel entityModel, Set<EntityModel> allModels) {
        try {
            writeEntityUtil(entityModel);
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


    private void writeEntityUtil(EntityModel entityModel) {
        EntityUtilModel model = new EntityUtilModel(entityModel); // TODO Validation
        EntityUtilWriter writer = new EntityUtilWriter(model, processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            ProcessorLogger.reportError(this, processingEnv, e);
        }
    }

    private void writeEntityProxy(EntityModel entityModel, Set<EntityModel> allModels) {
        EntityProxyModel model = new EntityProxyModel(entityModel); // TODO Validation
        EntityProxyWriter writer = new EntityProxyWriter(model, processingEnv);
        try {
            writer.write();
        } catch (IOException e) {
            ProcessorLogger.reportError(this, processingEnv, e);
        }
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
