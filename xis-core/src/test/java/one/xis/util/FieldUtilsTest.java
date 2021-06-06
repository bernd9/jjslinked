package one.xis.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FieldUtilsTest {

    class Test1 {
        private Set<String> field;
    }

    class Test2 {
        private Set field;
    }

    @Test
    void getGenericCollectionTypePresent() throws NoSuchFieldException {
        Field field = Test1.class.getDeclaredField("field");
        assertThat(FieldUtils.getGenericCollectionType(field)).isPresent();
        assertThat(FieldUtils.getGenericCollectionType(field)).contains(String.class);
    }

    @Test
    void getGenericCollectionTypeEmpty() throws NoSuchFieldException {
        Field field = Test2.class.getDeclaredField("field");
        assertThat(FieldUtils.getGenericCollectionType(field)).isEmpty();
    }
}