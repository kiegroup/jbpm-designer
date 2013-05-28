package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class AppSetup {

    private static final String JBPM_REPO_PLAYGROUND = "jbpm-playground";
    private static final String GUVNOR_REPO_PLAYGROUND = "uf-playground";
    // default repository section - start
    private static final String JBPM_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private static final String GUVNOR_URL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";

    @Inject
    private RepositoryService repositoryService;

    @PostConstruct
    public void onStartup() {
        try {
            Repository jbpmRepo = repositoryService.getRepository( JBPM_REPO_PLAYGROUND );
            if ( jbpmRepo == null ) {
                final String userName = "guvnorngtestuser1";
                final String password = "test1234";
                repositoryService.createRepository( "git", JBPM_REPO_PLAYGROUND, new HashMap<String, Object>() {{
                    put( "origin", JBPM_URL );
                    put( "username", userName );
                    put( "crypt:password", password );
                }} );
            }

            // TODO in case repo is not defined in system repository so we add default
            Repository guvnorRepo = repositoryService.getRepository( GUVNOR_REPO_PLAYGROUND );
            if ( guvnorRepo == null ) {
                final String userName = "guvnorngtestuser1";
                final String password = "test1234";
                repositoryService.createRepository( "git", GUVNOR_REPO_PLAYGROUND, new HashMap<String, Object>() {{
                    put( "origin", GUVNOR_URL );
                    put( "username", userName );
                    put( "crypt:password", password );
                }} );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Error when starting designer " + e.getMessage(), e );
        }
    }
}

