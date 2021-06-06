package one.xis.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;

@Getter
@RequiredArgsConstructor
class Module {
    private final Class<?> applicationClass;
    private final Collection<SingletonConstructor> singletonConstructors;
    private final Map<ClassReference, SingletonObject> singletonObjects;
    private final Map<ClassReference, ClassReference> classReplacements;

    @Override
    public int hashCode() {
        return applicationClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!Module.class.isInstance(obj)) {
            return false;
        }
        Module module = (Module) obj;
        return applicationClass.getName().equals(module.applicationClass.getName());
    }
}
