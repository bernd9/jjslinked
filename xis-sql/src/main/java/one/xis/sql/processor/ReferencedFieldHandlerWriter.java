package one.xis.sql.processor;

import com.squareup.javapoet.TypeSpec;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ReferencedFieldHandlerWriter {
    private final ReferredFieldModel model;

    void write(TypeSpec.Builder entityCrudHandler) {

    }

    private TypeSpec fieldHandlerDeclaration() {
        return null;
    }


}
