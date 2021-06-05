package one.xis.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

@FunctionalInterface
public interface ElementHandler<T extends Element> {

    void process(Set<T> elements, ProcessingEnvironment processingEnvironment);


}
