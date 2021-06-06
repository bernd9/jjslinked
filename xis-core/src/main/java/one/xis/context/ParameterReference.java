package one.xis.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class ParameterReference {
    private final ClassReference classReference;
    private final String name;
    private final Optional<ValueAnnotationReference> valueAnnotationReference;

    @UsedInGeneratedCode
    public static ParameterReference getRef(ClassReference classReference, String name, Optional<ValueAnnotationReference> valueAnnotationReference) {
        return new ParameterReference(classReference, name, valueAnnotationReference);
    }
}
