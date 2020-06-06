package com.ejaf.processor.parameter;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParameterProviderModel {
    private String typeRef;
    private String methodRef;
    private String paramRef;
    private String providerSuperClassSimpleName;
    private String providerSuperClassPackageName;
    private String providerClassSimpleName;
    private String providerClassPackageName;

    public String getProviderClassQualifiedName() {
        return String.join(".", providerClassPackageName, providerClassSimpleName);
    }

}
