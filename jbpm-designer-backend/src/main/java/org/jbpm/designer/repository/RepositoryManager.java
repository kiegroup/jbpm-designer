package org.jbpm.designer.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryManager {

    private static RepositoryManager instance;

    private Map<String, Repository> availableRepositories = new ConcurrentHashMap<String, Repository>();

    private RepositoryManager() {

    }

    public Repository getRepository(String repositoryId) {
        return this.availableRepositories.get(repositoryId);
    }

    public void registerRepository(String repositoryId, Repository repository) {
        if (this.availableRepositories.containsKey(repositoryId)) {
            return;
        }
        this.availableRepositories.put(repositoryId, repository);
    }

    public Repository unregisterRepository(String repositoryId) {

        Repository repository = this.availableRepositories.get(repositoryId);

        this.availableRepositories.remove(repository);

        return repository;
    }

    public static RepositoryManager getInstance() {
        if (instance == null) {
            instance = new RepositoryManager();
        }

        return instance;
    }
}
