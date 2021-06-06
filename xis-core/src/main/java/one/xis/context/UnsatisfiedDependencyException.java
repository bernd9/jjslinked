package one.xis.context;

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String beanClass) {
        super("No candidate for bean " + beanClass);
    }

    public UnsatisfiedDependencyException(Class<?> beanClass) {
        this(beanClass.getName());
    }

}
