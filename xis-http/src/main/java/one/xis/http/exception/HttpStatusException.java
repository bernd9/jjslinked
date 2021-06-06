package one.xis.http.exception;

import one.xis.http.ContentType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpStatusException extends RuntimeException {
    private final int status;
    private final ContentType contentType;

    public HttpStatusException(int status, ContentType contentType, String message) {
        super(message);
        this.contentType = contentType;
        this.status = status;
    }
}
