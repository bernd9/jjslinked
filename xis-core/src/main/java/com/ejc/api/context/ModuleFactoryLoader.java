package com.ejc.api.context;

import com.ejc.util.ClassUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ModuleFactoryLoader {

    static final String RESOURCE_FOLDER_DIR = ApplicationContext.RESOURCE_FOLDER_DIR;
    static final String RESOURCE_FOLDER_JAR = RESOURCE_FOLDER_DIR + "/";

    public Set<ModuleFactory> load() {
        Set<ModuleFactory> factories = new HashSet<>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements) {
            factories.addAll(getModuleFactories(element));
        }
        return factories;
    }

    private Set<ModuleFactory> getModuleFactories(String element) {
        final File file = new File(element);
        Set<String> moduleFactoryNames = new HashSet<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                moduleFactoryNames.addAll(getModuleFactoryNamesFromDirectory(new File(file, "META-INF/modules")));
            } else {
                try {
                    moduleFactoryNames.addAll(getModuleFactoriesNamesFromJarFile(file));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return moduleFactoryNames.stream()
                .map(ClassUtils::createInstance)
                .map(ModuleFactory.class::cast)
                .collect(Collectors.toSet());
    }

    private Set<String> getModuleFactoriesNamesFromJarFile(File jarFile) throws Exception {
        Set<String> names = new HashSet<>();
        ZipFile jar = new ZipFile(jarFile);
        if (jar.getEntry(RESOURCE_FOLDER_DIR) == null) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(RESOURCE_FOLDER_JAR) && !name.equals(RESOURCE_FOLDER_JAR)) {
                names.add(name.replace(RESOURCE_FOLDER_JAR, ""));
            }
        }
        return names;
    }

    private Set<String> getModuleFactoryNamesFromDirectory(File modulesDir) {
        if (!modulesDir.exists()) {
            return Collections.emptySet();
        }
        return Arrays.stream(modulesDir.listFiles())
                .map(File::getName)
                //.map(name -> name.replace(RESOURCE_FOLDER_DIR, ""))
                .collect(Collectors.toSet());
    }
}
