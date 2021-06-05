package com.ejc.http.api.controller;

import java.util.Map;

public interface UrlFragment {

    default boolean matches(String part) {
        return true;
    }

    default void apply(String part, Map<String, String> result) {

    }
}
