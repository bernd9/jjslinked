package one.xis.context;

import com.ejc.ApplicationRunner;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ApplicationRunnerBase extends ApplicationRunner {

    private final ClassReference applicationClass;

    @Override
    public void doRun(Collection<SingletonPreProcessor> singletonPreProcessors, Collection<Supplier<Object>> suppliers) {
        ApplicationContextFactory factory = new ApplicationContextFactory(applicationClass.getReferencedClass());
        singletonPreProcessors.forEach(factory::addSingletonProcessor);
        suppliers.forEach(factory::addSingletonPreSupplier);
        factory.createApplicationContext();
    }
}
