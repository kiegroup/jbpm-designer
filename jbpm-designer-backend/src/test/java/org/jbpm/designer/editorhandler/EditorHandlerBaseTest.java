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
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EditorHandlerBaseTest extends RepositoryBaseTest {

    @Mock
    IDiagramProfileService profileService;

    @Spy
    @InjectMocks
    private EditorHandler editorHandler = new EditorHandler();

    @Before
    public void setup() {
        super.setup();
        when(profileService.findProfile(any(HttpServletRequest.class), anyString())).thenReturn(profile);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testGetInstanceViewMode() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);

        Map<String, String> params = new HashMap<String, String>();
        params.put("instanceviewmode", "false");

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

    @Test
    public void testDoGetFindProfile() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        try {
            editorHandler.doGet(request, mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService, times(1)).findProfile(request, "jbpm");
    }

    @Test
    public void testDoGetProfileAlreadySet() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        editorHandler.setProfile(profile);
        try {
            editorHandler.doGet(request, mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService, never()).findProfile(any(HttpServletRequest.class), anyString());
    }
}
