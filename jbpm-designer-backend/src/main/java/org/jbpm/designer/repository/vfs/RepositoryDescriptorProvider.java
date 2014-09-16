package org.jbpm.designer.repository.vfs;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class RepositoryDescriptorProvider {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    private RepositoryService repositoryService;


    private Map<String, RepositoryDescriptor> knownRepositories = new ConcurrentHashMap<String, RepositoryDescriptor>();




    @PostConstruct
    public void init() {

        Collection<Repository> active = repositoryService.getRepositories();
        if (active != null) {
            for (org.guvnor.structure.repositories.Repository repo : active) {
                for (String branchName : repo.getBranches()) {
                    buildAndRegister(repo, branchName);
                }
            }
        }
    }

    public RepositoryDescriptor getRepositoryDescriptor(String repositoryAlias, String branchName) {
        if (branchName == null) {
            branchName = "master";
        }

        if (knownRepositories.containsKey(branchName+"@"+repositoryAlias)) {
            return knownRepositories.get(branchName+"@"+repositoryAlias);
        } else if (knownRepositories.size() == 1) {
            return knownRepositories.values().iterator().next();
        }  else {
            Repository repository = repositoryService.getRepository(repositoryAlias);
            if (repository != null) {
                return buildAndRegister(repository, branchName);
            }
        }

        throw new IllegalStateException("Repository with alias " + repositoryAlias + " not found");
    }

    private RepositoryDescriptor buildAndRegister(Repository repository, String branchName) {
        String repoUri = repository.getRoot().toURI().replaceFirst("://.*?@", "://"+branchName+"@");
        URI root = URI.create(repoUri);

        FileSystem fs = ioService.getFileSystem(root);
        Path rootPath = fs.provider().getPath(root);

        RepositoryDescriptor descriptor = new RepositoryDescriptor(root, fs, rootPath);
        knownRepositories.put(branchName+"@"+repository.getAlias(), descriptor);

        return descriptor;
    }
}
