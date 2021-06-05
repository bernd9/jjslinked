package com.ejc.http.api.controller;

public class IllegalUrlPatternException extends RuntimeException {
    public IllegalUrlPatternException(String url, String pattern) {
        super(String.format("illegal expression %s' in url '%s'", pattern, url));
    }
}
