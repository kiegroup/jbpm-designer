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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class TaskFormsEditorServletTest extends RepositoryBaseTest {

    Logger logger = LoggerFactory.getLogger(TaskFormsEditorServletTest.class);

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testSaveFormAsset() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("action",
                   "save");
        params.put("profile",
                   "jbpm");
        params.put("taskname",
                   Base64.encodeBase64String(UriUtils.encode("evaluate").getBytes()));
        params.put("tfvalue",
                   "this is simple task content");
        params.put("formtype",
                   "ftl");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params),
                                      new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage",
                                                        new FilterByExtension("ftl"));
        assertNotNull(forms);
        // ftl forms are retired. Should not get generated
        assertEquals(0,
                     forms.size());
    }

    @Test
    public void testLoadFormAsset() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());

        AssetBuilder builderForm = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builderForm.content("this is simple task content")
                .type("ftl")
                .name("evaluate-taskform")
                .location("/defaultPackage");
        String uniqueIdForm = repository.createAsset(builderForm.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("action",
                   "load");
        params.put("profile",
                   "jbpm");
        params.put("taskname",
                   Base64.encodeBase64String(UriUtils.encode("evaluate").getBytes()));
        params.put("tfvalue",
                   "this is simple task content");
        params.put("formtype",
                   "ftl");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params),
                                      response);

        String formData = new String(response.getContent());
        logger.debug(formData);
        // "false" is response to UI when no form is generated
        // ftl forms are retired now
        assertEquals("false",
                     formData);
    }

    @Test
    public void testLoadForm_i18nName() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("BPTaskForm_i18nNames")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());

        // setup parameters
        String taskName = "проверить";
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("action",
                   "load");
        params.put("profile",
                   "jbpm");
        params.put("taskname",
                   Base64.encodeBase64String(UriUtils.encode(taskName).getBytes()));
        params.put("tfvalue",
                   "this is simple task content");
        params.put("formtype",
                   "ftl");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params),
                                      response);

        Collection<Asset> forms = repository.listAssets("/defaultPackage",
                                                        new FilterByExtension("ftl"));
        assertNotNull(forms);
        // ftl forms are retired. Should not get generated
        assertEquals(0,
                     forms.size());
    }

    @Test
    public void testSaveNotExistingTaskForm() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());

        // setup parameters
        String taskName = "mytask";
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid",
                   uniqueId);
        params.put("action",
                   "save");
        params.put("profile",
                   "jbpm");
        params.put("taskname",
                   Base64.encodeBase64String(UriUtils.encode(taskName).getBytes()));
        params.put("tfvalue",
                   "this is simple task content");
        params.put("formtype",
                   "form");
        params.put("json",
                   "{\"resourceId\":\"Definition\",\"properties\":{\"namespaces\":\"\",\"name\":\"\",\"expressionlanguage\":\"http://www.mvel.org/2.0\",\"executable\":\"true\",\"package\":\"\",\"vardefs\":\"\",\"customdescription\":\"\",\"customcaseidprefix\":\"\",\"customcaseroles\":\"\",\"adhocprocess\":\"false\",\"imports\":\"\",\"globals\":\"\",\"id\":\"evaluation.abc\",\"version\":\"1.0\",\"timeunit\":\"min\",\"currency\":\"\",\"targetnamespace\":\"http://www.omg.org/bpmn20\",\"typelanguage\":\"http://www.java.com/javaTypes\",\"processn\":\"abc\",\"documentation\":\"\"},\"stencil\":{\"id\":\"BPMNDiagram\"},\"childShapes\":[{\"resourceId\":\"_17704A2A-ABF7-4F52-A5C5-0A6C638B73B3\",\"properties\":{\"name\":\"myTask\",\"documentation\":\"\",\"isselectable\":\"true\",\"invisid\":\"\",\"isforcompensation\":\"\",\"assignments\":\"\",\"assignmentsview\":\"0 data inputs, 0 data outputs\",\"tasktype\":\"User\",\"messageref\":\"\",\"script\":\"\",\"script_language\":\"java\",\"bgcolor\":\"#fafad2\",\"bordercolor\":\"#000000\",\"fontcolor\":\"#000000\",\"fontsize\":\"\",\"datainputset\":\"\",\"dataoutputset\":\"\",\"origbgcolor\":\"#fafad2\",\"nomorph\":\"true\",\"origbordercolor\":\"#000000\",\"ruleflowgroup\":\"\",\"rulelanguage\":\"DRL\",\"onentryactions\":\"\",\"onexitactions\":\"\",\"isasync\":\"false\",\"customautostart\":\"false\",\"taskname\":\"myTask\",\"serviceoperation\":\"\",\"serviceinterface\":\"\",\"serviceimplementation\":\"Java\",\"actors\":\"\",\"groupid\":\"\",\"subject\":\"\",\"description\":\"\",\"content\":\"\",\"reassignment\":\"\",\"notifications\":\"\",\"locale\":\"\",\"createdby\":\"\",\"skippable\":\"true\",\"priority\":\"\",\"multipleinstance\":\"false\",\"multipleinstancecollectioninput\":\"\",\"multipleinstancecollectionoutput\":\"\",\"multipleinstancedatainput\":\"\",\"multipleinstancedataoutput\":\"\",\"multipleinstancecompletioncondition\":\"\",\"min\":\"5\",\"max\":\"10\",\"standarddeviation\":\"1\",\"mean\":\"0\",\"distributiontype\":\"uniform\",\"quantity\":\"1\",\"workinghours\":\"8\",\"unitcost\":\"0\"},\"stencil\":{\"id\":\"Task\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":178,\"y\":210},\"upperLeft\":{\"x\":78,\"y\":130}},\"dockers\":[]}],\"bounds\":{\"lowerRight\":{\"x\":3000,\"y\":2000},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"/org.kie.workbench.KIEWebapp/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"ssextensions\":[]}");
        params.put("ppdata",
                   "");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params),
                                      response);

        Collection<Asset> ftlForms = repository.listAssets("/defaultPackage",
                                                           new FilterByExtension("ftl"));
        assertNotNull(ftlForms);
        // ftl forms are retired. Should not get generated
        assertEquals(0,
                     ftlForms.size());

        Collection<Asset> formForms = repository.listAssets("/defaultPackage",
                                                            new FilterByExtension("form"));
        assertNotNull(formForms);
        assertEquals(1,
                     formForms.size());

        params.remove("formtype");
        params.put("formtype",
                   "frm");

        taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));
        response = new TestHttpServletResponse();
        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params),
                                      response);

        Collection<Asset> ftlForms2 = repository.listAssets("/defaultPackage",
                                                            new FilterByExtension("ftl"));
        assertNotNull(ftlForms2);
        // ftl forms are retired. Should not get generated
        assertEquals(0,
                     ftlForms2.size());

        Collection<Asset> frmForms = repository.listAssets("/defaultPackage",
                                                           new FilterByExtension("frm"));
        assertNotNull(frmForms);
        assertEquals(1,
                     frmForms.size());
    }
}
