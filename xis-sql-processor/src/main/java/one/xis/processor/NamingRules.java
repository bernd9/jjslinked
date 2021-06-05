package one.xis.processor;

import com.ejc.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class NamingRules {

    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_(\\w*)");

    static String toSqlName(String javaName) {
        return Arrays.stream(javaName.split("(?=[A-Z])"))
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }

    static String toJavaClassName(String sqlName) {
        return StringUtils.firstToUpperCase(underscoresToCamelCase(sqlName));
    }

    static String underscoresToCamelCase(String sqlName) {
        return Arrays.stream(sqlName.split("_"))
                .map(StringUtils::firstToUpperCase)
                .collect(Collectors.joining());

    }

}
