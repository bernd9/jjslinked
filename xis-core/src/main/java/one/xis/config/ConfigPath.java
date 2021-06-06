package one.xis.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ConfigPath {
    private final LinkedList<String> pathElements;

    ConfigPath(String orig) {
        pathElements = new LinkedList<>(split(orig));
    }

    boolean matches(String name) {
        List<String> compare = split(name);
        for (int i = 0; i < compare.size(); i++) {
            if (!compare.get(i).equals(pathElements.get(i))) {
                return false;
            }
        }
        for (int i = 0; i < compare.size(); i++) {
            pathElements.removeFirst();
        }
        return true;
    }

    boolean hasNext() {
        return !pathElements.isEmpty();
    }

    private static List<String> split(String orig) {
        return Arrays.asList(orig.split("\\."));
    }


}
