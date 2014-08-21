package org.jbpm.designer.shared.security;

public enum AppRoles {
    ADMIN, SUDO, MANAGER, DIRECTOR;

    public String getName() {
        return toString().toLowerCase();
    }
}
