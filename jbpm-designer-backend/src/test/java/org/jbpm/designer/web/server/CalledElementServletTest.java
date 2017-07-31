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

package org.jbpm.designer.web.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CalledElementServletTest extends RepositoryBaseTest {

    @Mock
    IDiagramProfileService profileService;

    @Spy
    @InjectMocks
    private CalledElementServlet servlet = new CalledElementServlet();

    @Before
    public void setup() {
        super.setup();
        when(profileService.findProfile(any(HttpServletRequest.class),
                                        anyString())).thenReturn(profile);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testDoPostFindProfile() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               times(1)).findProfile(request,
                                     "jbpm");
    }

    @Test
    public void testDoPostProfileAlreadySet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        servlet.profile = profile;
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               never()).findProfile(any(HttpServletRequest.class),
                                    anyString());
    }
}
