package one.xis.sql.processor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EntityResultSetValidator {
    private final EntityResultSetModel model;

    // TODO validate : 2 Entities with same simple name will cause conflict
    void validate() throws ModelValidationException {

    }
}
