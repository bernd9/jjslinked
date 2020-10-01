package com.ejc.api.context.model;

import com.ejc.api.context.ClassReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CollectionDependencyField {
    private final String name;
    private final ClassReference collectionType;
    private final ClassReference genericType;
    

    void setSingletons(Set<SingletonModel> models) {
    }

}
