package one.xis.context;

import java.util.Optional;
import java.util.Set;

public class CompoundSingletonPreProcessor extends SingletonPreProcessor<Object> {

    private final Set<SingletonPreProcessor> singletonPreProcessors;

    public CompoundSingletonPreProcessor(Set<SingletonPreProcessor> singletonPreProcessors) {
        super(Object.class);
        this.singletonPreProcessors = singletonPreProcessors;
    }

    @Override
    public Optional<Object> beforeInstantiation(Class<Object> type) {
        return singletonPreProcessors.stream()
                .filter(processor -> processor.matches(type))
                .map(singletonProcessor -> singletonProcessor.beforeInstantiation(type))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }


    @Override
    public Object afterInstantiation(Object o) {
        for (SingletonPreProcessor<Object> processor: singletonPreProcessors) {
            if (processor.matches(o.getClass())) {
                o = processor.afterInstantiation(o);
            }
        }
        return o;
    }

    void addSingletonProcessor(SingletonPreProcessor singletonPreProcessor) {
        singletonPreProcessors.add(singletonPreProcessor);
    }


}
