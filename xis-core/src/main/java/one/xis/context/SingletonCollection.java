package one.xis.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
abstract class SingletonCollection {
    private final ClassReference elementType;

    boolean isSatisfied(@NonNull SingletonProviders providers) {
        return !providers.hasMatchingSourceFor(elementType);

    }

}
