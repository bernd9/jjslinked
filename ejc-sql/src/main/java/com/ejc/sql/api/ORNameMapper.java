package com.ejc.sql.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ORNameMapper {

    public static String toSqlName(String fieldName) {
        StringBuilder s = new StringBuilder();
        char[] chars = fieldName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    s.append("_");
                }
                s.append(Character.toLowerCase(c));
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }
}
