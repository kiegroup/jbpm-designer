package org.jbpm.designer.repository.vfs;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.solder.core.Veto;
import org.jbpm.designer.server.service.PathEvent;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;

@RequestScoped
public class  RepositoryDescriptor {

    private static final String SEP = File.separator;

    @Inject
    private Instance<HttpServletRequest> httpRequest;
    @Inject
    private RepositoryDescriptorProvider provider;


    private URI repositoryRoot;
    private Path repositoryRootPath;
    private FileSystem fileSystem;
    private String path;
    private boolean configured = false;

    public RepositoryDescriptor() {

    }

    public RepositoryDescriptor(URI repositoryRoot, FileSystem fileSystem, Path repositoryRootPath) {
        this.repositoryRoot = repositoryRoot;
        this.fileSystem = fileSystem;
        this.repositoryRootPath = repositoryRootPath;

        this.configured = true;
    }

    public void onAnyDocumentEvent(@Observes PathEvent path) {
        this.path = path.getPath();
    }

    public String getStringRepositoryRoot() {
        String repo = this.repositoryRoot.toString();
        if (repo.endsWith("/")) {
            return repo.substring(0, repo.length() - 2);
        }

        return repo;
    }

    public URI getRepositoryRoot() {
        configure();
        return repositoryRoot;
    }

    public void setRepositoryRoot(URI repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    public Path getRepositoryRootPath() {
        configure();
        return repositoryRootPath;
    }

    public void setRepositoryRootPath(Path repositoryRootPath) {
        this.repositoryRootPath = repositoryRootPath;
    }

    public FileSystem getFileSystem() {
        configure();
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private void configure() {
        String repositoryAlias = "";
        if (!this.configured) {

            String uuid = httpRequest.get().getParameter("uuid");
            if (uuid == null) {
                uuid = httpRequest.get().getParameter("assetId");
            }
            if (uuid == null && path != null) {
                uuid = path;
            }
            if (uuid != null) {
                // git based pattern
                Pattern pattern = Pattern.compile("@(.*?)/");
                if (uuid.indexOf("@") == -1) {
                    // simple fs pattern
                    pattern = Pattern.compile(SEP + "(.*?)" + SEP);
                }
                Matcher matcher = pattern.matcher(uuid);
                if (matcher.find()) {
                    repositoryAlias = matcher.group(1);
                }
            }

            RepositoryDescriptor found = provider.getRepositoryDescriptor(repositoryAlias);
            this.fileSystem = found.getFileSystem();
            this.repositoryRoot = found.getRepositoryRoot();
            this.repositoryRootPath = found.getRepositoryRootPath();
        }
    }
}