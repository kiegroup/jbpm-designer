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

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.Definitions;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.taskforms.BPMNFormBuilderManager;
import org.jbpm.designer.taskforms.TaskFormTemplateManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kie.workbench.common.forms.bpmn.BPMNFormBuilderService;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskFormsServletTest extends RepositoryBaseTest {

    @Mock
    protected VFSService vfsServices;

    @Mock
    protected BPMNFormBuilderService<Definitions> formBuilderService;

    @Mock
    protected BPMNFormBuilderManager builderManager;

    @Mock
    protected TaskFormTemplateManager templateManager;

    @InjectMocks
    TaskFormsServlet taskFormsServlet;

    protected String dirName = "defaultPackage";
    protected String processFileName = "process";

    @Before
    public void setup() {
        super.setup();

        when(vfsServices.get(any())).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return PathFactory.newPath((String) args[0],
                                           (String) args[0]);
            }
        });

        when(builderManager.getBuilderByFormType(anyString())).thenReturn(formBuilderService);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testTaskFormServletForFormType() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-DefaultProcess.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "form");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        Collection<Asset> formForms = repository.listAssets("/" + dirName,
                                                            new FilterByExtension("form"));
        assertEquals(0,
                     formForms.size());
    }

    @Test
    public void testTaskFormServletForFrmType() throws Exception {
        when(formBuilderService.getFormExtension()).thenReturn("frm");
        when(formBuilderService.buildFormContent(any(),
                                                 any(),
                                                 any())).thenReturn("dummyform");

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-DefaultProcess.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "frm");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        Collection<Asset> frmForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("frm"));
        assertEquals(1,
                     frmForms.size());
    }

    @Test
    public void testTaskFormServletWithUserTaskForFormType() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-UserTask.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "form");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        Collection<Asset> formForms = repository.listAssets("/" + dirName,
                                                            new FilterByExtension("form"));
        assertEquals(0,
                     formForms.size());
    }

    @Test
    public void testTaskFormServletWithUserTaskForFrmType() throws Exception {
        when(formBuilderService.getFormExtension()).thenReturn("frm");
        when(formBuilderService.buildFormContent(any(),
                                                 any(),
                                                 any())).thenReturn("dummyform");

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-UserTask.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "frm");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        Collection<Asset> frmForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("frm"));
        // process form and task form
        assertEquals(2,
                     frmForms.size());
    }

    @Test
    public void testWithUserTaskForExistingFrmType() throws Exception {
        when(formBuilderService.getFormExtension()).thenReturn("frm");
        when(formBuilderService.buildFormContent(any(),
                                                 any(),
                                                 any())).thenReturn("dummyform");

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());

        AssetBuilder formBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        formBuilder.content("form content".getBytes())
                .type("frm")
                .name("evaluate-taskform")
                .location("/" + dirName);
        repository.createAsset(formBuilder.getAsset());

        AssetBuilder formBuilder2 = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        formBuilder2.content("form content".getBytes())
                .type("frm")
                .name("testprocess-taskform")
                .location("/" + dirName);
        repository.createAsset(formBuilder2.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-UserTask.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "frm");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        Collection<Asset> frmForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("frm"));
        // process form and task form
        assertEquals(2,
                     frmForms.size());
    }

    @Test
    public void testInvalidFormType() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-DefaultProcess.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "invalidFormType");
        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        TestHttpServletResponse testResponse = new TestHttpServletResponse();
        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                testResponse);

        assertEquals("fail",
                     new String(testResponse.getContent(),
                                "UTF-8"));
    }

    @Test
    public void testFailResponseOnException() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("json",
                   readFile("BPMN2-InvalidDefaultProcess.json"));
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   null);
        params.put("formtype",
                   "frm");
        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        TestHttpServletResponse testResponse = new TestHttpServletResponse();
        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                testResponse);

        assertEquals("fail",
                     new String(testResponse.getContent(),
                                "UTF-8"));
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = TaskFormsServletTest.class.getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }
}
