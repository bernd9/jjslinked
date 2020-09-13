package com.ejc.processor;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileObjectClassLoader extends ClassLoader {
    private Collection<JavaFileObject> javaFileObjects;
    private Map<String, Class<?>> classes = new HashMap<>();
    private Map<String, byte[]> bytesByName = new HashMap<>();

    public FileObjectClassLoader(ClassLoader classLoader, Collection<JavaFileObject> javaFileObjects) {
        super(classLoader);
        this.javaFileObjects = javaFileObjects;
        init();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (bytesByName.containsKey(name)) {
            return defineClass(name, bytesByName.get(name), 0, bytesByName.get(name).length);
        }
        return Class.forName(name);
    }


    private void init() {
        javaFileObjects.stream()
                .filter(o -> o.getKind() == JavaFileObject.Kind.CLASS)
                .forEach(this::addClass);
    }

    private void addClass(JavaFileObject fileObject) {
        byte[] bytes = getBytes(fileObject);
        className(fileObject).ifPresent(name -> bytesByName.put(name, bytes));

    }

    private byte[] getBytes(JavaFileObject fileObject) {
        try (ByteArrayInputStream in = (ByteArrayInputStream) fileObject.openInputStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Pattern NAME_PATTERN = Pattern.compile("/[A-Z_]+/(.*).class");

    private Optional<String> className(JavaFileObject fileObject) {
        Matcher m = NAME_PATTERN.matcher(fileObject.getName());
        if (m.find()) {
            return Optional.of(m.group(1).replace('/', '.'));
        }
        return Optional.empty();
    }
}
