package com.ejc.http.api.controller;

import com.ejc.api.context.ClassReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
@RequiredArgsConstructor
public class ParameterProviderForNamedPart implements ParameterProvider<Object> {
    private final String partName;
    private final ClassReference parameterType;

    @Override
    public Object provide(ControllerMethodInvocationContext context) {
        try {
            if (parameterType.getReferencedClass().equals(InputStream.class)) {
                return provideInputStream(context);
            }
            if (parameterType.getReferencedClass().equals(BufferedInputStream.class)) {
                return new BufferedInputStream(provideInputStream(context), 512);
            }
            if (parameterType.getReferencedClass().equals(Object.class)) {
                return provideByteArray(context);
            }
            if (parameterType.getReferencedClass().isArray() && parameterType.getReferencedClass().getComponentType().equals(Byte.class)) {
                return provideByteArray(context);
            }
            throw new IllegalStateException(parameterType.getReferencedClass() + " is not a valid type for multipart/form-data-parts");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream provideInputStream(ControllerMethodInvocationContext context) throws Exception {
        return context.getRequest().getPart(partName).getInputStream();
    }

    private byte[] provideByteArray(ControllerMethodInvocationContext context) {
        byte[] buff = new byte[512];
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (InputStream in = new BufferedInputStream(provideInputStream(context))) {
                int length = 0;
                while ((length = in.read(buff)) > 0) {
                    out.write(buff, 0, length);
                }
                return buff;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
