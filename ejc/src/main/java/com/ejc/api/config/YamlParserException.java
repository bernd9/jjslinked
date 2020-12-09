package com.ejc.api.config;

public class YamlParserException extends RuntimeException {
    public YamlParserException(String line, String message) {
        super(message + " in line : '" + line + "'");
    }

    public YamlParserException(YamlConfigFile.YamlLine yamlLine, String message) {
        this(yamlLine.content, message);
    }
}
