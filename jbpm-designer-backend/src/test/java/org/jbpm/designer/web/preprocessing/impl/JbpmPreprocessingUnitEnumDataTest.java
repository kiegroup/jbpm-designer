/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Asset.AssetType;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class JbpmPreprocessingUnitEnumDataTest extends RepositoryBaseTest {
    private static final String REPOSITORY_ROOT = "designer-playground";
    private static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    private static final String USERNAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";
    private static final String ORIGIN_URL = "https://github.com/mswiderski/designer-playground.git";
    private static final String FETCH_COMMAND = "?fetch";

    private static String gitLocalClone = System.getProperty("java.io.tmpdir") + "git-repo";
    private static Map<String, String> env = new HashMap<String, String>();
    private static final String WITH_MAVEN_DEPS = ""
            + "import org.drools.core.process.core.datatype.impl.type.StringDataType;\n"
            + "import org.drools.core.process.core.datatype.impl.type.EnumDataType;\n"
            + "["
            + "  ["
            + "    \"name\" : \"Email\","
            + "    \"parameters\" : ["
            + "      \"From\" : new StringDataType(),"
            + "      \"To\" : new StringDataType(),"
            + "      \"Subject\" : new StringDataType(),"
            + "      \"Body\" : new StringDataType()"
            + "      ],"
            + "    \"customEditor\" : \"true\","
            + "    \"parameterValues\" : ["
            + "            \"Subject\" : new EnumDataType(\"org.jbpm.designer.test.util.CarsEnum\")"
            + "      ],"
            + "    \"mavenDependencies\" : ["
            + "        \"org.jbpm:jbpm-designer-test-util:1.0.0-SNAPSHOT\""
            + "      ],"
            + "    \"displayName\" : \"Email\","
            + "    \"icon\" : \"widicon.png\""
            + "  ]"
            + "]";
    private static final String WITHOUT_MAVEN_DEPS = ""
          + "import org.drools.core.process.core.datatype.impl.type.StringDataType;\n"
          + "import org.drools.core.process.core.datatype.impl.type.EnumDataType;\n"
          + "["
          + "  ["
          + "    \"name\" : \"Email\","
          + "    \"parameters\" : ["
          + "      \"From\" : new StringDataType(),"
          + "      \"To\" : new StringDataType(),"
          + "      \"Subject\" : new StringDataType(),"
          + "      \"Body\" : new StringDataType()"
          + "      ],"
          + "    \"customEditor\" : \"true\","
          + "    \"parameterValues\" : ["
          + "            \"Subject\" : new EnumDataType(\"org.jbpm.designer.test.util.CarsEnum\")"
          + "      ],"
          + "    \"displayName\" : \"Email\","
          + "    \"icon\" : \"widicon.png\""
          + "  ]"
          + "]";

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
    public void testWithMavenDependencyDefined() throws Exception {
        Repository repository = createRepository();
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");
        
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(AssetType.Text);
        builder.description("WithMavenDependencies")
            .location("/myprocesses")
            .name("processwid")
            .type("wid")
            .content(WITH_MAVEN_DEPS);
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
        assertTrue(workDefinitions.containsKey("Email"));
        Map<String,Object> parmValues = workDefinitions.get("Email").getParameterValues();
        assertNotNull(parmValues);
        assertEquals(1, parmValues.size());
        assertTrue(parmValues.containsKey("Subject"));
        assertEquals("TOYOTA,MAZDA,FORD,NISSAN,HONDA",parmValues.get("Subject"));
    }
    
    @Test
    public void testWithoutMavenDependencyDefined() throws Exception {
        Repository repository = createRepository();
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");
        
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(AssetType.Text);
        builder.description("WithMavenDependencies")
            .location("/myprocesses")
            .name("processwid")
            .type("wid")
            .content(WITHOUT_MAVEN_DEPS);
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
        assertTrue(workDefinitions.containsKey("Email"));
        Map<String,Object> parmValues = workDefinitions.get("Email").getParameterValues();
        assertNotNull(parmValues);
        assertEquals(0, parmValues.size());
    }

}
