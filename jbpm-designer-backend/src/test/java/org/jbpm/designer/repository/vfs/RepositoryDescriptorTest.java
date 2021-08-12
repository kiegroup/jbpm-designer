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

import javax.enterprise.inject.Instance;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryDescriptorTest {

    @Mock
    private RepositoryDescriptorProvider provider;

    @Mock
    private Instance<HttpServletRequest> httpRequests;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private RepositoryDescriptor repositoryDescriptorMock;

    @InjectMocks
    private RepositoryDescriptor repositoryDescriptor = new RepositoryDescriptor();

    @Before
    public void setup() {
        doReturn(httpRequest).when(httpRequests).get();
        doReturn(repositoryDescriptorMock).when(provider).getRepositoryDescriptor(any(),
                                                                                  any(),
                                                                                  any());
    }

    @Test
    public void configureGitDirTest() {
        doReturn("default://main@space/project/src/main/resources/com/space/project/asset.bpmn2").when(httpRequest).getParameter("assetId");

        repositoryDescriptor.configure();

        verify(provider).getRepositoryDescriptor(new Space("space"),
                                                 "project",
                                                 "main");
    }
}
