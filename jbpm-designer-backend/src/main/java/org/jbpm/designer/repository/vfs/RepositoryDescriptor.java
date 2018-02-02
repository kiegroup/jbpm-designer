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

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.server.service.PathEvent;
import org.jbpm.designer.util.Utils;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.Space;

@RequestScoped
public class RepositoryDescriptor {

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

    public RepositoryDescriptor(URI repositoryRoot,
                                FileSystem fileSystem,
                                Path repositoryRootPath) {
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
            return repo.substring(0,
                                  repo.length() - 1);
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
        Space space = null;
        String repositoryAlias = "";
        String branchName = "";
        if (!this.configured) {

            String uuid = Utils.getUUID(httpRequest.get());
            if (uuid == null) {
                uuid = httpRequest.get().getParameter("assetId");
            }
            if (uuid == null && path != null) {
                uuid = path;
            }
            if (uuid != null) {

                if (uuid.indexOf("@") == -1) {
                    // simple fs pattern
                    Pattern pattern = Pattern.compile(SEP + "(.*?)" + SEP + "(.*?)" + SEP);
                    Matcher matcher = pattern.matcher(uuid);
                    if (matcher.find()) {
                        space = new Space(matcher.group(1));
                        repositoryAlias = matcher.group(2);
                    }
                } else {
                    // git based pattern
                    Pattern pattern = Pattern.compile("(://)(.*?)@(.*?)/(.*?)");
                    Matcher matcher = pattern.matcher(uuid);
                    if (matcher.find()) {
                        branchName = matcher.group(2);
                        space = new Space(matcher.group(3));
                        repositoryAlias = matcher.group(4);
                    }
                }
            }

            if (space == null) {
                throw new IllegalStateException("Cannot parse space from uuid in request: " + uuid);
            }

            RepositoryDescriptor found = provider.getRepositoryDescriptor(space,
                                                                          repositoryAlias,
                                                                          branchName);
            this.fileSystem = found.getFileSystem();
            this.repositoryRoot = found.getRepositoryRoot();
            this.repositoryRootPath = found.getRepositoryRootPath();
        }
    }
}
