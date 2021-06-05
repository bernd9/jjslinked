package one.xis.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CollectionConstructorParameterElement implements ConstructorParameterElement {
    private final Class<? extends Collection> collectionType;
    private final TypeMirror genericType;

}
