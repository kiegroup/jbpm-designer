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
        public void fire( ResourceOpenedEvent event ) {
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

        when(readOnlyPlaceRequest.getParameter(anyString(), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "true";
            }
        });

        when(readPlaceRequst.getParameter(anyString(), anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return "false";
            }
        });


        when(assetService.getEditorParameters(anyObject(), anyString(), anyString(), anyObject())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                PlaceRequest place = (PlaceRequest) args[3];

                if(place.getParameter("readonly", "false").equals("true")) {
                    resourceOpenedEvent.fire(testResourceOpenedEvent);
                }
                return null;
            }
        });

        assetService.getEditorParameters(null, null, null, readPlaceRequst);
        assertEquals(receivedEvents.size(), 0);

        assetService.getEditorParameters(null, null, null, readOnlyPlaceRequest);
        assertEquals(receivedEvents.size(), 1);
    }
}
