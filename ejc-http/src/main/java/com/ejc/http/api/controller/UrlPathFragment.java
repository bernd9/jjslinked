package com.ejc.http.api.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UrlPathFragment implements UrlFragment {
    private final String value;

    @Override
    public boolean matches(@NonNull String part) {
        return part.equals(value);
    }
}
