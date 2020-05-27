package processor;

import com.jjslinked.annotations.LinkedMethod;
import com.jjslinked.ast.ClientClassNode;
import com.jjslinked.ast.LinkedMethodNode;
import com.jjslinked.ast.LinkedObservableNode;
import com.jjslinked.ast.ParamNode;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.element.ElementKind.PARAMETER;

@SupportedAnnotationTypes("com.jjslink.annotations.Client")
public class ClientProcessor extends AbstractProcessor {

    private TypeElement linkedMethodAnnotation;
    private TypeElement linkedObservableAnnotation;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        TypeElements typeElements = new TypeElements(processingEnv);
        this.linkedMethodAnnotation = typeElements.getByName(Annotations.LinkedMethod.className());
        this.linkedMethodAnnotation = typeElements.getByName(Annotations.LinkedObservable.className());


        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream().flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
                .map(TypeElement.class::cast).forEach(this::processClientClass);
        return false;
    }

    private void processClientClass(TypeElement clientElement) {
        ClientClassNode clientClassNode = ClientClassNode.builder()
                .linkedObservables(linkedObservableNodes(clientElement))
                .linkedMethods(linkedMethodNodes(clientElement))
                .build();

    }

    private Collection<LinkedMethodNode> linkedMethodNodes(TypeElement clientElement) {
        return clientElement.getEnclosedElements().stream()
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(this::isLinkedMethod)
                .map(this::toLinkedMethodNode)
                .collect(Collectors.toSet());
    }

    private boolean isLinkedMethod(ExecutableElement typeElement) {
        return typeElement.getAnnotation(LinkedMethod.class) != null;
    }

    private LinkedMethodNode toLinkedMethodNode(ExecutableElement method) {
        return LinkedMethodNode.builder()
                .clientMethod(getMethodName(method))
                .parameters(methodParameters(method))
                .returnType(method.getReturnType().toString())
                .exceptionTypes(exceptionTypes(method))
                .build();
    }

    private Set<String> exceptionTypes(ExecutableElement method) {
        return method.getThrownTypes().stream().map(TypeMirror::toString).collect(Collectors.toSet());
    }

    private String getMethodName(ExecutableElement method) {
        String nameInAnnotation = method.getAnnotation(LinkedMethod.class).clientMethod();
        return nameInAnnotation.isEmpty() ? method.getSimpleName().toString() : nameInAnnotation;
    }

    private List<ParamNode> methodParameters(ExecutableElement method) {
        return method.getParameters().stream()
                .filter(this::isParameter)
                .map(VariableElement.class::cast)
                .map(this::toParamNode)
                .collect(Collectors.toList());
    }

    private ParamNode toParamNode(VariableElement variable) {
        return ParamNode.builder()
                .name(variable.getSimpleName().toString())
                .type(variable.asType().toString())
                .build();
    }

    private boolean isParameter(Element e) {
        return e.getKind() == PARAMETER;
    }

    private Collection<LinkedObservableNode> linkedObservableNodes(Element clientElement) {
        return null;
    }

}
