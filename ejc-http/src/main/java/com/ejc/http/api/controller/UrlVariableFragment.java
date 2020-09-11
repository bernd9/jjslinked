package com.ejc.http.api.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class UrlVariableFragment implements UrlFragment {
    private final String variableName;

    @Override
    public void apply(String part, Map<String, String> result) {
        result.put(variableName, part);
    }
}
