package org.jbpm.designer.repository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

public class VFSFileSystemProducer {

    private IOService ioService = new IOServiceDotFileImpl();

    public RepositoryDescriptor produceFileSystem( final Map<String, String> env ) {
        URI repositoryRoot = URI.create( env.get( "repository.root" ) );

        FileSystem fileSystem = ioService.getFileSystem( repositoryRoot );

        if ( fileSystem == null ) {

            fileSystem = ioService.newFileSystem( repositoryRoot, env, BOOTSTRAP_INSTANCE );
        }

        // fetch file system changes - mainly for remote based file systems
        String fetchCommand = (String) env.get( "fetch.cmd" );
        if ( fetchCommand != null ) {
            fileSystem = ioService.getFileSystem( URI.create( env.get( "repository.root" ) + fetchCommand ) );
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put( env.get( "repository.root" ), "designer-repo" );

        Path rootPath = fileSystem.provider().getPath( repositoryRoot );
        return new RepositoryDescriptor( repositoryRoot, fileSystem, rootPath );
    }

    public IOService getIoService() {
        return this.ioService;
    }

}
