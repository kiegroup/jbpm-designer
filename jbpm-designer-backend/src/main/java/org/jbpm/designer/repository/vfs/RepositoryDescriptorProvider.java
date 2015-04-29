package org.jbpm.designer.repository.vfs;

//@ApplicationScoped
public class RepositoryDescriptorProvider {

//    @Inject
//    @Named("ioStrategy")
//    private IOService ioService;
//    @Inject
//    private RepositoryService repositoryService;
//
//
//    private Map<String, RepositoryDescriptor> knownRepositories = new ConcurrentHashMap<String, RepositoryDescriptor>();
//
//
//
//
//    @PostConstruct
//    public void init() {
//
//        Collection<Repository> active = repositoryService.getRepositories();
//        if (active != null) {
//            for (org.uberfire.backend.repositories.Repository repo : active) {
//                buildAndRegister(repo);
//            }
//        }
//    }
//
//    public RepositoryDescriptor getRepositoryDescriptor(String repositoryAlias) {
//
//
//        if (knownRepositories.containsKey(repositoryAlias)) {
//            return knownRepositories.get(repositoryAlias);
//        } else if (knownRepositories.size() == 1) {
//            return knownRepositories.values().iterator().next();
//        }  else {
//            Repository repository = repositoryService.getRepository(repositoryAlias);
//            if (repository != null) {
//                return buildAndRegister(repository);
//            }
//        }
//
//        throw new IllegalStateException("Repository with alias " + repositoryAlias + " not found");
//    }
//
//    private RepositoryDescriptor buildAndRegister(Repository repository) {
//        URI root = URI.create(repository.getUri());
//
//        FileSystem fs = ioService.getFileSystem(root);
//        Path rootPath = fs.provider().getPath(root);
//
//        RepositoryDescriptor descriptor = new RepositoryDescriptor(root, fs, rootPath);
//        knownRepositories.put(repository.getAlias(), descriptor);
//
//        return descriptor;
//    }
}
