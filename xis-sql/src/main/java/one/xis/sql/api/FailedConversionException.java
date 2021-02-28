package one.xis.sql.api;

public class FailedConversionException extends RuntimeException{
    public FailedConversionException(Object source, Class<?> target) {
        super(String.format("can not convert %d to %d", source, target.getName()));
    }
}
