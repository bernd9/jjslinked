package one.xis.context;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InitMethod {
    private final String methodName;

    void doInvokeMethod(Object bean) {
        try {
            var method = bean.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
