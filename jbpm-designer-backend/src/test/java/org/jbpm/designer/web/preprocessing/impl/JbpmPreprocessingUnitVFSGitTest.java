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

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestIDiagramProfile;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.*;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.ST;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitVFSGitTest extends RepositoryBaseTest {

    // TODO change it to generic independent path
    private static final String REPOSITORY_ROOT = "designer-playground";
    private static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    private static final String USERNAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";
    private static final String ORIGIN_URL      = "https://github.com/mswiderski/designer-playground.git";
    private static final String FETCH_COMMAND = "?fetch";

    private static String gitLocalClone = System.getProperty("java.io.tmpdir") + "git-repo";
    private static Map<String, String> env = new HashMap<String, String>();

    private VFSFileSystemProducer producer = new VFSFileSystemProducer();

    @Spy
    @InjectMocks
    private JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit();

    @BeforeClass
    public static void prepare() {

        env.put( "username", USERNAME );
        env.put( "password", PASSWORD );
        env.put( "origin", ORIGIN_URL );
        env.put( "fetch.cmd", FETCH_COMMAND );
        System.setProperty("org.kie.nio.git.dir", gitLocalClone);
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
        preprocessingUnitVFS.init(new TestServletContext(), "/", null);

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);

        // run preprocess
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
        repository.assetExists("/global/defaultservicenodeicon.png");
        repository.assetExists("/global/patterns.json");
        repository.assetExists("/global/.gitignore");

        Collection<Asset> defaultStuff = repository.listAssets("/myprocesses");
        assertNotNull(defaultStuff);
        assertEquals(2, defaultStuff.size());
        repository.assetExists("/myprocesses/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/myprocesses/process.bpmn2");
        repository.assetExists("/myprocesses/.gitignore");

    }

    @Test
    public void testEmptyCustomEditor() throws Exception {
        Repository repository = createRepository();
        //prepare folders that will be used
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("import org.drools.core.process.core.datatype.impl.type.StringDataType;\n" +
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
        preprocessingUnitVFS.init(new TestServletContext(), "/", null);
        Asset<String> widAsset = repository.loadAsset(uniqueWidID);
        Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        preprocessingUnitVFS.evaluateWorkDefinitions(workDefinitions, widAsset, widAsset.getAssetLocation(), repository);
        // $workitemDefs:{k| $if(workitemDefs.(k).customEditor)$ HAVE CUSTOM EDITOR $endif$ }$

        assertNotNull(workDefinitions);
        assertEquals(1, workDefinitions.size());
        assertTrue(workDefinitions.containsKey("Rewardsystem"));
        assertTrue(workDefinitions.get("Rewardsystem").getCustomEditor() == null);

        // run it through a sample ST4 template what has the same code as the bpmn2 stencil set and make sure
        // we get right results
        ST workItemTemplate = new ST("$workitemDefs:{k| $if(workitemDefs.(k).customEditor)$CEdefined$else$CEnotdefined$endif$ }$", '$', '$');
        workItemTemplate.add("workitemDefs", workDefinitions);
        assertFalse(workItemTemplate.render().contains("CEdefined"));
        assertTrue(workItemTemplate.render().contains("CEnotdefined"));

        // now test to make sure if "good" value for customEditor is given that it still works as before
        workDefinitions.get("Rewardsystem").setCustomEditor("myRewardsCustomEditor");
        ST workItemTemplate2 = new ST("$workitemDefs:{k| $if(workitemDefs.(k).customEditor)$CEdefined$else$CEnotdefined$endif$ }$", '$', '$');
        workItemTemplate2.add("workitemDefs", workDefinitions);
        assertTrue(workItemTemplate2.render().contains("CEdefined"));
        assertFalse(workItemTemplate2.render().contains("CEnotdefined"));

    }
}
