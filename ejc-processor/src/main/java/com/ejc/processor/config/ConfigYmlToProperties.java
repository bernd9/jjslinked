package com.ejc.processor.config;

import java.io.File;
import java.util.Map;

public class ConfigYmlToProperties {


    public void writePropertyFiles() {
        new ConfigYmlFinder().getConfigFilesByProfile().forEach(this::writeProperties);
    }

    private void writeProperties(String profile, File yamlFile) {

    }


}
