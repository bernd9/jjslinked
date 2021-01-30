package one.xis.sql.processor;

import com.google.auto.service.AutoService;
import one.xis.sql.Entity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class EntityAnnotationProcessor extends AbstractProcessor {

    private Map<TypeMirror, TypeElement> entities;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        entities = new HashMap<>();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (entities == null) {
            // We have to be sure, that this will never happen
            throw new IllegalStateException("processor does not support multiple rounds");
        }
        entities.putAll(roundEnv.getElementsAnnotatedWith(Entity.class)
                .stream()
                .map(TypeElement.class::cast)
                .collect(Collectors.toMap(TypeElement::asType, Function.identity())));
        processEntityTypes();
        return false;
    }

    private void processEntityTypes() {
        entities.values().forEach(this::processEntityType);
    }

    private void processEntityType(TypeElement entityType) {

    }

}
