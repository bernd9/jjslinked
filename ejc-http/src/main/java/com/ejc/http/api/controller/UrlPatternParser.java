package com.ejc.http.api.controller;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class UrlPatternParser {

    private static final Pattern PATH_VAR_PATTERN = Pattern.compile("\\$?\\{([^\\]]+)\\}");

    public static UrlPattern parse(String url) {
        return new UrlPattern(Arrays.stream(url.split("/"))
                .filter(s -> !s.isEmpty())
                .map(file -> toUrlFragment(file, url))
                .collect(Collectors.toList()));
    }

    private UrlFragment toUrlFragment(String s, String url) {
        if (s.contains("{") || s.contains("}")) {
            return toVarFragment(s, url);
        }
        return new UrlPathFragment(s);
    }

    private static UrlFragment toVarFragment(String pattern, String url) {
        Matcher matcher = PATH_VAR_PATTERN.matcher(pattern);
        if (!matcher.find()) {
            throw new IllegalUrlPatternException(url, pattern);
        }
        return new UrlVariableFragment(matcher.group(1));
    }
}
