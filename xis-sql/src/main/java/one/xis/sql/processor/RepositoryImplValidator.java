package one.xis.sql.processor;

import lombok.RequiredArgsConstructor;

import javax.lang.model.type.TypeMirror;

@RequiredArgsConstructor
class RepositoryImplValidator {
    private final RepositoryImplModel implModel;
    private final TypeMirror pkTypeInInterface; // TODO validate to equal pktype in entity-model.

    void validate() throws ModelValidationException {

    }
}
