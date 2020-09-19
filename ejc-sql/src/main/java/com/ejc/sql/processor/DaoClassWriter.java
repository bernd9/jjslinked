package com.ejc.sql.processor;

import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

@Builder
public class DaoClassWriter {
    private final TypeElement dao;
    private final boolean crudRepository;
    private final ProcessingEnvironment processingEnvironment;
    private String daoImlSimpleName;

    void write() throws Exception {

    }
}
