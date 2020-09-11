package com.ejc.http.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
class UrlPatternMatcher {
    private final String path;
    private final UrlPattern urlPattern;

    @Getter
    private Map<String, String> pathVariables = new HashMap<>();

    boolean matches() {
        String[] parts = path.split("/");
        if (parts.length != urlPattern.getUrlFragments().size()) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            UrlFragment urlFragment = urlPattern.getUrlFragments().get(i);
            if (!urlFragment.matches(parts[i])) {
                return false;
            }
            urlFragment.apply(parts[i], pathVariables);
        }
        return true;
    }


}
