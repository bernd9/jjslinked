package com.ejc.sql.processor;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
class EntityManagerModel {
    private final EntityModel entityModel;
    private final String insertStatement;
    private final String updateStatement;
    private final String deleteStatement;


}
