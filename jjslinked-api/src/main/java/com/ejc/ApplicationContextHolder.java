package com.ejc;

import com.jjslinked.ApplicationContext;
import lombok.Getter;
import lombok.Setter;

public class ApplicationContextHolder {

    @Getter
    @Setter
    private static ApplicationContext context;
}
