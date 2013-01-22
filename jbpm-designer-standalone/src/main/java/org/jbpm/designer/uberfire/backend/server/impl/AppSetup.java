package org.jbpm.designer.uberfire.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
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

    @PostConstruct
    public void onStartup() {
        final String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final String userName = "guvnorngtestuser1";
        final String password = "test1234";
        final URI fsURI = URI.create( "git://uf-playground" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( "username", userName );
            put( "password", password );
            put( "origin", gitURL );
        }};

        FileSystem fs = null;

        try {
            fs = ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fs = ioService.getFileSystem( fsURI );
        }

        activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( "default://uf-playground", "uf-playground" );
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

}

