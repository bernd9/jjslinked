package one.xis.http.api.controller;

import one.xis.context.ClassReference;
import one.xis.util.TypeUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForUrlParam implements ParameterProvider<Object> {
    private final String parameterKey;
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        String param = context.getPathVariables().get(parameterKey);
        return TypeUtils.convertStringToSimple(param, parameterType.getReferencedClass());
    }
}
