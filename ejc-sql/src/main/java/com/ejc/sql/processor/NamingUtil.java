package com.ejc.sql.processor;

import lombok.experimental.UtilityClass;

import javax.lang.model.element.TypeElement;

@UtilityClass
class NamingUtil {

    String firstToUpperCase(String s) {
        if (s.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(s.charAt(0)));
        if (s.length() > 1) {
            builder.append(s.substring(1));
        }
        return builder.toString();
    }

    String firstToLowerCase(String s) {
        if (s.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(s.charAt(0)));
        if (s.length() > 1) {
            builder.append(s.substring(1));
        }
        return builder.toString();
    }

    String toFieldName(TypeElement typeElement) {
        return NamingUtil.firstToLowerCase(typeElement.getSimpleName().toString());
    }
}
