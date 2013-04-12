package org.jbpm.designer.repository.vfs;

import java.net.URI;

import org.jboss.solder.core.Veto;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;

@Veto
public class  RepositoryDescriptor {

    private URI repositoryRoot;
    private Path repositoryRootPath;
    private FileSystem fileSystem;

    public RepositoryDescriptor() {

    }

    public RepositoryDescriptor(URI repositoryRoot, FileSystem fileSystem, Path repositoryRootPath) {
        this.repositoryRoot = repositoryRoot;
        this.fileSystem = fileSystem;
        this.repositoryRootPath = repositoryRootPath;
    }

    public String getStringRepositoryRoot() {
        String repo = this.repositoryRoot.toString();
        if (repo.endsWith("/")) {
            return repo.substring(0, repo.length() - 2);
        }

        return repo;
    }

    public URI getRepositoryRoot() {
        return repositoryRoot;
    }

    public void setRepositoryRoot(URI repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    public Path getRepositoryRootPath() {
        return repositoryRootPath;
    }

    public void setRepositoryRootPath(Path repositoryRootPath) {
        this.repositoryRootPath = repositoryRootPath;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
}