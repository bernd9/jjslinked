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
            entities.stream().map(this::createEntityModel)
                    .forEach(this::processEntityModel);
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

    private void processEntityModel(EntityModel entityModel) {
        writeSaveHandler(entityModel);
        writeRepositoryImpl(entityModel);
    }

    private void writeSaveHandler(EntityModel entityModel) {
        SaveHandlerModel saveHandlerModel = new SaveHandlerModel(entityModel);
        SaveHandlerWriter saveHandlerWriter = new SaveHandlerWriter(saveHandlerModel, processingEnv);
        try {
            saveHandlerWriter.write();
        } catch (IOException e) {
            ProcessorLogger.reportError(this, processingEnv, e);
        }
    }

    private void writeRepositoryImpl(EntityModel entityModel) {

    }

}
