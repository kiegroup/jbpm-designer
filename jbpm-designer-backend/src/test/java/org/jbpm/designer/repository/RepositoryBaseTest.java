/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;

import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;

public class RepositoryBaseTest {

    protected static final String REPOSITORY_ROOT = (System.getProperty("java.io.tmpdir").endsWith(File.separator)
            ?System.getProperty("java.io.tmpdir"):(System.getProperty("java.io.tmpdir") + File.separator)) + "designer-repo";
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
    
}
