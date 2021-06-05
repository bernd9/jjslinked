package one.xis.processor;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
class EntityTableAccessorModelValidator {
    private final EntityTableAccessorModel model;
    private final Set<EntityModel> allModels;

    void validate() throws ModelValidationException {

    }
}
