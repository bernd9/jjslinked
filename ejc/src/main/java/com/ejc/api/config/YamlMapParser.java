package com.ejc.api.config;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ejc.util.TypeUtils.convertStringToSimple;

class YamlMapParser {
    static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+) *: (\\w+)");

    <K, V> Map<K, V> parseMap(String str, Class<K> keyType, Class<V> valueType) {
        return Arrays.stream(str.split(","))
                .map(this::keyValueMatcher)
                .filter(Matcher::find)
                .collect(Collectors.toMap(m1 -> convertStringToSimple(m1.group(1), keyType),
                        m2 -> convertStringToSimple(m2.group(2), valueType)));
    }

    private Matcher keyValueMatcher(String s) {
        return KEY_VALUE_PATTERN.matcher(s);
    }


}
