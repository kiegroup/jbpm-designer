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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskFormsServletTest extends RepositoryBaseTest {

    @Mock
    protected VFSService vfsServices;

    @Mock
    protected BPMNFormBuilderService<Definitions> formBuilderService;

    @Mock
    protected BPMNFormBuilderManager builderManager;

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

        when(builderManager.getFormBuilders()).thenReturn(Arrays.asList(formBuilderService));
        when(builderManager.getBuilderByFormType(anyString())).thenReturn(formBuilderService);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testTaskFormServletForFormType() throws Exception {
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
                   "form");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        // old ftl form should not be created
        Collection<Asset> ftlForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("ftl"));
        assertEquals(0,
                     ftlForms.size());
    }

    @Test
    public void testTaskFormServletForFrmType() throws Exception {
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

        // old ftl form should not be created
        Collection<Asset> ftlForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("ftl"));
        assertEquals(0,
                     ftlForms.size());
    }

    @Test
    public void testTaskFormServletWithUserTaskForFormType() throws Exception {
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
                   "form");

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository,
                                                                           "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params),
                                new TestHttpServletResponse());

        // old ftl form should not be created
        Collection<Asset> ftlForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("ftl"));
        assertEquals(0,
                     ftlForms.size());
    }

    @Test
    public void testTaskFormServletWithUserTaskForFrmType() throws Exception {
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

        // old ftl form should not be created
        Collection<Asset> ftlForms = repository.listAssets("/" + dirName,
                                                           new FilterByExtension("ftl"));
        assertEquals(0,
                     ftlForms.size());
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = TaskFormsServletTest.class.getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }

    private List<Asset> sortAssets(Collection<Asset> assets) {
        ArrayList<Asset> arrAssets = new ArrayList<Asset>(assets);
        arrAssets.sort(new Comparator<Asset>() {
            @Override
            public int compare(Asset a1,
                               Asset a2) {
                if (a1.getName() == null) {
                    return -1;
                } else if (a2.getName() == null) {
                    return 1;
                } else {
                    return (a1.getName().compareTo(a2.getName()));
                }
            }
        });
        return arrAssets;
    }
}
