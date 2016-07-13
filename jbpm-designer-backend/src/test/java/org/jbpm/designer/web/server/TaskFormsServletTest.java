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

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.formModeler.designer.integration.BPMNFormBuilderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TaskFormsServletTest  extends RepositoryBaseTest {

    @Mock
    private VFSService vfsServices;

    @Mock
    BPMNFormBuilderService formModelerService;

    @InjectMocks
    TaskFormsServlet taskFormsServlet = new TaskFormsServlet();

    @Before
    public void setup() {
        super.setup();

        when(vfsServices.get(any())).thenAnswer(new Answer<Path>() {
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
    public void testTaskFormServlet() throws Exception {
        when(formModelerService.buildFormXML(any(), any(), any(), any(), any())).thenReturn("dummyform");

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("hello")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("BPMN2-DefaultProcess.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository, "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage", new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(1, forms.size());
        assertEquals("hello-taskform", forms.iterator().next().getName());
        assertEquals("/defaultPackage", forms.iterator().next().getAssetLocation());

        Asset<String> form = repository.loadAsset(forms.iterator().next().getUniqueId());
        assertNotNull(form.getAssetContent());
    }

    @Test
    public void testTaskFormServletWithUserTask() throws Exception {
        when(formModelerService.buildFormXML(any(), any(), any(), any(), any())).thenReturn("dummyform");

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("userTask")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("BPMN2-UserTask.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository, "org/jbpm/designer/public")));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage", new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(2, forms.size());
        List<Asset> arrForms = sortAssets(forms);
        Iterator<Asset> assets = arrForms.iterator();
        Asset asset1 = assets.next();
        assertEquals("evaluate-taskform", asset1.getName());
        assertEquals("/defaultPackage", asset1.getAssetLocation());

        Asset asset2 = assets.next();
        assertEquals("testprocess-taskform", asset2.getName());
        assertEquals("/defaultPackage", asset2.getAssetLocation());

        Asset<String> form1 = repository.loadAsset(asset1.getUniqueId());
        assertNotNull(form1.getAssetContent());
        Asset<String> form2 = repository.loadAsset(asset2.getUniqueId());
        assertNotNull(form2.getAssetContent());
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = TaskFormsServletTest.class.getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }

    private List<Asset> sortAssets(Collection<Asset> assets) {
        ArrayList<Asset> arrAssets = new ArrayList<Asset>(assets);
        arrAssets.sort(new Comparator<Asset>() {
            @Override
            public int compare(Asset a1, Asset a2) {
                if (a1.getName() == null ) {
                    return -1;
                }
                else if (a2.getName() == null ) {
                    return 1;
                }
                else {
                    return (a1.getName().compareTo(a2.getName()));
                }
            }
        });
        return arrAssets;
    }
}
