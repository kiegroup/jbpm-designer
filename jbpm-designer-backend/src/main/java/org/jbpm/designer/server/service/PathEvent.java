package org.jbpm.designer.server.service;

public class PathEvent {

    private String path;

    public PathEvent() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PathEvent(String path) {
        this.path = path;
    }
}
