package org.jbpm.designer.repository;

public class Directory {

    private String uniqueId;
    private String name;
    private String location;

    public Directory(String uniqueId, String name, String location) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
