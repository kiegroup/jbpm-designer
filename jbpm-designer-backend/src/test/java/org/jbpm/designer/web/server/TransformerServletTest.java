/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransformerServletTest  extends RepositoryBaseTest {

    private static final String formattedSVGEncoded = "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOm9yeXg9Imh0dHA6Ly9vcnl4LWVkaXRvci5vcmciIGlkPSJfNURCNkVCREYtNzBDNy00RTJFLTk0MTAtNUFCNTZDMDI4NDYwIiB3aWR0aD0iMTk3LjI4MTI1IiBoZWlnaHQ9IjIzNS45MjE4NzUiIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB4bWxuczpzdmc9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcy8+PGcgc3Ryb2tlPSJub25lIiBmb250LWZhbWlseT0iVmVyZGFuYSwgc2Fucy1zZXJpZiIgZm9udC1zaXplLWFkanVzdD0ibm9uZSIgZm9udC1zdHlsZT0ibm9ybWFsIiBmb250LXZhcmlhbnQ9Im5vcm1hbCIgZm9udC13ZWlnaHQ9Im5vcm1hbCIgbGluZS1oZWlndGg9Im5vcm1hbCIgZm9udC1zaXplPSIxMiI+PGcgY2xhc3M9InN0ZW5jaWxzIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxNSwgMTUuOTIxODc1KSI+PGcgY2xhc3M9Im1lIi8+PGcgY2xhc3M9ImNoaWxkcmVuIj48ZyBpZD0iXzI0OUIzMzgwLTQzMzAtNDQwRi1CRTRGLTc5NzcxRjRDQURDMSIgYnBtbjJub2RlaWQ9InByb2Nlc3NTdGFydEV2ZW50Ij48ZyBjbGFzcz0ic3RlbmNpbHMiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDEyMCwgMTY1KSI+PGcgY2xhc3M9Im1lIj48ZyBwb2ludGVyLWV2ZW50cz0iZmlsbCIgaWQ9Il8yNDlCMzM4MC00MzMwLTQ0MEYtQkU0Ri03OTc3MUY0Q0FEQzEiPiAgICAgICAgPGRlZnMgaWQ9Il8yNDlCMzM4MC00MzMwLTQ0MEYtQkU0Ri03OTc3MUY0Q0FEQzFfXzI0OUIzMzgwLTQzMzAtNDQwRi1CRTRGLTc5NzcxRjRDQURDMV81Ij4gCQk8cmFkaWFsR3JhZGllbnQgaWQ9Il8yNDlCMzM4MC00MzMwLTQ0MEYtQkU0Ri03OTc3MUY0Q0FEQzFiYWNrZ3JvdW5kIiBjeD0iMTAlIiBjeT0iMTAlIiByPSIxMDAlIiBmeD0iMTAlIiBmeT0iMTAlIj4gCQkJPHN0b3Agb2Zmc2V0PSIwJSIgc3RvcC1jb2xvcj0iI2ZmZmZmZiIgc3RvcC1vcGFjaXR5PSIxIiBpZD0iXzI0OUIzMzgwLTQzMzAtNDQwRi1CRTRGLTc5NzcxRjRDQURDMV9fMjQ5QjMzODAtNDMzMC00NDBGLUJFNEYtNzk3NzFGNENBREMxXzYiLz4gCQkJPHN0b3AgaWQ9Il8yNDlCMzM4MC00MzMwLTQ0MEYtQkU0Ri03OTc3MUY0Q0FEQzFmaWxsX2VsIiBvZmZzZXQ9IjEwMCUiIHN0b3AtY29sb3I9IiM5YWNkMzIiIHN0b3Atb3BhY2l0eT0iMSIvPiAJCTwvcmFkaWFsR3JhZGllbnQ+IAk8L2RlZnM+IAkgICAgIDxjaXJjbGUgaWQ9Il8yNDlCMzM4MC00MzMwLTQ0MEYtQkU0Ri03OTc3MUY0Q0FEQzFiZ19mcmFtZSIgY3g9IjE1IiBjeT0iMTUiIHI9IjE1IiBzdHJva2U9IiMwMDAwMDAiIGZpbGw9InVybCgjXzI0OUIzMzgwLTQzMzAtNDQwRi1CRTRGLTc5NzcxRjRDQURDMWJhY2tncm91bmQpIHdoaXRlIiBzdHJva2Utd2lkdGg9IjEiIHN0eWxlPSJzdHJva2UtZGFzaGFycmF5OiA1LjUsIDMiLz4gICAgICA8Y2lyY2xlIGlkPSJfMjQ5QjMzODAtNDMzMC00NDBGLUJFNEYtNzk3NzFGNENBREMxZnJhbWUiIGN4PSIxNSIgY3k9IjE1IiByPSIxNSIgc3Ryb2tlPSIjMDAwMDAwIiBmaWxsPSJub25lIiBzdHJva2Utd2lkdGg9IjEiIGRpc3BsYXk9ImluaGVyaXQiLz4gIAk8dGV4dCBmb250LXNpemU9IjgiIGlkPSJfMjQ5QjMzODAtNDMzMC00NDBGLUJFNEYtNzk3NzFGNENBREMxdGV4dF9uYW1lIiB4PSIxNSIgeT0iMzIiIG9yeXg6YWxpZ249InRvcCBjZW50ZXIiIHN0cm9rZT0iYmxhY2siIHN0cm9rZS13aWR0aD0iMHB0IiBsZXR0ZXItc3BhY2luZz0iLTAuMDFweCIgZmlsbD0iIzAwMDAwMCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgdHJhbnNmb3JtPSJyb3RhdGUoMCAxNSAzMikiIHZpc2liaWxpdHk9ImluaGVyaXQiIG9yeXg6Zm9udFNpemU9IjExIi8+ICAgPC9nPjwvZz48ZyBjbGFzcz0iY2hpbGRyZW4iIHN0eWxlPSJvdmVyZmxvdzpoaWRkZW4iLz48ZyBjbGFzcz0iZWRnZSIvPjwvZz48ZyBjbGFzcz0iY29udHJvbHMiPjxnIGNsYXNzPSJkb2NrZXJzIi8+PGcgY2xhc3M9Im1hZ25ldHMiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDEyMCwgMTY1KSI+PGcgcG9pbnRlci1ldmVudHM9ImFsbCIgZGlzcGxheT0ibm9uZSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNywgNykiPjxjaXJjbGUgY3g9IjgiIGN5PSI4IiByPSI0IiBzdHJva2U9Im5vbmUiIGZpbGw9InJlZCIgZmlsbC1vcGFjaXR5PSIwLjMiLz48L2c+PC9nPjwvZz48L2c+PC9nPjxnIGNsYXNzPSJlZGdlIi8+PHRleHQgaWQ9Il8zRkFDMEJCQy00OTI4LTRBQUEtQTMxNC05NDFERTZDNERDNjgiIHN0eWxlPSJzdHJva2Utd2lkdGg6MTtmaWxsOnJnYigxNzcsMTk0LDIxNCk7Zm9udC1mYW1pbHk6YXJpYWw7Zm9udC13ZWlnaHQ6Ym9sZCIgZm9udC1zaXplPSI4IiBvbmNsaWNrPSJPUllYLlBsdWdpbnMuQ2FudmFzVGl0bGUub3BlblRleHR1YWxBbmFseXNpcygpIiBvbm1vdXNlb3Zlcj0iT1JZWC5QbHVnaW5zLkNhbnZhc1RpdGxlLmFkZFRvb2xUaXAoJ18zRkFDMEJCQy00OTI4LTRBQUEtQTMxNC05NDFERTZDNERDNjgnKSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMTAsIDIwKSI+YnAxIHYuMS4wIChFdmFsdWF0aW9uLmJwMSk8L3RleHQ+PC9nPjwvZz48L3N2Zz4=";


    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testTransformToPng() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("test process")
                .type("bpmn2")
                .name("bp1")
                .location("/global");
        String id = repository.createAsset(builder.getAsset());

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg", formattedSVGEncoded);
                params.put("uuid", id);
        params.put("profile", "jbpm");
        params.put("transformto", "png");
        params.put("processid", "bp1");

        TestHttpServletResponse response = new  TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params), response);

        int responseStatus = response.getStatus();
        assertEquals(0, responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);

        Collection<Asset> assets = repository.listAssets("/global", new FilterByExtension("png"));
        Asset<String> asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());
    }

    @Test
    public void testTransformToPdf() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("test process")
                .type("bpmn2")
                .name("bp1")
                .location("/global");
        String id = repository.createAsset(builder.getAsset());

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg", formattedSVGEncoded);
        params.put("uuid", id);
        params.put("profile", "jbpm");
        params.put("transformto", "pdf");
        params.put("processid", "bp1");

        TestHttpServletResponse response = new  TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params), response);

        int responseStatus = response.getStatus();
        assertEquals(0, responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);

        Collection<Asset> assets = repository.listAssets("/global", new FilterByExtension("pdf"));
        Asset<String> asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());
    }


}
