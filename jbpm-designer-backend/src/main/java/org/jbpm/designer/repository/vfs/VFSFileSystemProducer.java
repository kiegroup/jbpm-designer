package org.jbpm.designer.repository.vfs;

import org.jbpm.designer.web.profile.IDiagramProfile;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

public class VFSFileSystemProducer {

    private IOService ioService = new IOServiceDotFileImpl();
    ActiveFileSystems activeFileSystems = new ActiveFileSystemsImpl();

    public FileSystem produceFileSystem(IDiagramProfile profile, final Map<String, String> env) {
        URI repositoryRoot = URI.create(profile.getRepositoryRoot());

        FileSystem fileSystem = ioService.getFileSystem( repositoryRoot );

        if ( fileSystem == null ) {

            fileSystem = ioService.newFileSystem( repositoryRoot, env, BOOTSTRAP_INSTANCE );
        }

        // fetch file system changes - mainly for remote based file systems
        String fetchCommand = (String) env.get("fetch.cmd");
        if (fetchCommand != null) {
            fileSystem = ioService.getFileSystem(URI.create(profile.getRepositoryRoot() + fetchCommand));
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(profile.getRepositoryRoot(), "designer-repo");
        activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS(map, fileSystem.supportedFileAttributeViews()) );
       return fileSystem;
    }

    public IOService getIoService() {
        return this.ioService;
    }

    public ActiveFileSystems getActiveFileSystems() {
        return this.activeFileSystems;
    }
}
