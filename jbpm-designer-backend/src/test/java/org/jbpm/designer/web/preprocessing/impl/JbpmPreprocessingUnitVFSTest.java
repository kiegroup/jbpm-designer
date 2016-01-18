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
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JbpmPreprocessingUnitVFSTest extends RepositoryBaseTest {


    @Before
    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        producer = new VFSFileSystemProducer();
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("repository.root", VFS_REPOSITORY_ROOT);
        env.put("repository.globaldir", "/global");
        descriptor = producer.produceFileSystem(env);
    }

    @After
    public void teardown() {
        File repo = new File(REPOSITORY_ROOT);
        if(repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
    }
    @Test
    public void testProprocess() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
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
        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit(new TestServletContext(), "/", null);

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
        repository.assetExists("/global/patterns.json");
        repository.assetExists("/global/defaultservicenodeicon.png");

        Collection<Asset> defaultStuff = repository.listAssets("/myprocesses");
        assertNotNull(defaultStuff);
        assertEquals(2, defaultStuff.size());
        repository.assetExists("/myprocesses/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/myprocesses/process.bpmn2");

    }
}
