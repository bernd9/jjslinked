package com.ejc.processor.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class ConfigYmlFinder {

    private static final Pattern YAML_FILE_PATTERN = Pattern.compile("application(\\-(.*))?\\.ya?ml");

    Map<String, File> getConfigFilesByProfile() {
        Map<String, File> rv = new HashMap<>();
        getClassDirectories()
                .map(this::getConfigFilesByProfile)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .forEach(e -> rv.put(e.getKey(), e.getValue()));
        return rv;

    }

    private Map<String, File> getConfigFilesByProfile(File directory) {
        Map<String, File> rv = new HashMap<>();
        for (File file : directory.listFiles()) {
            Matcher matcher = YAML_FILE_PATTERN.matcher(file.getName());
            if (matcher.find()) {
                rv.put(matcher.group(2) != null ? matcher.group(2) : "default", file);
            }
        }
        return rv;
    }

    private Stream<File> getClassDirectories() {
        return getClassPathElements()
                .map(File::new)
                .filter(File::isDirectory);
    }

    private Stream<String> getClassPathElements() {
        String classPath = System.getProperty("java.class.path", ".");
        return Arrays.stream(classPath.split(System.getProperty("path.separator")));
    }
}
