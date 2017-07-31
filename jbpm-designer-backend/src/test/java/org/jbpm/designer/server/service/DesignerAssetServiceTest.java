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

package org.jbpm.designer.server.service;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import org.jbpm.designer.repository.RepositoryBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DesignerAssetServiceTest extends RepositoryBaseTest {

    private final List<Object> receivedEvents = new ArrayList<Object>();

    private Event<ResourceOpenedEvent> resourceOpenedEvent = new EventSourceMock<ResourceOpenedEvent>() {
        @Override
        public void fire(ResourceOpenedEvent event) {
            receivedEvents.add(event);
        }
    };

    @Mock
    private ResourceOpenedEvent testResourceOpenedEvent;

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testGetEditorParameters() throws Exception {
        DefaultDesignerAssetService assetService = mock(DefaultDesignerAssetService.class);

        PlaceRequest readOnlyPlaceRequest = mock(PlaceRequest.class);
        PlaceRequest readPlaceRequst = mock(PlaceRequest.class);

        when(readOnlyPlaceRequest.getParameter(anyString(),
                                               anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "true";
            }
        });

        when(readPlaceRequst.getParameter(anyString(),
                                          anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "false";
            }
        });

        when(assetService.getEditorParameters(anyObject(),
                                              anyString(),
                                              anyString(),
                                              anyObject())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                PlaceRequest place = (PlaceRequest) args[3];

                if (place.getParameter("readonly",
                                       "false").equals("true")) {
                    resourceOpenedEvent.fire(testResourceOpenedEvent);
                }
                return null;
            }
        });

        assetService.getEditorParameters(null,
                                         null,
                                         null,
                                         readPlaceRequst);
        assertEquals(receivedEvents.size(),
                     0);

        assetService.getEditorParameters(null,
                                         null,
                                         null,
                                         readOnlyPlaceRequest);
        assertEquals(receivedEvents.size(),
                     1);
    }
}
