package org.jbpm.designer.service;

import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.server.service.DefaultDesignerAssetService;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.event.Event;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


        when(assetService.getEditorParameters(any(Path.class), anyString(), anyString(), any(PlaceRequest.class))).thenAnswer(new Answer<String>() {
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
