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

package org.jbpm.designer.web.preprocessing.impl;

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
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.workbench.events.NotificationEvent;


import javax.enterprise.event.Event;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitVFSTest extends RepositoryBaseTest {

    @Mock
    private Event<DesignerWorkitemInstalledEvent> workitemInstalledEvent;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private POMService pomService;

    @Mock
    private ProjectService<? extends Project> projectService;

    @Mock
    private MetadataService metadataService;

    private JbpmPreprocessingUnit preprocessingUnitVFS;

    private Repository repository;

    private String uniqueId;

    private Map<String, String> params;

    protected String dirName = "myprocesses";
    protected String processFileName = "process";

    @Before
    public void setup() {
        super.setup();

        repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
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

        // create instance of preprocessing unit
         preprocessingUnitVFS = new JbpmPreprocessingUnit(new TestServletContext(), "/", Mockito.mock(VFSService.class),
                                                            workitemInstalledEvent, notificationEvent,
                                                            pomService, projectService, metadataService);


        // setup parameters
        params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
    }

    @After
    public void teardown() {
        super.teardown();
        System.clearProperty(EditorHandler.SERVICE_REPO);
        System.clearProperty(EditorHandler.SERVICE_REPO_TASKS);
    }
    @Test
    public void testPreprocess() {

        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        // validate results
        Collection<Asset> globalAssets = repository.listAssets("/global");
        assertNotNull(globalAssets);
        assertEquals(30, globalAssets.size());
        repository.assetExists("/global/backboneformsinclude.fw");
        repository.assetExists("/global/backbonejsinclude.fw");
        repository.assetExists("/global/cancelbutton.fw");
        repository.assetExists("/global/checkbox.fw");
        repository.assetExists("/global/customeditors.json");
        repository.assetExists("/global/div.fw");
        repository.assetExists("/global/dropdownmenu.fw");
        repository.assetExists("/global/fieldset.fw");
        repository.assetExists("/global/form.fw");
        repository.assetExists("/global/handlebarsinclude.fw");
        repository.assetExists("/global/htmlbasepage.fw");
        repository.assetExists("/global/image.fw");
        repository.assetExists("/global/jqueryinclude.fw");
        repository.assetExists("/global/jquerymobileinclude.fw");
        repository.assetExists("/global/link.fw");
        repository.assetExists("/global/mobilebasepage.fw");
        repository.assetExists("/global/orderedlist.fw");
        repository.assetExists("/global/passwordfield.fw");
        repository.assetExists("/global/radiobutton.fw");
        repository.assetExists("/global/script.fw");
        repository.assetExists("/global/submitbutton.fw");
        repository.assetExists("/global/table.fw");
        repository.assetExists("/global/textarea.fw");
        repository.assetExists("/global/textfield.fw");
        repository.assetExists("/global/themes.json");
        repository.assetExists("/global/unorderedlist.fw");
        repository.assetExists("/global/defaultemailicon.gif");
        repository.assetExists("/global/defaultlogicon.gif");
        repository.assetExists("/global/patterns.json");
        repository.assetExists("/global/defaultservicenodeicon.png");

        Collection<Asset> defaultStuff = repository.listAssets("/" + dirName);
        assertNotNull(defaultStuff);
        assertEquals(2, defaultStuff.size());

        repository.assetExists("/" + dirName.replaceAll("\\s", "%20") + "/WorkDefinitions.wid");
        repository.assetExists("/" + dirName.replaceAll("\\s", "%20") + "/" + processFileName.replaceAll("\\s", "%20") + ".bpmn2");
    }

    @Test
    public void testInstallDefaultWids() throws Exception{
        System.setProperty(EditorHandler.SERVICE_REPO, ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS, "SwitchYardService,Rewardsystem");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid",
                                            "/" + dirName + "/SwitchYardService.wid",
                                            "/" + dirName + "/Rewardsystem.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png"),
                              Arrays.asList("/" + dirName + "/switchyard.gif",
                                            "/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent, Mockito.times(2)).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNoServiceRepo() throws Exception{
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS, "SwitchYardService,Rewardsystem");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent, Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNoServiceTasks() throws Exception{
        System.setProperty(EditorHandler.SERVICE_REPO, ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent, Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallNotExistingTask() throws Exception{
        System.setProperty(EditorHandler.SERVICE_REPO, ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS, "NonExistingTask");

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid"),
                              Arrays.asList("/" + dirName + "/defaultservicenodeicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent, Mockito.never()).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    @Test
    public void testInstallTwice() throws Exception{
        System.setProperty(EditorHandler.SERVICE_REPO, ServiceRepoUtilsTest.class.getResource("servicerepo").toURI().toString());
        System.setProperty(EditorHandler.SERVICE_REPO_TASKS, "MicrosoftAcademy");

        // run preprocess twice
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null, false, false, null, null);

        verifyWidsPngsAndGifs(Arrays.asList("/" + dirName + "/WorkDefinitions.wid",
                                            "/" + dirName + "/MicrosoftAcademy.wid"),
                              Arrays.asList("/" + dirName + "/microsoftacademy.png",
                                            "/" + dirName + "/defaultservicenodeicon.png"),
                              Arrays.asList("/" + dirName + "/defaultemailicon.gif",
                                            "/" + dirName + "/defaultlogicon.gif"));

        Mockito.verify(workitemInstalledEvent, Mockito.times(2)).fire(Matchers.any(DesignerWorkitemInstalledEvent.class));
    }

    private void verifyWidsPngsAndGifs(List<String> wids, List<String> pngs, List<String> gifs) {
        if(wids != null) {
            Collection<Asset> storedWids = repository.listAssetsRecursively("/", new FilterByExtension("wid"));
            assertEquals(wids.size(), storedWids.size());
            for (String wid : wids) {
                repository.assetExists(wid.replaceAll("\\s", "%20"));
            }
        }

        if(pngs != null) {
            Collection<Asset> storedPngs = repository.listAssetsRecursively("/", new FilterByExtension("png"));
            assertEquals(pngs.size(), storedPngs.size());
            for (String png : pngs) {
                repository.assetExists(png.replaceAll("\\s", "%20"));
            }
        }

        if(gifs != null) {
            Collection<Asset> storedGifs = repository.listAssetsRecursively("/", new FilterByExtension("gif"));
            assertEquals(gifs.size(), storedGifs.size());
            for (String gif : gifs) {
                repository.assetExists(gif.replaceAll("\\s", "%20"));
            }
        }
    }

}
