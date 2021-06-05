package com.ejc.http.api.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class UrlVariableFragment implements UrlFragment {
    @Getter
    private final String variableName;

    @Override
    public void apply(String part, Map<String, String> result) {
        result.put(variableName, part);
    }
}
