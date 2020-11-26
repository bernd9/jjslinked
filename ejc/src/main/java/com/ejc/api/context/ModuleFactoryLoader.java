package com.ejc.api.context;

import com.ejc.util.ClassUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Log4j2
@Getter
@RequiredArgsConstructor
public class ModuleFactoryLoader {

    public static final String RESOURCE_FOLDER = "META-INF/modules";

    public Set<ModuleFactory> load2() {
        ServiceLoader<ModuleFactory> loader = ServiceLoader.load(ModuleFactory.class);
        return loader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toSet());
    }

    public Set<ModuleFactory> load3() {
        return moduleLoaderClassNames()
                .map(ClassUtils::createInstance)
                .map(ModuleFactory.class::cast)
                .collect(Collectors.toSet());
    }

    public Set<ModuleFactory> load() {
        return asStream(moduleFolderUrls())
                .map(this::asPath)
                .map(this::listFiles)
                .flatMap(Collection::stream)
                .map(ClassUtils::createInstance)
                .map(ModuleFactory.class::cast)
                .collect(Collectors.toSet());
    }

    private Set<String> listFiles(Path path) {
        int index = path.toString().length() + 1;
        try {
            Set<Path> paths = new HashSet<>(Files.walk(path, 1).collect(Collectors.toSet()));
            paths.remove(path);
            return paths.stream()
                    .map(p -> p.toString().substring(index))
                    .map(Object::toString).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path asPath(URL url) {
        try {
            URI uri = url.toURI();
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                return fileSystem.getPath(RESOURCE_FOLDER);
            } else if (uri.getScheme().equals("file")) {
                return Paths.get(uri);
            } else throw new IllegalArgumentException("unsupported schema: " + uri.getScheme());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Stream<String> moduleLoaderClassNames() {
        return asStream(moduleFolderUrls())
                .map(this::listFiles)
                .flatMap(Set::stream);
    }

    private Set<String> listFiles(URL folder) {
        log.info("url:" + folder);
        String path = folder.getPath();
        return Arrays.stream(new File(path).listFiles())
                .map(File::getAbsolutePath)
                .map(resource -> resource.substring(path.length() + 1))
                .collect(Collectors.toSet());
    }


    private Enumeration<URL> moduleFolderUrls() {
        try {
            return ClassLoader.getSystemResources(RESOURCE_FOLDER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<URL> asStream(Enumeration<URL> enumeration) {
        Iterable<URL> iterable = () -> enumeration.asIterator();
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
