package org.jbpm.designer.repository.vfs;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

@ApplicationScoped
public class RepositoryDescriptorProvider {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    private RepositoryService repositoryService;

    @Inject
    private Instance<HttpServletRequest> httpRequest;

    private Map<String, RepositoryDescriptor> knownRepositories = new ConcurrentHashMap<String, RepositoryDescriptor>();

    @PostConstruct
    public void init() {

        Collection<Repository> active = repositoryService.getRepositories();
        if (active != null) {
            for (org.uberfire.backend.repositories.Repository repo : active) {
                buildAndRegister(repo);
            }
        }
    }

    @Produces
    @RequestScoped
    public RepositoryDescriptor getRepositoryDescriptor() {
        String repositoryAlias = "";
        Pattern pattern = Pattern.compile("@(.*?)/");
        Matcher matcher = pattern.matcher(httpRequest.get().getParameter("uuid"));
        if (matcher.find()) {
            repositoryAlias = matcher.group(1);
        }
        if (knownRepositories.containsKey(repositoryAlias)) {
            return knownRepositories.get(repositoryAlias);
        } else if (knownRepositories.size() == 1) {
            return knownRepositories.values().iterator().next();
        }  else {
            Repository repository = repositoryService.getRepository(repositoryAlias);
            if (repository != null) {
                return buildAndRegister(repository);
            }
        }

        throw new IllegalStateException("Repository with alias " + repositoryAlias + " not found");
    }

    private RepositoryDescriptor buildAndRegister(Repository repository) {
        URI root = URI.create(repository.getUri());

        FileSystem fs = ioService.getFileSystem(root);
        Path rootPath = fs.provider().getPath(root);

        RepositoryDescriptor descriptor = new RepositoryDescriptor(root, fs, rootPath);
        knownRepositories.put(repository.getAlias(), descriptor);

        return descriptor;
    }
}
