package com.ejc.processor;

import com.ejc.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Set;

@RequiredArgsConstructor
public class StoredInitMethods {

    private final String filename;
    private final ProcessingEnvironment processingEnvironment;

    @Delegate
    private Set<String> items;

    public void load() {
        this.items = Set.copyOf(IOUtils.read(processingEnvironment.getFiler(), filename));
    }

    public void save() {
        IOUtils.write(items, processingEnvironment.getFiler(), filename);
    }
}
