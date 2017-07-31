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

import java.io.File;
import java.util.HashMap;

import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;

public class RepositoryBaseTest {

    protected static final String REPOSITORY_ROOT = (System.getProperty("java.io.tmpdir").endsWith(File.separator)
            ? System.getProperty("java.io.tmpdir") : (System.getProperty("java.io.tmpdir") + File.separator)) + "designer-repo";
    protected static final String VFS_REPOSITORY_ROOT = "default://" + REPOSITORY_ROOT;

    protected JbpmProfileImpl profile;
    protected RepositoryDescriptor descriptor;
    protected VFSFileSystemProducer producer;

    protected void deleteFiles(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteFiles(file);
            }
            file.delete();
        }
    }

    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        producer = new VFSFileSystemProducer();
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("repository.root",
                VFS_REPOSITORY_ROOT);
        env.put("repository.globaldir",
                "/global");
        descriptor = producer.produceFileSystem(env);
    }

    public void teardown() {
        File repo = new File(REPOSITORY_ROOT);
        if (repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
    }

    public Repository createRepository() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        return repository;
    }

    public String createAsset(Repository repository,
                              String location,
                              String name,
                              String fileType,
                              String bpContent) {
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content(bpContent)
                .type(fileType)
                .name(name)
                .location(location);
        return repository.createAsset(builder.getAsset());
    }
}
