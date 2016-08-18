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
package org.jbpm.designer.web.server;

import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceRepoUtilsTest extends RepositoryBaseTest {

    @Mock
    private VFSService vfsServices;

    @Mock
    private IOService ioService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private POMService pomService;

    @Mock
    private MetadataService metadataService;

    @Captor
    private ArgumentCaptor<NotificationEvent> notificationCaptor;

    @Captor
    private ArgumentCaptor<DesignerWorkitemInstalledEvent> widinstallCaptor;

    protected Event<NotificationEvent> notification = mock(EventSourceMock.class);

    private final List<Object> receivedWidInstallEvents = new ArrayList<Object>();
    private Event<DesignerWorkitemInstalledEvent> widinstall = new EventSourceMock<DesignerWorkitemInstalledEvent>() {

        @Override
        public void fire( DesignerWorkitemInstalledEvent event ) {
            receivedWidInstallEvents.add(event);
        }

    };


    @Before
    public void setup() {
        super.setup();
        //MockitoAnnotations.initMocks(this);

        when(vfsServices.get(anyString())).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return PathFactory.newPath((String) args[0], (String) args[0]);
            }
        });

    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testInstallWorkitem() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);

        // create the bpmn2 asset
        AssetBuilder bpmn2builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        bpmn2builder.content("bpmn2 content")
                .type("bpmn2")
                .name("samplebpmn2process")
                .location("/src/main/resources");
        String bpmn2AssetID = repository.createAsset(bpmn2builder.getAsset());
        assertNotNull(bpmn2AssetID);

        // create the pom.xml
        AssetBuilder pomBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        pomBuilder.content("")
                .type("xml")
                .name("pom")
                .location("/");
        String pomAssetID = repository.createAsset(pomBuilder.getAsset());
        assertNotNull(pomAssetID);

        Collection<Asset> foundAsset = repository.listAssets("/src/main/resources", new FilterByExtension("bpmn2"));
        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        Path rootPath = Paths.convert(((VFSRepository) repository).getDescriptor().getRepositoryRootPath());

        String uuid = rootPath.toURI() + "/src/main/resources/samplebpmn2process.bpmn2";
        String pomuuid = rootPath.toURI() + "/pom.xml";

        KieProject project = Mockito.mock(KieProject.class);
        when(project.getRootPath()).thenReturn(rootPath);
        final org.uberfire.backend.vfs.Path pomXmlPath = mock( org.uberfire.backend.vfs.Path.class );

        when( project.getPomXMLPath() ).thenReturn( pomXmlPath );
        when( pomXmlPath.toURI() ).thenReturn(pomuuid);
        when(project.getPomXMLPath() ).thenReturn( pomXmlPath );

        POM projectPOM = new POM();
        when( pomService.load(pomXmlPath) ).thenReturn(projectPOM);

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);

        Map<String, WorkDefinitionImpl> workitemsFromRepo = WorkItemRepository.getWorkDefinitions(getClass().getResource("servicerepo").toURI().toString());

        // should not fail if icon is not defined
        assertNotNull(workitemsFromRepo.get("Rewardsystem"));
        WorkDefinitionImpl rewardSystemWorkItem = workitemsFromRepo.get("Rewardsystem");
        assertEquals("Rewardsystem", rewardSystemWorkItem.getName());
        assertNull(rewardSystemWorkItem.getIcon());

        ServiceRepoUtils.installWorkItem(workitemsFromRepo,
                "Rewardsystem",
                uuid,
                repository,
                vfsServices,
                widinstall,
                notification,
                pomService,
                projectService,
                metadataService);

        // jbpm-console stores deployment descriptor if it receives install event from designer
        // make sure that the install event was fired
        assertEquals(1, receivedWidInstallEvents.size());
        // make sure the event content is valid
        Object event = receivedWidInstallEvents.get(0);
        assertTrue(event instanceof DesignerWorkitemInstalledEvent);
        DesignerWorkitemInstalledEvent eventReceived = (DesignerWorkitemInstalledEvent) event;
        assertEquals("Rewardsystem", eventReceived.getName());
        assertEquals("mvel: com.rewardsystem.MyRewardsHandler()", eventReceived.getValue());

        // nake sure the correct wid maven dependencies got installed into the pom
        assertNotNull(projectPOM);
        assertNotNull(projectPOM.getDependencies());
        assertEquals(2, projectPOM.getDependencies().size());
        Dependencies pomDepends = projectPOM.getDependencies();

        Dependency depends1 = pomDepends.get(0);
        assertNotNull(depends1);
        assertEquals("com.rewardssystem", depends1.getGroupId());
        assertEquals("myrewardssystem", depends1.getArtifactId());
        assertEquals("2.0", depends1.getVersion());

        Dependency depends2 = pomDepends.get(1);
        assertNotNull(depends2);
        assertEquals("com.rewardssystem", depends2.getGroupId());
        assertEquals("systemhelper", depends2.getArtifactId());
        assertEquals("1.2", depends2.getVersion());


    }

}
