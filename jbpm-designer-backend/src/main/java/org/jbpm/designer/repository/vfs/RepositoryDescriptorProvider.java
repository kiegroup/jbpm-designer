/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.repository.vfs;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.Space;

@ApplicationScoped
public class RepositoryDescriptorProvider {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    private RepositoryService repositoryService;

    private Map<String, RepositoryDescriptor> knownRepositories = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {

        Collection<Repository> active = repositoryService.getAllRepositoriesFromAllUserSpaces();
        if (active != null) {
            for (org.guvnor.structure.repositories.Repository repo : active) {
                for (final Branch branch : repo.getBranches()) {
                    buildAndRegister(repo,
                                     branch.getName());
                }
            }
        }
    }

    public RepositoryDescriptor getRepositoryDescriptor(Space space,
                                                        String repositoryAlias,
                                                        String branchName) {
        if (branchName == null) {
            branchName = "master";
        }

        if (knownRepositories.containsKey(branchName + "@" + repositoryAlias)) {
            return knownRepositories.get(branchName + "@" + repositoryAlias);
        } else if (knownRepositories.size() == 1) {
            return knownRepositories.values().iterator().next();
        } else {
            Repository repository = repositoryService.getRepositoryFromSpace(space, repositoryAlias);
            if (repository != null) {
                return buildAndRegister(repository,
                                        branchName);
            }
        }

        throw new FileSystemNotFoundException("Repository with alias " + repositoryAlias + " not found");
    }

    private RepositoryDescriptor buildAndRegister(Repository repository,
                                                  String branchName) {

        if (!repository.getDefaultBranch().isPresent()) {
            throw new IllegalStateException("Repository should have at least one branch.");
        }

        String repoUri = repository.getDefaultBranch().get().getPath().toURI().replaceFirst("://.*?@",
                                                                                            "://" + branchName + "@");
        URI root = URI.create(repoUri);

        FileSystem fs = ioService.getFileSystem(root);
        Path rootPath = fs.provider().getPath(root);

        RepositoryDescriptor descriptor = new RepositoryDescriptor(root,
                                                                   fs,
                                                                   rootPath);
        knownRepositories.put(branchName + "@" + repository.getAlias(),
                              descriptor);

        return descriptor;
    }
}
