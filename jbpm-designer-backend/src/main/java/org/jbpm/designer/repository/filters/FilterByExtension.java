package org.jbpm.designer.repository.filters;

import org.jbpm.designer.repository.Filter;
import org.kie.commons.java.nio.file.Path;

public class FilterByExtension implements Filter<Path> {

    private String extension;

    public FilterByExtension(String extension) {
        this.extension = extension;
    }

    public boolean accept(Path path) {
        return path.getFileName().toString().endsWith(extension);
    }
}
