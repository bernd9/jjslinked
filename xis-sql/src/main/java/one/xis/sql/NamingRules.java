package one.xis.sql;

import com.ejc.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NamingRules {

    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_(\\w*)");

    public static String toSqlName(String javaName) {
        return Arrays.stream(javaName.split("(?=[A-Z])"))
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }

    public static String underscoresToCamelCase(String sqlName) {
        return Arrays.stream(sqlName.split("_"))
                .map(StringUtils::firstToUpperCase)
                .collect(Collectors.joining());

    }

}
