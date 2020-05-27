package processor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Annotations {
    LinkedObservable,
    LinkedMethod,
    UserId,
    ClientId;

    private static final String PACKAGE = "com.jjslink.annotations.";

    String className() {
        return PACKAGE + name();
    }


}
