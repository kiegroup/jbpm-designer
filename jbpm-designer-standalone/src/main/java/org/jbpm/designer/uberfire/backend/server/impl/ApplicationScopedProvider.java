package org.jbpm.designer.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@ApplicationScoped
public class ApplicationScopedProvider {

    private final DefaultSystemRepository systemRepository = new DefaultSystemRepository();

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return systemRepository;
    }
}
