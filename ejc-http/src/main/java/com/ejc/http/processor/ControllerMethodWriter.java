package com.ejc.http.processor;

import com.ejc.Singleton;
import com.ejc.api.context.ClassReference;
import com.ejc.http.HttpMethod;
import com.ejc.http.api.controller.*;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Builder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

@Builder
class ControllerMethodWriter {
    private String simpleClassName;
    private String packageName;
    private HttpMethod httpMethod;
    private ExecutableElement methodElement;
    private ProcessingEnvironment processingEnvironment;
    private List<ParameterProvider<?>> parameterProviders;
    private String url;

    void write() throws IOException {
        TypeSpec typeSpec = TypeSpec.classBuilder(simpleClassName)
                .addAnnotation(Singleton.class)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor())
                .superclass(ControllerMethod.class)
                .addOriginatingElement(methodElement.getEnclosingElement())
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnvironment.getFiler());

    }

    private MethodSpec constructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("setControllerClass($T.getRef(\"$L\"))", ClassReference.class, methodElement.getEnclosingElement())
                .addStatement("setMethodName(\"$L\")", methodElement.getSimpleName())
                .addStatement("setHttpMethod($T.$L)", HttpMethod.class, httpMethod.name());
        addParameterTypes(builder);
        addUrlPattern(builder);
        addParameterProviders(builder);
        return builder.build();
    }

    private void addParameterProviders(MethodSpec.Builder builder) {
        parameterProviders.forEach(provider -> addParameterProvider(builder, provider));
    }

    private void addParameterProvider(MethodSpec.Builder builder, ParameterProvider<?> provider) {
        if (ParameterProviderForServletRequest.class.isInstance(provider)) {
            builder.addStatement("addParameterProvider(new $T($T.class))", ParameterProviderForServletRequest.class);
        } else if (ParameterProviderForServletResponse.class.isInstance(provider)) {
            builder.addStatement("addParameterProvider(new $T($T.class))", ParameterProviderForServletResponse.class);
        } else if (ParameterProviderForSession.class.isInstance(provider)) {
            builder.addStatement("addParameterProvider(new $T($T.class))", ParameterProviderForSession.class);
        } else if (ParameterProviderForRequestBody.class.isInstance(provider)) {
            ParameterProviderForRequestBody parameterProviderForRequestBody = (ParameterProviderForRequestBody) provider;
            builder.addStatement("addParameterProvider(new $T($T.getRef(\"$L\")))", ParameterProviderForRequestBody.class, ClassReference.class, parameterProviderForRequestBody.getParameterType().getClassName());
        } else if (ParameterProviderForUrlParam.class.isInstance(provider)) {
            ParameterProviderForUrlParam providerForUrlParam = (ParameterProviderForUrlParam) provider;
            builder.addStatement("addParameterProvider(new $T(\"$L\", $T.getRef(\"$L\")))", ParameterProviderForUrlParam.class, ClassReference.class, providerForUrlParam.getParameterKey(), providerForUrlParam.getParameterType().getClassName());
        } else {
            throw new IllegalStateException();
        }
    }

    private void addUrlPattern(MethodSpec.Builder builder) {
        UrlPattern urlPattern = UrlPatternParser.parse(url);
        builder.addStatement("$T urlPattern = new $T()", UrlPattern.class, UrlPattern.class);
        builder.addStatement("setUrlPattern(urlPattern)", UrlPattern.class);
        urlPattern.getUrlFragments().stream().forEach(fragment -> addUrlFragment(builder, fragment));
    }

    private void addUrlFragment(MethodSpec.Builder builder, UrlFragment urlFragment) {
        if (UrlPathFragment.class.isInstance(urlFragment)) {
            addUrlPathFragment(builder, (UrlPathFragment) urlFragment);
        } else if (UrlVariableFragment.class.isInstance(urlFragment)) {
            addUrlVariableFragment(builder, (UrlVariableFragment) urlFragment);
        } else {
            throw new IllegalStateException();
        }
    }

    private void addUrlPathFragment(MethodSpec.Builder builder, UrlPathFragment urlFragment) {
        builder.addStatement("urlPattern.addPathFragment(\"$L\")", urlFragment);
    }

    private void addUrlVariableFragment(MethodSpec.Builder builder, UrlVariableFragment urlFragment) {
        builder.addStatement("urlPattern.addPathVariable(\"$L\")", urlFragment.getVariableName());
    }

    private void addParameterTypes(MethodSpec.Builder builder) {
        methodElement.getParameters().forEach(type -> builder.addStatement("addParameterType($T.class)", type));
    }


}
