package one.xis.http.api.controller;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class UrlPattern {

    @Getter
    private List<UrlFragment> urlFragments = new LinkedList<>();

    public UrlPattern() {
    }

    public UrlPattern(List<UrlFragment> urlFragments) {
        this.urlFragments = urlFragments;
    }

    public void addPathFragment(String path) {
        urlFragments.add(new UrlPathFragment(path));
    }

    public void addPathVariable(String name) {
        urlFragments.add(new UrlVariableFragment(name));
    }

    public UrlPatternMatcher matcher(String path) {
        return new UrlPatternMatcher(path, this);
    }
}

