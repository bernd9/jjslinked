package com.ejc.http;

import com.ejc.InjectAll;
import com.ejc.Singleton;

import java.util.Collection;

@Singleton
class HttpControllerInvoker {

    @InjectAll
    private Collection<HttpRequestHandler> requestHandlers;
}
