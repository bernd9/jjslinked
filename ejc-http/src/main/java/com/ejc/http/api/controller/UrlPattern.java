package com.ejc.http.api.controller;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class UrlPattern {

    @Getter
    private final List<UrlFragment> urlFragments = new LinkedList<>();

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

