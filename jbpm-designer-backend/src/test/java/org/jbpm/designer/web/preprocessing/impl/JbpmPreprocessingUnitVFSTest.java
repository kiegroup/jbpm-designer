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

package org.jbpm.designer.web.preprocessing.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestIDiagramProfile;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.web.server.ServiceRepoUtilsTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitVFSTest extends RepositoryBaseTest {

    @Mock
    protected Event<DesignerWorkitemInstalledEvent> workitemInstalledEvent;

    @Mock
    protected Event<NotificationEvent> notificationEvent;

    @Mock
    protected POMService pomService;

    @Mock
    protected ProjectService<? extends Project> projectService;

    @Mock
    protected MetadataService metadataService;

    @Spy
    @InjectMocks
    protected JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();

    protected Repository repository;

    protected String uniqueId;

    protected Map<String, String> params;

    protected String dirName = "myprocesses";
    protected String processFileName = "process";

    @Before
    public void setup() {
        super.setup();
        preprocessingUnitVFS.workitemInstalledEventEvent = workitemInstalledEvent;
        preprocessingUnitVFS.notification = notificationEvent;

        repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);

        //prepare folders that will be used
        repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        // prepare process asset that will be used to preprocess
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        uniqueId = repository.createAsset(builder.getAsset());

        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  Mockito.mock(VFSService.class));

        // setup parameters
        params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
    }

    @After
    public void teardown() {
        super.teardown();
        System.clearProperty(EditorHandler.SERVICE_REPO);
        System.clearProperty(EditorHandler.SERVICE_REPO_TASKS);
    }

    @Test
    public void testPreprocess() {

        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        // validate results
        Collection<Asset> globalAssets = repository.listAssets("/global");
        assertNotNull(globalAssets);
        assertEquals(9,
                     globalAssets.size());
        repository.assetExists("/global/customeditors.json");
        repository.assetExists("/global/themes.json");
        repository.assetExists("/global/defaultmilestoneicon.png");
        repository.assetExists("/global/defaultemailicon.gif");
        repository.assetExists("/global/defaultlogicon.gif");
        repository.assetExists("/global/patterns.json");
        repository.assetExists("/global/defaultservicenodeicon.png");
        repository.assetExists("/global/defaultbusinessrulesicon.png");
        repository.assetExists("/global/defaultdecisionicon.png");

        Collection<Asset> defaultStuff = repository.listAssets("/" + dirName);
        assertNotNull(defaultStuff);
        assertEquals(2,
                     defaultStuff.size());
        repository.assetExists("/" + dirName.replaceAll("\\s",
                                                        "%20") + "/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/" + dirName.replaceAll("\\s",
                                                        "%20") + "/" + processFileName.replaceAll("\\s",
                                                                                                  "%20") + ".bpmn2");
    }

    @Test
    public void testInstallDefaultWids() throws Exception {
        System.setProperty(EditorHandler.SERVICE_REPO,
                           ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS,
                           "SwitchYardService,Rewardsystem");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid",
                                            "/" + dirName + "/SwitchYardService.wid",
                                            "/" + dirName + "/Rewardsystem.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png",
                                            "/" + dirName + "/defaultmilestoneicon.png",
                                            "/" + dirName + "/defaultbusinessrulesicon.png",
                                            "/" + dirName + "/defaultdecisionicon.png"),
                              Arrays.asList("/" + dirName + "/switchyard.gif",
                                            "/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent,
                       Mockito.times(2)).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNoServiceRepo() throws Exception {
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS,
                           "SwitchYardService,Rewardsystem");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png",
                                            "/" + dirName + "/defaultmilestoneicon.png",
                                            "/" + dirName + "/defaultbusinessrulesicon.png",
                                            "/" + dirName + "/defaultdecisionicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent,
                       Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNoServiceTasks() throws Exception {
        System.setProperty(EditorHandler.SERVICE_REPO,
                           ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png",
                                            "/" + dirName + "/defaultmilestoneicon.png",
                                            "/" + dirName + "/defaultbusinessrulesicon.png",
                                            "/" + dirName + "/defaultdecisionicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent,
                       Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNotExistingTask() throws Exception {
        System.setProperty(EditorHandler.SERVICE_REPO,
                           ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS,
                           "NonExistingTask");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png",
                                            "/" + dirName + "/defaultmilestoneicon.png",
                                            "/" + dirName + "/defaultbusinessrulesicon.png",
                                            "/" + dirName + "/defaultdecisionicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent,
                       Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallTwice() throws Exception {
        System.setProperty(EditorHandler.SERVICE_REPO,
                           ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS,
                           "MicrosoftAcademy");

        // run preprocess twice
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params),
                                        null,
                                        new TestIDiagramProfile(repository),
                                        null,
                                        false,
                                        false,
                                        null,
                                        null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid",
                                            "/" + dirName + "/MicrosoftAcademy.wid"),
                              Arrays.asList("/" + dirName + "/microsoftacademy.png",
                                            "/" + dirName + "/defaultservicenodeicon.png",
                                            "/" + dirName + "/defaultmilestoneicon.png",
                                            "/" + dirName + "/defaultbusinessrulesicon.png",
                                            "/" + dirName + "/defaultdecisionicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent,
                       Mockito.times(2)).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    private void verifyWidsPngsAndGifs(List<String> wids,
                                       List<String> pngs,
                                       List<String> gifs) {
        if (wids != null) {
            Collection<Asset> storedWids = repository.listAssetsRecursively("/",
                                                                            new FilterByExtension("wid"));
            assertEquals(wids.size(),
                         storedWids.size());
            for (String wid : wids) {
                wid.replaceAll("\\s",
                               "%20");
                repository.assetExists(wid.replaceAll("\\s", "%20"));
            }
        }

        if (pngs != null) {
            Collection<Asset> storedPngs = repository.listAssetsRecursively("/",
                                                                            new FilterByExtension("png"));
            assertEquals(pngs.size(),
                         storedPngs.size());
            for (String png : pngs) {
                png.replaceAll("\\s",
                               "%20");
                repository.assetExists(png.replaceAll("\\s", "%20"));
            }
        }

        if (gifs != null) {
            Collection<Asset> storedGifs = repository.listAssetsRecursively("/",
                                                                            new FilterByExtension("gif"));
            assertEquals(gifs.size(),
                         storedGifs.size());
            for (String gif : gifs) {
                gif.replaceAll("\\s",
                               "%20");
                repository.assetExists(gif.replaceAll("\\s", "%20"));
            }
        }
    }
}
