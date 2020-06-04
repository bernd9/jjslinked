package com.jjslinked.validation;

// TODO Processor to add Validators for javax-validation
public interface Validator<T> {

    void validate(T t, String paramName);

    default void validateRaw(String jsonOrValue, String paramName) {
    }
}
