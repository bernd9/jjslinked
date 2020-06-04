package com.jjslinked.processor.codegen.java;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ImportModel {
    private final Class<?> type;
    String staticName;
}
