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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestIDiagramProfile;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.stringtemplate.v4.ST;
import org.uberfire.backend.server.util.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitVFSGitTest extends RepositoryBaseTest {

    // TODO change it to generic independent path
    protected static final String REPOSITORY_ROOT = "designer-playground";
    protected static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    protected static final String USERNAME = "guvnorngtestuser1";
    protected static final String PASSWORD = "test1234";
    protected static final String ORIGIN_URL = "https://github.com/mswiderski/designer-playground.git";
    protected static final String FETCH_COMMAND = "?fetch";

    protected static String gitLocalClone = System.getProperty("java.io.tmpdir") + "git-repo";
    protected static Map<String, String> env = new HashMap<String, String>();

    protected VFSFileSystemProducer producer = new VFSFileSystemProducer();

    protected String dirName = "myprocesses";
    protected String processFileName = "process";

    @Spy
    @InjectMocks
    private JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();

    @BeforeClass
    public static void prepare() {

        env.put("username",
                USERNAME);
        env.put("password",
                PASSWORD);
        env.put("origin",
                ORIGIN_URL);
        env.put("fetch.cmd",
                FETCH_COMMAND);
        System.setProperty("org.kie.nio.git.dir",
                           gitLocalClone);
    }

    @AfterClass
    public static void cleanup() {
        System.clearProperty("org.kie.nio.git.dir");
    }

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testProprocess() {
        Repository repository = createRepository();
        //prepare folders that will be used
        repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        // prepare process asset that will be used to preprocess
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());

        // create instance of preprocessing unit
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);

        // run preprocess
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
        repository.assetExists("/global/defaultemailicon.gif");
        repository.assetExists("/global/defaultmilestoneicon.png");
        repository.assetExists("/global/defaultlogicon.gif");
        repository.assetExists("/global/defaultservicenodeicon.png");
        repository.assetExists("/global/defaultbusinessrulesicon.png");
        repository.assetExists("/global/defaultdecisionicon.png");
        repository.assetExists("/global/patterns.json");
        repository.assetExists("/global/.gitignore");

        Collection<Asset> defaultStuff = repository.listAssets("/" + dirName);
        assertNotNull(defaultStuff);
        assertEquals(2,
                     defaultStuff.size());
        repository.assetExists("/" + dirName.replaceAll("\\s",
                                                        "%20") + "/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/" + dirName.replaceAll("\\s",
                                                        "%20") + "/process.bpmn2");
        repository.assetExists("/" + dirName.replaceAll("\\s",
                                                        "%20") + "/.gitignore");
    }

    @Test
    public void testWorkitemParameterValues() throws Exception {
        Repository repository = createRepository();
        Directory testProjectDir = repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("[\n" +
                                "    [\n" +
                                "        \"name\" : \"TestServiceWithParamValues\",\n" +
                                "        \"description\" : \"TestServiceWithParamValues\",\n" +
                                "        \"parameters\" : [\n" +
                                "            \"param1\" : new StringDataType(),\n" +
                                "            \"param2\" : new StringDataType(),\n" +
                                "            \"param3\" : new StringDataType()\n" +
                                "        ],\n" +
                                "        \"parameterValues\" : [\n" +
                                "            \"param1\" : new EnumDataType(\"org.jbpm.designer.web.preprocessing.impl.CarsEnum\"),\n" +
                                "            \"param2\" : \"\",\n" +
                                "            \"param3\" : \"one,two,three\"\n" +
                                "        ],\n" +
                                "        \"results\" : [\n" +
                                "            \"result1\" : new StringDataType(),\n" +
                                "            \"result2\" : new StringDataType()\n" +
                                "        ],\n" +
                                "        \"displayName\" : \"TestServiceWithParamValues\",\n" +
                                "        \"icon\" : \"widicon.png\",\n" +
                                "        \"category\": \"MyTestServices\",\n" +
                                "\t    \"dependencies\" : [\n" +
                                "        ]\n" +
                                "    ]\n" +
                                "]\n")
                .type("wid")
                .name("processwid")
                .location("/" + dirName);
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/" + dirName);
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.setBuilderCache(getBuilderCache(mockProject));
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();

        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository,
                                                     mockProject);

        assertNotNull(workDefinitions);
        assertEquals(1,
                     workDefinitions.size());
        assertTrue(workDefinitions.containsKey("TestServiceWithParamValues"));
        assertNotNull(workDefinitions.get("TestServiceWithParamValues").getParameterValues());
        assertEquals(2,
                     workDefinitions.get("TestServiceWithParamValues").getParameterValues().size());
        Map<String, Object> paramValues = (workDefinitions.get("TestServiceWithParamValues").getParameterValues());
        assertTrue(paramValues.containsKey("param1"));
        assertTrue(paramValues.containsKey("param3"));
        assertFalse(paramValues.containsKey("param2"));

        assertEquals("TOYOTA,MAZDA,FORD,NISSAN,HONDA",
                     paramValues.get("param1"));
        assertEquals("one,two,three",
                     paramValues.get("param3"));

        Map<String, Map<String, List<String>>> customParams = new HashMap<>();
        for (Map.Entry<String, WorkDefinitionImpl> widEntry : workDefinitions.entrySet()) {
            WorkDefinitionImpl widImpl = widEntry.getValue();
            if (widImpl.getParameterValues() != null) {
                Map<String, Object> widImplParamValues = widImpl.getParameterValues();

                Map<String, List<String>> customParamsValueMap = new HashMap<>();
                for (Map.Entry<String, Object> widParamValueEntry : widImplParamValues.entrySet()) {
                    if (widParamValueEntry.getValue() != null && widParamValueEntry.getValue() instanceof String) {
                        customParamsValueMap.put(widParamValueEntry.getKey(),
                                                 Arrays.asList(((String) widParamValueEntry.getValue()).split(",\\s*")));
                    }
                }
                customParams.put(widEntry.getKey(),
                                 customParamsValueMap);
            }
        }

        assertNotNull(customParams);
        assertEquals(1,
                     customParams.size());
        assertTrue(customParams.containsKey("TestServiceWithParamValues"));
        assertEquals(2,
                     customParams.get("TestServiceWithParamValues").size());

        // test stringtemplate evaluation of custom parameters
        ST workItemTemplate = new ST("$workitemDefs:{k| $workitemDefs.(k).parameters:{k1| $if(customParams.(k).(k1))$ $k1.name$ $endif$ }$ }$",
                                     '$',
                                     '$');
        workItemTemplate.add("workitemDefs",
                             workDefinitions);
        workItemTemplate.add("customParams",
                             customParams);

        // custom parameters available for param1 and param3
        assertTrue(workItemTemplate.render().contains("param1"));
        assertTrue(workItemTemplate.render().contains("param3"));
        assertFalse(workItemTemplate.render().contains("param2"));

        // test stringtemplate evaluation of custom parameters
        ST workItemTemplate2 = new ST("$workitemDefs:{k| $workitemDefs.(k).parameters:{k1| $if(customParams.(k).(k1))$ $else$ $k1.name$ $endif$ }$ }$",
                                      '$',
                                      '$');
        workItemTemplate2.add("workitemDefs",
                              workDefinitions);
        workItemTemplate2.add("customParams",
                              customParams);

        // no custom parameters available for param2
        assertFalse(workItemTemplate2.render().contains("param1"));
        assertFalse(workItemTemplate2.render().contains("param3"));
        assertTrue(workItemTemplate2.render().contains("param2"));
    }

    @Test
    public void testEmptyCustomEditor() throws Exception {
        Repository repository = createRepository();
        //prepare folders that will be used
        Directory testProjectDir = repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("\n" +
                                "[\n" +
                                "\n" +
                                "  [\n" +
                                "    \"name\" : \"Rewardsystem\",\n" +
                                "    \"description\" : \"Notifies the Reward System\",\n" +
                                "    \"displayName\" : \"Rewardsystem\",\n" +
                                "    \"defaultHandler\": \"mvel: com.rewardsystem.MyRewardsHandler()\",\n" +
                                "    \"category\" : \"Rewards\",\n" +
                                "    \"customEditor\" : \"\",\n" +
                                "    \"icon\" : \"widicon.png\",\n" +
                                "  ]\n" +
                                "\n" +
                                "]")
                .type("wid")
                .name("processwid")
                .location("/" + dirName);
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/" + dirName);
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.setBuilderCache(getBuilderCache(mockProject));
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();

        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository,
                                                     mockProject);
        // $workitemDefs:{k| $if(workitemDefs.(k).customEditor)$ HAVE CUSTOM EDITOR $endif$ }$

        assertNotNull(workDefinitions);
        assertEquals(1,
                     workDefinitions.size());
        assertTrue(workDefinitions.containsKey("Rewardsystem"));
        assertTrue(workDefinitions.get("Rewardsystem").getCustomEditor() == null);

        // run it through a sample ST4 template what has the same code as the bpmn2 stencil set and make sure
        // we get right results
        ST workItemTemplate = new ST("$workitemDefs:{k| $if(workitemDefs.(k).customEditor)$CEdefined$else$CEnotdefined$endif$ }$",
                                     '$',
                                     '$');
        workItemTemplate.add("workitemDefs",
                             workDefinitions);
        assertFalse(workItemTemplate.render().contains("CEdefined"));
        assertTrue(workItemTemplate.render().contains("CEnotdefined"));

        // now test to make sure if "good" value for customEditor is given that it still works as before
        workDefinitions.get("Rewardsystem").setCustomEditor("myRewardsCustomEditor");
        ST workItemTemplate2 = new ST("$workitemDefs:{k| $if(workitemDefs.(k).customEditor)$CEdefined$else$CEnotdefined$endif$ }$",
                                      '$',
                                      '$');
        workItemTemplate2.add("workitemDefs",
                              workDefinitions);
        assertTrue(workItemTemplate2.render().contains("CEdefined"));
        assertFalse(workItemTemplate2.render().contains("CEnotdefined"));
    }

    @Test
    public void testCaseProjectSetting() throws Exception {
        Repository repository = createRepository();
        //prepare folders that will be used
        Directory testProjectDir = repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("\n" +
                                "[\n" +
                                "\n" +
                                "  [\n" +
                                "    \"name\" : \"MyCustomWorkitem\",\n" +
                                "    \"description\" : \"\",\n" +
                                "    \"displayName\" : \"MyCustomWorkitem\",\n" +
                                "    \"defaultHandler\": \"mvel: com.rewardsystem.MyCustomHandler()\",\n" +
                                "    \"category\" : \"Custom\",\n" +
                                "    \"customEditor\" : \"\",\n" +
                                "    \"icon\" : \"widicon.png\",\n" +
                                "  ]\n" +
                                "\n" +
                                "]")
                .type("wid")
                .name("processwid")
                .location("/" + dirName);
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/" + dirName);
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.setBuilderCache(getBuilderCache(mockProject));
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();

        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository,
                                                     mockProject);

        assertNotNull(workDefinitions);
        assertEquals(1,
                     workDefinitions.size());
        assertTrue(workDefinitions.containsKey("MyCustomWorkitem"));

        ST workItemTemplate = new ST("$if(caseproject)$CaseProjectTrue$else$CaseProjectFalse$endif$",
                                     '$',
                                     '$');
        workItemTemplate.add("workitemDefs",
                             workDefinitions);
        assertFalse(workItemTemplate.render().contains("CaseProjectTrue"));
        assertTrue(workItemTemplate.render().contains("CaseProjectFalse"));
    }

    @Test
    public void testEmptyIcon() throws Exception {
        Repository repository = createRepository();
        //prepare folders that will be used
        Directory testProjectDir = repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("\n" +
                                "[\n" +
                                "\n" +
                                "  [\n" +
                                "    \"name\" : \"Rewardsystem\",\n" +
                                "    \"description\" : \"Notifies the Reward System\",\n" +
                                "    \"displayName\" : \"Rewardsystem\",\n" +
                                "    \"defaultHandler\": \"mvel: com.rewardsystem.MyRewardsHandler()\",\n" +
                                "    \"category\" : \"Rewards\",\n" +
                                "    \"customEditor\" : \"true\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ]\n" +
                                "\n" +
                                "]")
                .type("wid")
                .name("processwid")
                .location("/" + dirName);
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("defaultservicenodeicon")
                .location("/global");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.setBuilderCache(getBuilderCache(mockProject));
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);

        preprocessingUnitVFS.setGlobalDir(new TestIDiagramProfile(repository).getRepositoryGlobalDir());

        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();

        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository,
                                                     mockProject);

        assertNotNull(workDefinitions);
        assertEquals(1,
                     workDefinitions.size());
        assertTrue(workDefinitions.containsKey("Rewardsystem"));
        assertNotNull(workDefinitions.get("Rewardsystem").getIcon());
        assertEquals("/global/defaultservicenodeicon.png",
                     workDefinitions.get("Rewardsystem").getIcon());
    }

    @Test
    public void testEmptyAndInvalidWidNames() throws Exception {
        Repository repository = createRepository();
        //prepare folders that will be used
        Directory testProjectDir = repository.createDirectory("/" + dirName);
        repository.createDirectory("/global");

        KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("\n" +
                                "[\n" +
                                "\n" +
                                "  [\n" +
                                "    \"name\" : \"12~!#!@$#@$#@%$#^$%&^%&^&(&)()()+_)|}{|';';/.,:{|}\",\n" +
                                "    \"description\" : \"Invalid Name\",\n" +
                                "    \"displayName\" : \"Invalid Name\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"Invalid Names Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"\",\n" +
                                "    \"description\" : \"Empty Name\",\n" +
                                "    \"displayName\" : \"Empty Name\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"Empty Names Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"      \",\n" +
                                "    \"description\" : \"Spaces Only Name\",\n" +
                                "    \"displayName\" : \"Spaces Only Name\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"Spaces Only Names Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"金江止ハオユヘ手報でゃゆ掲悩フチ経壁否訃嗅けぽ\",\n" +
                                "    \"description\" : \"Unicode Name\",\n" +
                                "    \"displayName\" : \"Unicode Name\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"Unicode Names Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"金江止ハオユヘ手報 金江止ハオユヘ手報 金江止ハオユヘ手報\",\n" +
                                "    \"description\" : \"Unicode Name With Spaces\",\n" +
                                "    \"displayName\" : \"Unicode Name With Spaces\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"Unicode Names With Spaces Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"ThisIsASampleWidName\",\n" +
                                "    \"description\" : \"Non Unicode Name\",\n" +
                                "    \"displayName\" : \"NonUnicode Name\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"NonUnicode Names Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "  [\n" +
                                "    \"name\" : \"This is some other sample Name\",\n" +
                                "    \"description\" : \"Non Unicode Name with spaces\",\n" +
                                "    \"displayName\" : \"NonUnicode Name with spaces\",\n" +
                                "    \"defaultHandler\": \"\",\n" +
                                "    \"category\" : \"NonUnicode Names with spaces Category\",\n" +
                                "    \"customEditor\" : \"false\",\n" +
                                "    \"icon\" : \"\",\n" +
                                "  ],\n" +
                                "\n" +
                                "]")
                .type("wid")
                .name("processwid")
                .location("/" + dirName);
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("defaultservicenodeicon")
                .location("/global");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.setBuilderCache(getBuilderCache(mockProject));
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);

        preprocessingUnitVFS.setGlobalDir(new TestIDiagramProfile(repository).getRepositoryGlobalDir());

        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();

        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository,
                                                     mockProject);

        assertNotNull(workDefinitions);
        assertEquals(4,
                     workDefinitions.size());

        ArrayList<String> validWidNames = new ArrayList<String>(
                Arrays.asList("金江止ハオユヘ手報でゃゆ掲悩フチ経壁否訃嗅けぽ",
                              "金江止ハオユヘ手報金江止ハオユヘ手報金江止ハオユヘ手報",
                              "ThisIsASampleWidName",
                              "ThisissomeothersampleName"));

        for(Map.Entry<String, WorkDefinitionImpl> entry : workDefinitions.entrySet()) {
            assertTrue(validWidNames.contains(entry.getKey()));
            assertTrue(validWidNames.contains(entry.getValue().getName()));
        }
    }

    private String decodeUniqueId(String uniqueId) {
        if (Base64.isBase64(uniqueId)) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                String uri = new String(decoded,
                                        "UTF-8");

                return UriUtils.encode(uri);
            } catch (UnsupportedEncodingException e) {

            }
        }

        return UriUtils.encode(uniqueId);
    }

    private LRUBuilderCache getBuilderCache(KieProject kieProject) {
        NewProjectEvent event = mock(NewProjectEvent.class);
        when(event.getProject()).thenReturn(kieProject);

        LRUBuilderCache builderCache = mock(LRUBuilderCache.class);
        KieContainer kieContainer = mock(KieContainer.class);
        when(kieContainer.getClassLoader()).thenReturn(this.getClass().getClassLoader());

        Builder workbenchBuilder = mock(Builder.class);
        when(workbenchBuilder.getKieContainer()).thenReturn(kieContainer);
        when(builderCache.getBuilder(anyObject())).thenReturn(workbenchBuilder);

        return builderCache;
    }
}
