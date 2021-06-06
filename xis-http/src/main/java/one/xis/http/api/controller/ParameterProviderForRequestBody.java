package one.xis.http.api.controller;

import one.xis.context.ClassReference;
import one.xis.http.ContentType;
import one.xis.http.exception.HttpStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForRequestBody implements ParameterProvider<Object> {
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        ObjectMapper objectMapper = context.getApplicationContext().getBean(ObjectMapper.class);
        try {
            return objectMapper.readValue(context.getRequest().getInputStream(), parameterType.getReferencedClass());
        } catch (IOException e) {
            throw new HttpStatusException(422, ContentType.JSON, "unable to read body content");
        }
    }
}
