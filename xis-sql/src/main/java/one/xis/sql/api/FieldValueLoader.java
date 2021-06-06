package one.xis.sql.api;


import one.xis.util.ObjectUtils;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class FieldValueLoader<K, V> {
    private final Function<K, V> loaderFunction;
    private K key;
    private Optional<V> optionalValue;

    public void setKey(K key) {
        if (!ObjectUtils.equals(this.key, key)) {
            this.key = key;
            this.optionalValue = null;
        }
    }

    public V getValue() {
        if (key == null) {
            return null;
        }
        if (optionalValue == null) {
            optionalValue = Optional.ofNullable(loaderFunction.apply(key));
        }
        return optionalValue.orElse(null);
    }


}
