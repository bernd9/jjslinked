package com.jjslinked.processor.codegen;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Name;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaSourceUtils {

    public static String firstToLowerCase(String s) {
        if (s == null || s.length() == 0)
            return "";
        return new StringBuilder().append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String getPackageName(Name qualifiedName) {
        return getPackageName(qualifiedName.toString());
    }

    public static String getPackageName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        if (index == -1) {
            return qualifiedName;
        }
        return qualifiedName.substring(0, index);
    }
}
