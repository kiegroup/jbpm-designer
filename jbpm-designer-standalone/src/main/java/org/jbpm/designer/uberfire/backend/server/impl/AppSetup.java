package org.jbpm.designer.uberfire.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

import static java.util.Arrays.*;

@ApplicationScoped
public class AppSetup {


    private final IOService ioService = new IOServiceDotFileImpl();

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Inject
    private RepositoryService repositoryService;

    private FileSystem fs = null;
    @PostConstruct
    public void onStartup() {
        try {
            Properties repositoryProps = new Properties();
            repositoryProps.load(this.getClass().getResourceAsStream("/repository.properties"));


            final String originUrl = repositoryProps.getProperty("repository.origin");
            final String userName = repositoryProps.getProperty("repository.username");
            final String password = repositoryProps.getProperty("repository.password");
            final String location = repositoryProps.getProperty("repository.location");
            Repository repository = repositoryService.getRepository(location);
            if(repository == null) {

                repositoryService.cloneRepository("git", location, originUrl, userName, password);
                repository = repositoryService.getRepository(location);
            }
            try {
                fs = ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());

            } catch (FileSystemAlreadyExistsException e) {
                fs = ioService.getFileSystem(URI.create(repository.getUri()));

            }
        } catch (Exception e) {
            throw new RuntimeException("Error when starting designer " + e.getMessage(), e);
        }
    }

    @Produces
    @Named("fileSystem")
    public FileSystem fileSystem() {
        return fs;
    }

}

