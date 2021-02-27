package one.xis.sql.processor;

// TODO : move to core
public class ModelValidationException extends Exception {
    public ModelValidationException() {
    }

    public ModelValidationException(String message) {
        super(message);
    }

    public ModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelValidationException(Throwable cause) {
        super(cause);
    }

    public ModelValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
