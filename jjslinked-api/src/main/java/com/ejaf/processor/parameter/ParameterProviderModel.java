package com.ejaf.processor.parameter;

import com.ejaf.processor.template.JavaTemplateModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParameterProviderModel implements JavaTemplateModel {
    private String typeRef;
    private String methodRef;
    private String parameterName;
    private int parameterIndex;
    private String parameterType;
    private String providerSuperClassSimpleName;
    private String providerSuperClassPackageName;
    private String providerClassSimpleName;
    private String providerClassPackageName;

    @Override
    public String getJavaClassQualifiedName() {
        return String.join(".", providerClassPackageName, providerClassSimpleName);
    }
}
