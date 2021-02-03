package one.xis.sql.processor;

import com.ejc.util.CollectorUtils;
import com.ejc.util.StringUtils;
import com.google.errorprone.annotations.Var;
import lombok.Getter;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class GettersAndSetters {
    private final TypeElement typeElement;
    private final Types types;
    private final Set<ExecutableElement> methods;
    private final Set<VariableElement> fields;

    @Getter
    private final Map<VariableElement, ExecutableElement> getters = new HashMap<>();

    @Getter
    private final Map<VariableElement, ExecutableElement> setters = new HashMap<>();

    GettersAndSetters(TypeElement typeElement, Types types) {
        this.typeElement = typeElement;
        this.types = types;
        this.methods = methods();
        this.fields = fields();
    }

    private void findSetters() {
        for (VariableElement field : fields) {
            getSetter(field).ifPresent(setter -> setters.put(field, setter));
        }
    }

    private void findGetters() {
        for (VariableElement field : fields) {
            getGetter(field).ifPresent(setter -> getters.put(field, setter));
        }
    }

    Optional<ExecutableElement> getGetter(VariableElement field) {
        return methods.stream()
                .filter(e -> isGetterForField(e, field))
                .collect(CollectorUtils.toOnlyOptional());
    }

    Optional<ExecutableElement> getSetter(VariableElement field) {
        return methods.stream()
                .filter(e -> isSetterForField(e, field))
                .collect(CollectorUtils.toOnlyOptional());
    }

    private boolean isSetterForField(ExecutableElement setter, VariableElement field) {
        if (setter.getParameters().size() != 1) {
            return false;
        }
        if (!matchesFieldType(setter.getParameters().get(0), field)) {
            return false;
        }
        if (!getSetterName(field).equals(setter.getSimpleName().toString())) {
            return false;
        }
        return true;
    }

    private boolean isGetterForField(ExecutableElement getter, VariableElement field) {
        if (!getter.getParameters().isEmpty()) {
            return false;
        }
        if (!matchesFieldType(getter.getReturnType(), field.asType())) {
            return false;
        }
        if (!getGetterName(field).equals(getter.getSimpleName().toString())) {
            return false;
        }
        return true;
    }

    private boolean matchesFieldType(VariableElement parameter, VariableElement field) {
        return types.isAssignable(parameter.asType(), field.asType());
    }

    private boolean matchesFieldType(TypeMirror parameterType, TypeMirror fieldType) {
        return types.isAssignable(parameterType, fieldType);
    }

    private String getGetterName(VariableElement field) {
        return getGetterName(field.getSimpleName().toString());
    }

    private String getGetterName(String fieldName) {
        return new StringBuilder("get")
                .append(StringUtils.firstToUpperCase(fieldName))
                .toString();
    }

    private String getSetterName(VariableElement field) {
        return getSetterName(field.getSimpleName().toString());
    }

    private String getSetterName(String fieldName) {
        return new StringBuilder("set")
                .append(StringUtils.firstToUpperCase(fieldName))
                .toString();
    }

    private Set<ExecutableElement> methods() {
        return typeElement.getEnclosedElements()
                .stream().filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<VariableElement> fields() {
        return typeElement.getEnclosedElements()
                .stream().filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toUnmodifiableSet());
    }
}
