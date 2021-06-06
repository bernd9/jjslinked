package one.xis.http.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class UrlPatternMatcher {
    private final String path;
    private final UrlPattern urlPattern;

    @Getter
    private Map<String, String> pathVariables = new HashMap<>();

    boolean matches() {
        List<String> parts = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (parts.size() != urlPattern.getUrlFragments().size()) {
            return false;
        }
        for (int i = 0; i < parts.size(); i++) {
            UrlFragment urlFragment = urlPattern.getUrlFragments().get(i);
            if (!urlFragment.matches(parts.get(i))) {
                return false;
            }
            urlFragment.apply(parts.get(i), pathVariables);
        }
        return true;
    }


}
