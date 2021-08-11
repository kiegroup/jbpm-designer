/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.spaces.Space;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryDescriptorProviderTest {

    @Mock
    private IOService ioService;

    @Mock
    private RepositoryService repositoryService;

    @InjectMocks
    private RepositoryDescriptorProvider repositoryDescriptorProvider;

    @Before
    public void setup() {
        final FileSystem fs = mock(FileSystem.class);
        doReturn(mock(FileSystemProvider.class)).when(fs).provider();
        doReturn(fs).when(ioService).getFileSystem(any());

        List<Repository> repositories = new ArrayList<>();

        repositories.add(createRepository("repo1",
                                          "space1",
                                          "main",
                                          "main",
                                          "other-branch"));

        repositories.add(createRepository("repo2",
                                          "space1",
                                          "main",
                                          "main"));
        repositories.add(createRepository("repo1",
                                          "space2",
                                          "main",
                                          "main"));

        repositories.add(createRepository("repo with spaces",
                                          "space3",
                                          "main",
                                          "main"));
        doReturn(repositories).when(repositoryService).getAllRepositoriesFromAllUserSpaces();
    }

    @Test
    public void getRepositoryDescriptorForExistingRepositoriesTest() {
        repositoryDescriptorProvider.init();

        final RepositoryDescriptor repo1space1Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space1"),
                                                                                                                "repo1",
                                                                                                                "main");
        assertEquals("default://main@space1/repo1",
                     repo1space1Descriptor.getRepositoryRoot().toString());

        final RepositoryDescriptor repo2space1Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space1"),
                                                                                                                "repo2",
                                                                                                                "main");
        assertEquals("default://main@space1/repo2",
                     repo2space1Descriptor.getRepositoryRoot().toString());

        final RepositoryDescriptor repo1space2Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space2"),
                                                                                                                "repo1",
                                                                                                                "main");
        assertEquals("default://main@space2/repo1",
                     repo1space2Descriptor.getRepositoryRoot().toString());

        final RepositoryDescriptor repoWithSpacesspace3Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space3"),
                                                                                                                         "repowithspaces",
                                                                                                                         "main");
        assertEquals("default://main@space3/repowithspaces",
                     repoWithSpacesspace3Descriptor.getRepositoryRoot().toString());
    }

    @Test
    public void getRepositoryDescriptorForNewRepositoryTest() {
        repositoryDescriptorProvider.init();

        doReturn(createRepository("repo2",
                                  "space2",
                                  "main",
                                  "main")).when(repositoryService).getRepositoryFromSpace(new Space("space2"),
                                                                                            "repo2");

        final RepositoryDescriptor repo2space2Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space2"),
                                                                                                                "repo2",
                                                                                                                "main");
        assertEquals("default://main@space2/repo2",
                     repo2space2Descriptor.getRepositoryRoot().toString());

        doReturn(createRepository("repo with spaces",
                                  "space2",
                                  "main",
                                  "main")).when(repositoryService).getRepositoryFromSpace(new Space("space2"),
                                                                                            "repo with spaces");

        final RepositoryDescriptor repoWithSpacesSpace2Descriptor = repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space2"),
                                                                                                                         "repo with spaces",
                                                                                                                         "main");
        assertEquals("default://main@space2/repowithspaces",
                     repoWithSpacesSpace2Descriptor.getRepositoryRoot().toString());
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void getRepositoryDescriptorForUnexistentRepositoryTest() {
        repositoryDescriptorProvider.init();

        repositoryDescriptorProvider.getRepositoryDescriptor(new Space("space2"),
                                                             "repo2",
                                                             "main");
    }

    private Repository createRepository(final String alias,
                                        final String spaceName,
                                        final String defaultBranchName,
                                        final String... branchesNames) {
        final Space space = new Space(spaceName);
        final Repository repository = mock(Repository.class);

        final List<Branch> branches = new ArrayList<>();
        for (String branchName : branchesNames) {
            final String branchURI = "default://" + branchName + "@" + spaceName + "/" + alias;
            final Path branchPath = PathFactory.newPath(branchName,
                                                        branchURI);
            final Branch branch = new Branch(branchName,
                                             branchPath);
            branches.add(branch);

            if (branchName.equals(defaultBranchName)) {
                doReturn(Optional.of(branch)).when(repository).getDefaultBranch();
            }
        }

        doReturn(space).when(repository).getSpace();
        doReturn(alias).when(repository).getAlias();
        doReturn(branches).when(repository).getBranches();

        return repository;
    }
}
