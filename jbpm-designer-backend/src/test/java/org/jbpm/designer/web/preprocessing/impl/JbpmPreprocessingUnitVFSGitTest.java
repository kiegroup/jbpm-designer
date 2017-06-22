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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestIDiagramProfile;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.stringtemplate.v4.ST;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitVFSGitTest extends RepositoryBaseTest {

    // TODO change it to generic independent path
    private static final String REPOSITORY_ROOT = "designer-playground";
    private static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    private static final String USERNAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";
    private static final String ORIGIN_URL = "https://github.com/mswiderski/designer-playground.git";
    private static final String FETCH_COMMAND = "?fetch";

    private static String gitLocalClone = System.getProperty("java.io.tmpdir") + "git-repo";
    private static Map<String, String> env = new HashMap<String, String>();

    private VFSFileSystemProducer producer = new VFSFileSystemProducer();

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
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        // prepare process asset that will be used to preprocess
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("process")
                .location("/myprocesses");
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

        Collection<Asset> defaultStuff = repository.listAssets("/myprocesses");
        assertNotNull(defaultStuff);
        assertEquals(2,
                     defaultStuff.size());
        repository.assetExists("/myprocesses/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/myprocesses/process.bpmn2");
        repository.assetExists("/myprocesses/.gitignore");
    }

    @Test
    public void testWorkitemParameterValues() throws Exception {
        Repository repository = createRepository();
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("import org.jbpm.process.core.datatype.impl.type.StringDataType;\n" +
                                "import org.jbpm.process.core.datatype.impl.type.EnumDataType;\n" +
                                "[\n" +
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
                .location("/myprocesses");
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/myprocesses");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository);

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
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("import org.jbpm.process.core.datatype.impl.type.StringDataType;\n" +
                                "\n" +
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
                .location("/myprocesses");
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/myprocesses");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository);
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
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("import org.jbpm.process.core.datatype.impl.type.StringDataType;\n" +
                                "\n" +
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
                .location("/myprocesses");
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("widicon")
                .location("/myprocesses");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository);

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
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("import org.jbpm.process.core.datatype.impl.type.StringDataType;\n" +
                                "\n" +
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
                .location("/myprocesses");
        String uniqueWidID = repository.createAsset(builder.getAsset());

        AssetBuilder builder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder2.content("".getBytes())
                .type("png")
                .name("defaultservicenodeicon")
                .location("/global");
        String uniqueIconID = repository.createAsset(builder2.getAsset());

        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();
        preprocessingUnitVFS.init(new TestServletContext(),
                                  "/",
                                  null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);

        preprocessingUnitVFS.setGlobalDir(new TestIDiagramProfile(repository).getRepositoryGlobalDir());

        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions,
                                                     widAsset,
                                                     widAsset.getAssetLocation(),
                                                     repository);

        assertNotNull(workDefinitions);
        assertEquals(1,
                     workDefinitions.size());
        assertTrue(workDefinitions.containsKey("Rewardsystem"));
        assertNotNull(workDefinitions.get("Rewardsystem").getIcon());
        assertEquals("/global/defaultservicenodeicon.png",
                     workDefinitions.get("Rewardsystem").getIcon());
    }
}
