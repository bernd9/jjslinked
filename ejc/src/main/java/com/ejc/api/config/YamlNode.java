package com.ejc.api.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
class YamlNode {


    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("[ ]*(\\w+):[ ]*(\\w+)");
    private static final Pattern KEY_PATTERN = Pattern.compile("[ ]*(\\w+):");
    private static final Pattern ARRAY_ELEMENT_PATTERN = Pattern.compile("[ ]*\\-[ ]+(\\w+)?");

    private final int indents;
    private final String line;
    private YamlNode firstChild;
    private YamlNode nextSibling;
    private String name;
    private String value;
    private YamlNodeType nodeType;

    YamlNode(String line) {
        this.line = line;
        indents = indents(line);
        parseContent();
    }

    YamlNode() {
        indents = -1;
        line = "";
    }

    void addNode(YamlNode yamlNode) {
        if (yamlNode.indents == this.indents) {
            nextSibling = yamlNode;
        } else if (yamlNode.indents > this.indents) {
            if (firstChild != null) {
                YamlNode lastChild = firstChild.lastSibling();
                lastChild.addNode(yamlNode);
            } else {
                firstChild = yamlNode;
            }
        } else {
            throw new IllegalStateException("use upper node instead");
        }
    }

    YamlNode findNode(Iterator<String> path) {
        if (!path.hasNext()) {
            return null;
        }
        String name = path.next();
        if (name.equals(this.name)) {
            if (!path.hasNext()) {
                return this;
            }
            if (firstChild == null) {
                return null;
            }
            return firstChild.findNode(path);
        }
        YamlNode sibling = siblingByName(name);
        if (!path.hasNext()) {
            return sibling;
        }
        if (sibling.firstChild == null) {
            return null;
        }
        return sibling.firstChild.findNode(path);
    }

    private YamlNode siblingByName(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        if (nextSibling == null) {
            return null;
        }
        return nextSibling.siblingByName(name);
    }

    YamlNode lastSibling() {
        YamlNode lastSibling = this;
        while (lastSibling.nextSibling != null) {
            lastSibling = lastSibling.nextSibling;
        }
        return lastSibling;
    }

    List<YamlNode> getChildNodes() {
        List<YamlNode> childNodes = new ArrayList<>();
        YamlNode child = firstChild;
        while (child != null) {
            childNodes.add(child);
            child = child.nextSibling;
        }
        return childNodes;
    }

    private void parseContent() {
        Matcher matcher = ARRAY_ELEMENT_PATTERN.matcher(line);
        if (matcher.find()) {
            value = matcher.group(1);
            nodeType = YamlNodeType.ARRAY_ELEMENT;
            return;
        }
        matcher = KEY_VALUE_PATTERN.matcher(line);
        if (matcher.find()) {
            name = matcher.group(1);
            value = matcher.group(2);
            nodeType = YamlNodeType.KEY_VALUE;
            return;
        }
        matcher = KEY_PATTERN.matcher(line);
        if (matcher.find()) {
            name = matcher.group(1);
            nodeType = YamlNodeType.KEY;
            return;
        }

        throw new IllegalStateException();
    }

    private static int indents(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                return i;
            }
        }
        return line.length();
    }

    @Override
    public String toString() {
        return line;
    }
}
