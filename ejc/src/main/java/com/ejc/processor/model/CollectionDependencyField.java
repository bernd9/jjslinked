package com.ejc.processor.model;

import com.ejc.api.context.ClassReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CollectionDependencyField {
    private final String name;
    private final ClassReference collectionType;
    private final ClassReference genericType;
}
