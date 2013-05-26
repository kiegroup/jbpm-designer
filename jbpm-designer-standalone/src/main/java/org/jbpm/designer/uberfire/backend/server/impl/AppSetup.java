package org.jbpm.designer.uberfire.backend.server.impl;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.cluster.ClusterServiceFactory;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.io.impl.cluster.IOServiceClusterImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@ApplicationScoped
public class AppSetup {

    private static final String JBPM_REPO_PLAYGROUND = "jbpm-playground";
    private static final String GUVNOR_REPO_PLAYGROUND = "uf-playground";
    // default repository section - start
    private static final String JBPM_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private static final String GUVNOR_URL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private final String userName = "guvnorngtestuser1";
    private final String password = "test1234";

    private IOService ioService;

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    @Inject
    private RepositoryService repositoryService;

    private FileSystem fs = null;

    @PostConstruct
    public void onStartup() {
        try {
//            Properties repositoryProps = new Properties();
//            repositoryProps.load(this.getClass().getResourceAsStream("/repository.properties"));
//
//
//            final String originUrl = repositoryProps.getProperty("repository.origin");
//            final String userName = repositoryProps.getProperty("repository.username");
//            final String password = repositoryProps.getProperty("repository.password");
//            final String location = repositoryProps.getProperty("repository.location");
//            Repository repository = repositoryService.getRepository(location);
//            if(repository == null) {
//
//                repositoryService.cloneRepository("git", location, originUrl, userName, password);
//                repository = repositoryService.getRepository(location);
//            }
//            try {
//                fs = ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());
//
//            } catch (FileSystemAlreadyExistsException e) {
//                fs = ioService.getFileSystem(URI.create(repository.getUri()));
//
//            }

            if ( clusterServiceFactory == null ) {
                ioService = new IOServiceDotFileImpl();
            } else {
                ioService = new IOServiceClusterImpl( new IOServiceDotFileImpl(), clusterServiceFactory );
            }

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

