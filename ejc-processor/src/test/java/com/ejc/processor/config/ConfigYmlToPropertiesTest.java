package com.ejc.processor.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfigYmlToPropertiesTest {

    private ProcessingEnvironment processingEnvironment;
    private OutputStream out;

    @BeforeEach
    void init() throws IOException {
        out = mock(OutputStream.class);
        Filer filer = mock(Filer.class);
        FileObject fileObject = mock(FileObject.class);
        processingEnvironment = mock(ProcessingEnvironment.class);
        when(processingEnvironment.getFiler()).thenReturn(filer);
        when(filer.createResource(any(), any(), any())).thenReturn(fileObject);
        when(fileObject.openOutputStream()).thenReturn(out);
    }

    @Test
    void writePropertyFiles() throws IOException {
        new ConfigYmlToProperties(processingEnvironment).writePropertyFiles();
        verify(out, atLeast(3)).write(any(), anyInt(), anyInt());
        verify(out, times(3)).close();
    }
}