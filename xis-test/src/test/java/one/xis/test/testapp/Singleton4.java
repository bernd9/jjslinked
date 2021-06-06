package one.xis.test.testapp;

import one.xis.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Singleton
@Getter
@RequiredArgsConstructor
public class Singleton4 implements Interface1 {
    private final Singleton2 singleton2;
}
