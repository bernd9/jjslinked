package one.xis.config;

import one.xis.util.TypeUtils;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class YamlConfigFile {

    private YamlRootNode yamlRootNode = new YamlRootNode();

    static Optional<YamlConfigFile> load(String resourceFile) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFile);
        if (inputStream == null) {
            return Optional.empty();
        }
        YamlConfigFile yamlConfigFile = new YamlConfigFile();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            yamlConfigFile.parse(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.of(yamlConfigFile);
    }

    private void parse(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            yamlRootNode.append(line);
        }
    }

    <T> Optional<T> findValue(String path, Class<T> type) {
        Iterator<String> iterator = Arrays.stream(path.split("\\.")).iterator();
        return yamlRootNode.findChild(iterator, path)
                .map(YamlNode::getValue)
                .map(value -> TypeUtils.convertStringToSimple(value, type));
    }


    <C extends Collection<E>, E> Optional<C> findCollection(String path, Class<C> collectionType, Class<E> elementType) {
        Iterator<String> iterator = Arrays.stream(path.split("\\.")).iterator();
        Optional<YamlNode> result = yamlRootNode.findChild(iterator, path);
        if (!result.isPresent()) {
            return Optional.empty();
        }
        C collection = TypeUtils.emptyCollection(collectionType);
        collection.addAll(result
                .map(YamlNode::childNodes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(YamlArrayElementNode.class::isInstance)
                .map(YamlArrayElementNode.class::cast)
                .map(YamlArrayElementNode::getValue)
                .map(v -> TypeUtils.convertStringToSimple(v, elementType))
                .collect(Collectors.toList()));
        return Optional.of(collection);
    }

    <M extends Map<K, V>, K, V> Optional<M> findMap(String path, Class<M> mapType, Class<K> keyType, Class<V> valueType) {
        Iterator<String> iterator = Arrays.stream(path.split("\\.")).iterator();
        Optional<YamlNode> result = yamlRootNode.findChild(iterator, path);
        if (!result.isPresent()) {
            return Optional.empty();
        }
        M map = TypeUtils.emptyMap(mapType);
        result.map(YamlNode::childNodes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(DefaultYamlNode.class::isInstance)
                .map(DefaultYamlNode.class::cast)
                .forEach(n -> map.put(TypeUtils.convertStringToSimple(n.name, keyType),
                        TypeUtils.convertStringToSimple(n.value, valueType)));
        return Optional.of(map);
    }


    class YamlLine {
        int indents;
        String content;

        YamlLine(String line) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == ' ') {
                    indents++;
                } else break;
            }
            content = line.substring(indents);
        }
    }

    class YamlRootNode {

        private DefaultYamlNode firstChild;

        void append(String line) {
            YamlLine yamlLine = new YamlLine(line);
            if (yamlLine.content.startsWith("-")) {
                append(new YamlArrayElementNode(yamlLine));
            } else {
                append(new DefaultYamlNode(yamlLine));
            }
        }

        Optional<YamlNode> findChild(Iterator<String> path, String completePath) {
            if (!path.hasNext()) {
                throw new IllegalStateException("empty path");
            }
            String name = path.next();
            DefaultYamlNode child = firstChild;
            while (child != null) {
                if (child.name.equals(name)) {
                    if (path.hasNext()) {
                        return child.findChild(path, completePath);
                    }
                    return Optional.of(child);
                }
                child = (DefaultYamlNode) child.nextSibling;
            }
            return Optional.empty();
        }

        private void append(YamlNode yamlNode) {
            if (firstChild == null) {
                firstChild = (DefaultYamlNode) yamlNode;
            } else {
                firstChild.lastSibling().append(yamlNode);
            }
        }
    }

    @Getter
    class YamlNode {
        int indents;
        String value;
        YamlNode firstChild;
        YamlNode nextSibling;

        YamlNode(int indents) {
            this.indents = indents;
        }

        Optional<YamlNode> findChild(Iterator<String> path, String completePath) {
            String name = path.next();
            YamlNode child = firstChild;
            while (child != null) {
                if (child instanceof DefaultYamlNode) {
                    DefaultYamlNode defaultYamlNode = (DefaultYamlNode) child;
                    if (defaultYamlNode.name.equals(name)) {
                        if (path.hasNext()) {
                            return defaultYamlNode.findChild(path, completePath);
                        }
                        return Optional.of(child);
                    }
                }
                child = child.nextSibling;
            }
            return Optional.empty();

        }

        void append(YamlNode yamlNode) {
            if (yamlNode.indents == indents) {
                lastSibling().nextSibling = yamlNode;
            } else if (yamlNode.indents > indents) {
                if (firstChild == null) {
                    firstChild = yamlNode;
                } else {
                    firstChild.lastSibling().append(yamlNode);
                }
            }
        }

        YamlNode lastSibling() {
            YamlNode sibling = this;
            while (sibling.nextSibling != null) {
                sibling = sibling.nextSibling;
            }
            return sibling;
        }

        List<YamlNode> childNodes() {
            List<YamlNode> childNodes = new ArrayList<>();
            YamlNode child = firstChild;
            while (child != null) {
                childNodes.add(child);
                child = child.nextSibling;
            }
            return childNodes;
        }
    }

    class DefaultYamlNode extends YamlNode {
        String name;

        DefaultYamlNode(YamlLine line) {
            super(line.indents);
            int qm = line.content.indexOf(':');
            if (qm == -1) {
                throw new YamlParserException(line, "':' expected");
            }
            name = line.content.substring(0, qm);
            int valuePos = qm + 2;
            value = valuePos < line.content.length() ? line.content.substring(valuePos) : "";
        }

    }

    class YamlArrayElementNode extends YamlNode {
        YamlArrayElementNode(YamlLine yamlLine) {
            super(yamlLine.indents);
            String content = yamlLine.content;
            if (content.length() < 2) {
                throw new YamlParserException(content, "'-' must be followed by a blank");
            }
            if (content.charAt(0) != '-') {
                throw new YamlParserException(yamlLine, "'-' expected");
            }
            if (content.charAt(1) != ' ') {
                throw new YamlParserException(yamlLine, "' ' expected");
            }
            value = yamlLine.content.substring(2);
        }
    }

}
