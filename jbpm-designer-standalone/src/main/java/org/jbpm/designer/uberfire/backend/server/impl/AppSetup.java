package org.jbpm.designer.uberfire.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;


import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

import static java.util.Arrays.*;

public class AppSetup {

    private final IOService         ioService         = new IOServiceDotFileImpl();
    private final ActiveFileSystems activeFileSystems = new ActiveFileSystemsImpl();

    private FileSystem fs;

    @PostConstruct
    public void onStartup() throws Exception {
        Properties repository = new Properties();
        repository.load(this.getClass().getResourceAsStream("/repository.properties"));


        final String originUrl = repository.getProperty("repository.origin");
        final String userName = repository.getProperty("repository.username");
        final String password = repository.getProperty("repository.password");
        final String location = repository.getProperty("repository.location");
        final String locationWithScheme = repository.getProperty("repository.scheme") + "://" + location;
        final URI fsURI = URI.create(locationWithScheme);

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( "username", userName );
            put( "password", password );
            put( "origin", originUrl );
        }};
        try {
            fs = ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fs = ioService.getFileSystem( fsURI );
        }
        activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( locationWithScheme, location );
        }}, fs.supportedFileAttributeViews() ) );
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return activeFileSystems;
    }

    @Produces
    @Named("fileSystem")
    public FileSystem fileSystem() {
        return fs;
    }

}

