package com.ejc.processor;

import javax.lang.model.element.TypeElement;
import java.util.Set;

public class StoredTypeElements { // TODO generic superclass

    void add(TypeElement e) {

    }

    // TODO Ein Prozessor der nur in die Dateien schreibt, damit in der Kundenimplementierung alles in einem Kontext ist ?
    // Oder besser doch 2 Kontexte ? Vermutlich ja
    Set<StoredTypeElement> getStored() {
        return null;
    }

}
