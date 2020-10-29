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

package org.jbpm.designer.web.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.IDiagramProfileService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CalledElementServletTest extends RepositoryBaseTest {

    @Mock
    IDiagramProfileService profileService;

    @InjectMocks
    private CalledElementServlet servlet = spy(new CalledElementServlet());

    @Before
    public void setup() {
        super.setup();
        when(profileService.findProfile(any(HttpServletRequest.class),
                                        anyString())).thenReturn(profile);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testDoPostFindProfile() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               times(1)).findProfile(request,
                                     "jbpm");
    }

    @Test
    public void testDoPostProfileAlreadySet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        servlet.profile = profile;
        try {
            servlet.doPost(request,
                           mock(HttpServletResponse.class));
        } catch (Exception e) {
            // exception thrown due to mocked request and response
        }
        verify(profileService,
               never()).findProfile(any(HttpServletRequest.class),
                                    anyString());
    }

    @Test
    public void testGetJavaTypeNames() throws Exception {
        final PackageDataModelOracle oracle = mock(PackageDataModelOracle.class);
        when(oracle.getModulePackageNames()).thenReturn(Arrays.asList("org"));

        ModelField[] modelFields = new ModelField[]{
                new ModelField("this",
                               "org.Address",
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.SELF,
                               FieldAccessorsAndMutators.ACCESSOR,
                               DataType.TYPE_THIS),
                new ModelField("street",
                               String.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_STRING),
                new ModelField("homeAddress",
                               Boolean.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_BOOLEAN),
                new ModelField("number",
                               Integer.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_NUMERIC_INTEGER)};

        Map<String, ModelField[]> fields = new java.util.HashMap<>();
        fields.put("org.Address",
                   modelFields);

        when(oracle.getModuleModelFields()).thenReturn(fields);

        CalledElementServlet servlet = new CalledElementServlet();
        List<String> javatypeNameList = servlet.getJavaTypeNames(oracle);

        assertNotNull(javatypeNameList);
        assertEquals(1,
                     javatypeNameList.size());
        assertEquals("org.Address",
                     javatypeNameList.get(0));
    }

    @Test
    public void testGetProcessInfo() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder;

        builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("<bpmn2:process id=\"com.sample.testprocess1\" drools:packageName=\"com.myteam.myproject\" drools:version=\"1.0\" name=\"Test Process 1\" isExecutable=\"true\"></bpmn2:process>")
                .type("bpmn2")
                .name("testprocess1")
                .location("/defaultPackage");
        repository.createAsset(builder.getAsset());

        builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("<bpmn2:process id=\"com.sample.testprocess2\" drools:packageName=\"com.myteam.myproject\" drools:version=\"1.0\" name=\"Test Process 2\" isExecutable=\"true\"></bpmn2:process>")
                .type("bpmn2")
                .name("testprocess2")
                .location("/defaultPackage");
        repository.createAsset(builder.getAsset());

        CalledElementServlet servlet = new CalledElementServlet();
        servlet.profile = profile;

        Map<String, String> params = new HashMap<String, String>();
        params.put("action",
                   "showProcessesInPackage");

        servlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        servlet.doPost(new TestHttpServletRequest(params),
                       response);

        String returnData = new String(response.getContent());
        assertNotNull(returnData);
        assertTrue(returnData.contains("com.sample.testprocess1|/defaultPackage|testprocess1.bpmn2"));
        assertTrue(returnData.contains("com.sample.testprocess2|/defaultPackage|testprocess2.bpmn2"));
    }

    @Test
    public void testOpenProcessInTabExtendedChars() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder;

        builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("<bpmn2:process id=\"com.myteam.тестПроцесс\" drools:packageName=\"com.myteam\" drools:version=\"1.0\" name=\"Test Process 1\" isExecutable=\"true\"></bpmn2:process>")
                .type("bpmn2")
                .name("тестПроцесс")
                .location("/defaultPackage");
        repository.createAsset(builder.getAsset());

        CalledElementServlet servlet = new CalledElementServlet();
        servlet.profile = profile;

        Map<String, String> params = new HashMap<String, String>();
        params.put("action",
                   "openprocessintab");
        params.put("pid", "com.myteam.тестПроцесс");

        servlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        servlet.doPost(new TestHttpServletRequest(params),
                       response);

        String returnData = new String(response.getContent());
        assertNotNull(returnData);
        // make sure returnData includes both process name and the uriutils encoded process name
        assertTrue(returnData.contains("тестПроцесс.bpmn2"));
        assertTrue(returnData.contains(UriUtils.encode("тестПроцесс.bpmn2")));
    }
}
