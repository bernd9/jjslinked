package one.xis.sql.processor;

import lombok.experimental.UtilityClass;

// TODO move to core
@UtilityClass
public class StringUtils {

    public String firstToUpperCase(String s) {
        if (s.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(s.charAt(0)));
        if (s.length() > 1) {
            builder.append(s.substring(1));
        }
        return builder.toString();
    }

    public String firstToLowerCase(String s) {
        if (s.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(s.charAt(0)));
        if (s.length() > 1) {
            builder.append(s.substring(1));
        }
        return builder.toString();
    }
}
