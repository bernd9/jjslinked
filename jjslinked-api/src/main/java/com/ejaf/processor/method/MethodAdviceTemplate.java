package com.ejaf.processor.method;

import com.ejaf.processor.parameter.ParameterProviderModel;
import com.ejaf.processor.template.JavaTemplate;

public class MethodAdviceTemplate extends JavaTemplate<ParameterProviderModel> {


    protected MethodAdviceTemplate() {
        super("MethodAspect");
    }
}
