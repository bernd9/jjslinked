package one.xis.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtils {

    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        if (o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public static boolean attributeEquals(Object o1, Object o2, String attributeName) {
        Object attr1 = FieldUtils.getFieldValue(o1, attributeName);
        Object attr2 = FieldUtils.getFieldValue(o2, attributeName);
        return equals(attr1, attr2);
    }


    public static int countNullValues(Object... objects) {
        return (int) Arrays.stream(objects).filter(Objects::isNull).count();
    }
}
