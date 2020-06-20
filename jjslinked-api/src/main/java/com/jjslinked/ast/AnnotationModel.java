package com.jjslinked.ast;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AnnotationModel {
    String qualifiedName;

    @Override
    public String toString() {
        return qualifiedName.replace("@", "");
    }
}
