package com.ejc.processor;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
class SingletonWriterModel {

    private final Set<SingletonElement> singletonElements;
}
