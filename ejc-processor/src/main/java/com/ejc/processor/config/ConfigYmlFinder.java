package com.ejc.processor.config;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConfigYmlFinder {

    private static final Pattern YAML_FILE_PATTERN = Pattern.compile("(.*)\\.ya?ml");

    Map<String,File> getConfigFilesByProfile() {
        Map<String,File> rv = new HashMap<>();
        getClassDirectories()
                .filter(File::isDirectory)
                .map(this::getConfigFilesByProfile)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(e -> rv.put(e.getKey(), e.getValue()));
        return rv;

    }

    private Map<String,File> getConfigFilesByProfile(File directory) {
        Map<String,File> rv = new HashMap<>();
        for (File file: directory.listFiles()) {
            Matcher matcher = YAML_FILE_PATTERN.matcher(file.getName());
            if (matcher.find()) {
                rv.put(matcher.group(1), file);
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
