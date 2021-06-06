package one.xis.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {
    JSON("application(json");
    private final String string;
}
