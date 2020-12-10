package com.ejc;

import com.ejc.api.context.SingletonPreProcessor;
import com.ejc.util.ClassUtils;

import java.util.Collection;
import java.util.HashSet;

public abstract class ApplicationRunner {

    private static final String APPLICATION_RUNNER_IMPL = "com.ejc.ApplicationRunnerImpl";
    private static final Collection<SingletonPreProcessor> SINGLETON_PRE_PROCESSORS = new HashSet<>();

    public static void run() throws Exception {
        ApplicationRunner instance;
        try {
            instance = (ApplicationRunner) ClassUtils.createInstance(APPLICATION_RUNNER_IMPL);
        } catch (Exception e) {
            throw new IllegalStateException("No runner-implementation. May be you forgot to build project or to configure the annotation processor-lib in your buildfile");
        }
        instance.doRun(SINGLETON_PRE_PROCESSORS);
    }

    public static void addSingletonPreProcessor(SingletonPreProcessor singletonPreProcessor) {
        SINGLETON_PRE_PROCESSORS.add(singletonPreProcessor);
    }

    protected abstract void doRun(Collection<SingletonPreProcessor> singletonPreProcessors) throws Exception;

    public static void main(String[] args) throws Exception {
        long t0 = System.currentTimeMillis();
        run();
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t0);
    }

}



