package one.xis.sql.processor;

import com.squareup.javapoet.TypeName;
import lombok.Data;


@Data
class ParameterModel {
    private final TypeName typeName;
    private final String name;
}
