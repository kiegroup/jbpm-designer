/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
