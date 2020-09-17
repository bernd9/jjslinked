package com.ejc.http.api.controller;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class UrlPatternParser {

    private static final Pattern PATH_VAR_PATTERN = Pattern.compile("\\$?\\{([^\\]]+)\\}");

    public static UrlPattern parse(String url) {
        return new UrlPattern(toUrlFragment(url));
    }

    public static UrlPattern parse(String url1, String url2) {
        List<UrlFragment> fragments = new ArrayList<>(toUrlFragment(url1));
        fragments.addAll(toUrlFragment(url2));
        return new UrlPattern(fragments);
    }

    private List<UrlFragment> toUrlFragment(String url) {
        return Arrays.stream(url.split("/"))
                .filter(s -> !s.isEmpty())
                .map(file -> toUrlFragment(file, url))
                .collect(Collectors.toList());
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
