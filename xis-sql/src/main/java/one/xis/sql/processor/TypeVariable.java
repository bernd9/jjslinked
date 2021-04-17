package one.xis.sql.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import lombok.Getter;


@Getter
class TypeVariable {
    private final String name;
    private final TypeName bounds;

    TypeVariable(String name, Class<?> bounds) {
        this.name = name;
        this.bounds = TypeName.get(bounds);
    }

    TypeVariable(String name, TypeName bounds) {
        this.name = name;
        this.bounds = bounds;
    }

    TypeVariableName toTypeVariableName() {
        return TypeVariableName.get(name, bounds);
    }

}
