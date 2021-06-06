package one.xis.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValueAnnotationReference {
    private final String key;
    private final String defaultValue;
    private final boolean mandatory;
}
