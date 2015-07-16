package org.jbpm.designer.repository.filters;

import java.nio.file.Path;

import org.jbpm.designer.repository.Filter;

public class FilterByExtension implements Filter<Path> {

    private String extension;

    public FilterByExtension(String extension) {
        this.extension = extension;
    }

    public boolean accept(Path path) {
        return path.getFileName().toString().endsWith(extension);
    }
}
