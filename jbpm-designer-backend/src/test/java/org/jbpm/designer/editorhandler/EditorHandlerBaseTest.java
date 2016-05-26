/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.editorhandler;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.vfs.RepositoryBaseTest;
import org.jbpm.designer.repository.vfs.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditorHandlerBaseTest extends RepositoryBaseTest {

    @Before
    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        producer = new VFSFileSystemProducer();
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("repository.root", VFS_REPOSITORY_ROOT);
        env.put("repository.globaldir", "/global");
        descriptor = producer.produceFileSystem(env);
    }

    @After
    public void teardown() {
        File repo = new File(REPOSITORY_ROOT);
        if(repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
    }

    @Test
    public void testGetInstanceViewMode() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);

        Map<String, String> params = new HashMap<String, String>();
        params.put("instanceviewmode", "false");

        EditorHandler editorHandler = new EditorHandler();
        assertEquals(editorHandler.getInstanceViewMode(new TestHttpServletRequest(params)), "false");

        params.put("instanceviewmode", "true");
        assertEquals(editorHandler.getInstanceViewMode(new TestHttpServletRequest(params)), "true");

        params.remove("instanceviewmode");
        assertEquals(editorHandler.getInstanceViewMode(new TestHttpServletRequest(params)), "false");
    }
}
