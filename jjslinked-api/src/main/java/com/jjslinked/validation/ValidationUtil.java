package com.jjslinked.validation;

import com.jjslinked.annotations.Validate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.VariableElement;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {
    public static Set<Class<? extends Validator>> validators(VariableElement e) {
        return Optional.ofNullable(e.getAnnotation(Validate.class))
                .map(Validate::value)
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toSet());
    }
}
