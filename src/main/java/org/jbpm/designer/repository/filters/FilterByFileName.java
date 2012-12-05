package org.jbpm.designer.repository.filters;

import org.jbpm.designer.repository.Filter;
import org.kie.commons.java.nio.file.Path;

public class FilterByFileName implements Filter<Path> {

    private String name;

    public FilterByFileName(String name) {
        this.name = name;
    }

    public boolean accept(Path path) {
        return path.getFileName().toString().equals(name);
    }
}
