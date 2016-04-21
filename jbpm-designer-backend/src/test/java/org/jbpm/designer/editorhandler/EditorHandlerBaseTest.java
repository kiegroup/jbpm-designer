/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletConfig;


import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class EditorHandlerBaseTest extends RepositoryBaseTest {

    private static Boolean showPDFAnswer;

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

    @Test
    public void testDoShowPDFDoc() throws Exception {
        try {
            Repository repository = new VFSRepository(producer.getIoService());
            ((VFSRepository)repository).setDescriptor(descriptor);
            profile.setRepository(repository);

            final EditorHandler editorHandler = new EditorHandler();

            TestServletConfig config = new TestServletConfig(new TestServletContext(repository));

            // no init parameter set and no system property set
            assertFalse(editorHandler.doShowPDFDoc(config));

            // init parameter set and no system property set
            config.getServletContext().setInitParameter(EditorHandler.SHOW_PDF_DOC, "false");
            assertFalse(editorHandler.doShowPDFDoc(config));

            config.getServletContext().setInitParameter(EditorHandler.SHOW_PDF_DOC, "true");
            assertTrue(editorHandler.doShowPDFDoc(config));

            // system property overwrites config init parameter
            config.getServletContext().setInitParameter(EditorHandler.SHOW_PDF_DOC, "true");
            System.setProperty(EditorHandler.SHOW_PDF_DOC, "false");
            assertFalse(editorHandler.doShowPDFDoc(config));

            config.getServletContext().setInitParameter(EditorHandler.SHOW_PDF_DOC, "false");
            System.setProperty(EditorHandler.SHOW_PDF_DOC, "true");
            assertTrue(editorHandler.doShowPDFDoc(config));
        } finally {
            // clear system property for other tests
            System.clearProperty(EditorHandler.SHOW_PDF_DOC);
        }
    }
}
