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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class TaskFormsServletTest extends RepositoryBaseTest {

    @Mock
    private VFSService vfsServices;

    private Repository repository;

    @Before
    public void setup() {
        super.setup();

        when(vfsServices.get(anyString())).thenAnswer(new Answer<Path>() {
            @Override
            public Path answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return PathFactory.newPath((String) args[0], (String) args[0]);
            }
        });

        repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
    }

    @After
    public void teardown() {
        super.teardown();
    }

    protected String dirName = "defaultPackage";
    protected String processFileName = "process";

    @Test
    public void testTaskFormServlet() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("src/test/resources/BPMN2-DefaultProcess.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        TaskFormsServlet taskFormsServlet = new TaskFormsServlet();
        taskFormsServlet.setProfile(profile);
        taskFormsServlet.setVfsServices(vfsServices);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/" + dirName, new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(1, forms.size());
        assertEquals("hello-taskform", forms.iterator().next().getName());
        assertEquals("/" + dirName, forms.iterator().next().getAssetLocation());

        Asset<String> form = repository.loadAsset(forms.iterator().next().getUniqueId());
        assertNotNull(form.getAssetContent());
    }

    @Test
    public void testTaskFormServletWithUserTask() throws Exception {
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name(processFileName)
                .location("/" + dirName);
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("src/test/resources/BPMN2-UserTask.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        TaskFormsServlet taskFormsServlet = new TaskFormsServlet();
        taskFormsServlet.setProfile(profile);
        taskFormsServlet.setVfsServices(vfsServices);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/" + dirName, new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(2, forms.size());
        Iterator<Asset> assets = forms.iterator();
        Asset asset1 = assets.next();
        assertThat(asset1.getName(), anyOf(is("evaluate-taskform"), is("testprocess-taskform")));
        assertEquals("/" + dirName, asset1.getAssetLocation());

        Asset asset2 = assets.next();
        assertThat(asset2.getName(), anyOf(is("evaluate-taskform"), is("testprocess-taskform")));
        assertEquals("/" + dirName, asset2.getAssetLocation());

        Asset<String> form1 = repository.loadAsset(asset1.getUniqueId());
        assertNotNull(form1.getAssetContent());
        Asset<String> form2 = repository.loadAsset(asset2.getUniqueId());
        assertNotNull(form2.getAssetContent());
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
