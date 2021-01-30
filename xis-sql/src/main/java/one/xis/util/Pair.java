package one.xis.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO move to core
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pair<T1,T2> {
    private T1 value1;
    private T2 value2;
}
