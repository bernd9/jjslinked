package com.ejc.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpStatusException extends RuntimeException {
    private final int status;

    public HttpStatusException(int status, String message) {
        super(message);
        this.status = status;
    }
}
