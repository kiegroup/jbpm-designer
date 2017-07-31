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

package org.jbpm.designer.repository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

public class VFSFileSystemProducer {

    private IOService ioService = new IOServiceDotFileImpl();

    public RepositoryDescriptor produceFileSystem(final Map<String, String> env) {
        URI repositoryRoot = URI.create(env.get("repository.root"));

        FileSystem fileSystem;
        // this is a hack to avoid ERROR messages coming from UF when trying to get non-existing file system
        // there seems to be no way to ask the IOService if the file system exists
        try {
            fileSystem = ioService.newFileSystem(repositoryRoot,
                                                 env);
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(repositoryRoot);
        }

        // fetch file system changes - mainly for remote based file systems
        String fetchCommand = env.get("fetch.cmd");
        if (fetchCommand != null) {
            fileSystem = ioService.getFileSystem(URI.create(env.get("repository.root") + fetchCommand));
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(env.get("repository.root"),
                "designer-repo");

        Path rootPath = fileSystem.provider().getPath(repositoryRoot);
        return new RepositoryDescriptor(repositoryRoot,
                                        fileSystem,
                                        rootPath);
    }

    public IOService getIoService() {
        return this.ioService;
    }
}
