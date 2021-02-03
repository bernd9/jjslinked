package one.xis.sql.processor;

import com.ejc.util.JavaModelUtils;
import com.google.auto.service.AutoService;
import one.xis.sql.Entity;
import one.xis.sql.Repository;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private Map<TypeMirror, TypeElement> entities;
    private Set<TypeMirror> entitiesWithRepository;
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
        if (processed) {
            // We have to be sure, that this will never happen
            throw new IllegalStateException("processor does not support multiple processing-rounds");
        }
        findEntities(roundEnv);
        findEntitiesWithRepository(roundEnv);
        processEntityElements();
        return false;
    }


    private void findEntities(RoundEnvironment roundEnv) {
        entities = roundEnv.getElementsAnnotatedWith(Entity.class)
                .stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toMap(TypeElement::asType, Function.identity()));
    }

    private void findEntitiesWithRepository(RoundEnvironment roundEnv) {
        entitiesWithRepository = roundEnv.getElementsAnnotatedWith(Repository.class)
                .stream()
                .map(TypeElement.class::cast)
                .map(repositoryType -> JavaModelUtils.getGenericType(repositoryType, 1))
                .collect(Collectors.toSet());
    }

    private void processEntityElements() {
        createEntityModels();
        processEntityModels();
    }

    private void createEntityModels() {
        entities.values().forEach(EntityModel::new);
    }

    private void processEntityModels() {
        EntityModel.allEntityModels().forEach(this::processEntityModel);
    }


    private void processEntityModel(EntityModel entityModel) {
        writeSaveHandler(entityModel);
        writeDeleteHandler(entityModel);
        writeRepositoryImpl(entityModel);
    }


    private void writeSaveHandler(EntityModel entityModel) {

    }

    private void writeDeleteHandler(EntityModel entityModel) {

    }

    private void writeRepositoryImpl(EntityModel entityModel) {

    }

}
