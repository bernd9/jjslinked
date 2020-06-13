package com.jjslinked.handlebar;

import com.github.jknack.handlebars.Options;
import com.jjslinked.processor.util.CodeGeneratorUtils;
import lombok.NonNull;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HandlebarHelpers {

    public static CharSequence iterate(Object context, Options options) throws IOException {
        if (context instanceof Iterable) {
            String delimiter = options.params.length > 0 ? options.param(0) : "";
            return StreamSupport.stream(((Iterable<Object>) context).spliterator(), false).map(e -> {
                try {
                    return options.fn(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }).collect(Collectors.joining(delimiter));
        } else {
            return options.fn();
        }

    }

    public static CharSequence ifEqual(Object value1, Object value2, Options options) throws IOException {
        if (options.params.length > 0) {
            value2 = options.param(0);
        }
        boolean evaluate;
        if (value1 == null) {
            evaluate = value2 == null;
        } else if (value2 == null) {
            evaluate = false;
        } else {
            evaluate = value1.toString().equals(value2.toString());
        }
        if (evaluate) {
            return options.fn();
        }
        return "";
    }

    public static void setVar(@NonNull String name, @NonNull Object value, Options options) {
        options.data(name.toString(), value.toString());
    }

    public static String getVar(@NonNull String name, Options options) {
        return options.data(name);
    }

    public static String firstToLower(@NonNull String value, Options options) {
        return CodeGeneratorUtils.firstToLowerCase(value);
    }

}

