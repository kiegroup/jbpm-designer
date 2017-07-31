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
package org.jbpm.designer.client.parameters;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DesignerEditorParametersPublisherTest {

    @Mock
    private DesignerEditorParametersPublisherView view;

    @Mock
    private SessionInfo sessionInfo;

    @Spy
    @InjectMocks
    private DesignerEditorParametersPublisher publisher = new DesignerEditorParametersPublisher();

    private Map<String, String> parameters;

    @Before
    public void clearParameters() {
        when(sessionInfo.getId()).thenReturn("12345");
        parameters = new HashMap<String, String>();
    }

    @Test
    public void testPublish() {
        publisher.publish(parameters);

        verify(publisher,
               times(1)).publishProcessSources(parameters);
        verify(publisher,
               times(1)).publishActiveNodes(parameters);
        verify(publisher,
               times(1)).publishCompletedNodes(parameters);
        verify(publisher,
               times(1)).putTimeStampToParameters(parameters);
        verify(publisher,
               times(1)).putSessionIdToParameters(parameters);

        assertEquals(2,
                     parameters.size());
        assertTrue(parameters.containsKey("ts"));
        assertTrue(parameters.containsKey("sessionId"));
    }

    @Test
    public void testProcessSources() {
        parameters.put(DesignerEditorParametersPublisher.PROCESS_SOURCE_KEY,
                       "value");
        publisher.publishProcessSources(parameters);

        verify(view,
               times(1)).publishProcessSourcesInfo("value");
        assertEquals(1,
                     parameters.size());
        assertEquals("true",
                     parameters.get(DesignerEditorParametersPublisher.INSTANCE_VIEWMODE_KEY));
    }

    @Test
    public void testProcessSourcesEmpty() {
        parameters.put(DesignerEditorParametersPublisher.PROCESS_SOURCE_KEY,
                       "");
        publisher.publishProcessSources(parameters);

        verify(view,
               never()).publishProcessSourcesInfo(anyString());
        assertEquals(1,
                     parameters.size());
        assertEquals("false",
                     parameters.get(DesignerEditorParametersPublisher.INSTANCE_VIEWMODE_KEY));
    }

    @Test
    public void testProcessSourcesNull() {
        parameters.put(DesignerEditorParametersPublisher.PROCESS_SOURCE_KEY,
                       null);
        publisher.publishProcessSources(parameters);

        verify(view,
               never()).publishProcessSourcesInfo(anyString());
        assertEquals(1,
                     parameters.size());
        assertEquals("false",
                     parameters.get(DesignerEditorParametersPublisher.INSTANCE_VIEWMODE_KEY));
    }

    @Test
    public void testActiveNodes() {
        parameters.put(DesignerEditorParametersPublisher.ACTIVE_NODES_KEY,
                       "value");
        publisher.publishActiveNodes(parameters);

        verify(view,
               times(1)).publishActiveNodesInfo("value");
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testActiveNodesEmpty() {
        parameters.put(DesignerEditorParametersPublisher.ACTIVE_NODES_KEY,
                       "");
        publisher.publishActiveNodes(parameters);

        verify(view,
               never()).publishActiveNodesInfo(anyString());
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testActiveNodesNull() {
        parameters.put(DesignerEditorParametersPublisher.ACTIVE_NODES_KEY,
                       null);
        publisher.publishActiveNodes(parameters);

        verify(view,
               never()).publishActiveNodesInfo(anyString());
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testCompletedNodes() {
        parameters.put(DesignerEditorParametersPublisher.COMPLETED_NODES_KEY,
                       "value");
        publisher.publishCompletedNodes(parameters);

        verify(view,
               times(1)).publishCompletedNodesInfo("value");
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testCompletedNodesEmpty() {
        parameters.put(DesignerEditorParametersPublisher.COMPLETED_NODES_KEY,
                       "");
        publisher.publishCompletedNodes(parameters);

        verify(view,
               never()).publishCompletedNodesInfo(anyString());
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testCompletedNodesNull() {
        parameters.put(DesignerEditorParametersPublisher.COMPLETED_NODES_KEY,
                       null);
        publisher.publishCompletedNodes(parameters);

        verify(view,
               never()).publishCompletedNodesInfo(anyString());
        assertEquals(0,
                     parameters.size());
    }

    @Test
    public void testPutSessionId() {
        assertEquals(0,
                     parameters.size());
        publisher.putSessionIdToParameters(parameters);
        assertEquals(1,
                     parameters.size());
        assertEquals("12345",
                     parameters.get("sessionId"));
    }

    @Test
    public void testPutTimeStamp() {
        assertEquals(0,
                     parameters.size());
        publisher.putTimeStampToParameters(parameters);
        assertEquals(1,
                     parameters.size());
        assertTrue(parameters.containsKey("ts"));
        assertNotNull(parameters.get("ts"));
    }
}
