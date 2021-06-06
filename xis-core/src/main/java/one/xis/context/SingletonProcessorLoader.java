package one.xis.context;

import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

class SingletonProcessorLoader {
    static Set<SingletonPreProcessor> load() {
        ServiceLoader<SingletonPreProcessor> loader = ServiceLoader.load(SingletonPreProcessor.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }
}
