package one.xis.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class IOUtils {

    public List<String> lines(String resourceName, String charset, ClassLoader classLoader) throws IOException {
        try (InputStream in = classLoader.getResourceAsStream(resourceName)) {
            return lines(in, charset);
        }
    }

    public List<String> lines(InputStream inputStream, String charset) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
