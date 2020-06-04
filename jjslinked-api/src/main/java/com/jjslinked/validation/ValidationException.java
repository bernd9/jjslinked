package com.jjslinked.validation;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ValidationException extends RuntimeException {
    private final Set<Violation> violations;
}
