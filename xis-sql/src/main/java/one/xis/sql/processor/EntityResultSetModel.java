package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.xis.sql.api.EntityResultSet;

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
class EntityResultSetModel {

    @Getter
    private static Set<String> resultGetters;

    static {
        resultGetters = findResultGetters();
    }

    private static  Set<String> findResultGetters() {
        Set<String> names = new HashSet<>();
        names.addAll(findResultGetters(ResultSet.class));
        names.addAll(findResultGetters(EntityResultSet.class));
        return names;
    }

    private static Set<String> findResultGetters(Class<? extends ResultSet>  resultSetClass) {
        return Arrays.stream(resultSetClass.getDeclaredMethods())
                .filter(m -> m.getParameters().length == 1)
                .filter(m -> m.getParameters()[0].getType() == String.class)
                .filter(m -> m.getName().matches("get_.*"))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

    private final EntityModel entityModel;
    private final ProcessingEnvironment processingEnvironment;

    String getSimpleName() {
        return entityModel.getSimpleName() + "ResultSet";
    }

    String getPackageName() {
        return entityModel.getPackageName();
    }

    static String getSimpleName(EntityModel entityModel) {
        return EntityProxyModel.getEntityProxySimpleName(entityModel);
    }

}
