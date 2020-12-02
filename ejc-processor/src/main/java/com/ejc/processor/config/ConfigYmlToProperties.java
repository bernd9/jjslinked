package com.ejc.processor.config;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

@RequiredArgsConstructor
public class ConfigYmlToProperties {

    private final ProcessingEnvironment processingEnvironment;
    private final ConfigYmlToPropertiesConverter converter = new ConfigYmlToPropertiesConverter();

    public void writePropertyFiles() {
        new ConfigYmlFinder().getConfigFilesByProfile().forEach(this::writeProperties);
    }

    private void writeProperties(String profile, File yamlFile) {
        try {
            Properties properties = converter.toProperties(yamlFile);
            FileObject propertiesFileObject = processingEnvironment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", applicationPropertiesFileName(profile));
            writeProperties(properties, propertiesFileObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeProperties(Properties properties, FileObject propertiesFileObject) {
        try (OutputStream out = propertiesFileObject.openOutputStream()) {
            properties.store(out, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String applicationPropertiesFileName(String profile) {
        if (profile.isEmpty()) {
            return "application.properties";
        }
        return String.format("application-%s.properties", profile);
    }
}
