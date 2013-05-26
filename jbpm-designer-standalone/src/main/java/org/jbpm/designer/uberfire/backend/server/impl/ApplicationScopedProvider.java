package org.jbpm.designer.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.backend.server.repositories.SystemRepository;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@ApplicationScoped
public class ApplicationScopedProvider {

    @Produces
    @Named("system")
    public SystemRepository systemRepository() {
        return SYSTEM_REPO;
    }
}
