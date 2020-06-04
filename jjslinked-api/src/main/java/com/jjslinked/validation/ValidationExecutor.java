package com.jjslinked.validation;

import java.util.Set;
import java.util.function.Consumer;

public class ValidationExecutor {

    public void executeNotNullValidation(String param, String paramName) {
        // TODO
    }

    public void executeRegExprValidation(Object o, String regexpr, String paramName) {
        // TODO
    }

    public void executeValidation(Object o, Consumer<Object> validation) {
        try {
            validation.accept(o);
        } catch (ValidationException e) {
            // TODO
        }
    }

    public boolean validationOk() {
        return true;
    }

    public Set<Violation> getViolations() {
        return null;
    }
}
