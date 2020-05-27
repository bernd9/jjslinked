package processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("com.jjslink.annotations.Client")
public class ASTProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        annotations.stream().flatMap(annotation -> roundEnvironment.getElementsAnnotatedWith(annotation).stream())
                .map(TypeElement.class::cast).forEach(this::processClientClass);
        return false;
    }

    private void processClientClass(TypeElement clientElement) {

    }
}
