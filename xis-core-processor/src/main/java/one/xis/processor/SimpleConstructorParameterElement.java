package one.xis.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.type.TypeMirror;

@Getter
@RequiredArgsConstructor
public class SimpleConstructorParameterElement implements ConstructorParameterElement {
    private final TypeMirror type;
}
