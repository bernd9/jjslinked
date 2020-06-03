package com.jjslinked.parameters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

    public static <T> T convert(String param, Class<T> type) {
        return null;
    }
}
