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

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Note this test relies on external service repository: http://people.redhat.com/tsurdilo/repository/
 * so it will fail when service repository is not accessible
 */
public class JbpmServiceRepositoryServletTest  extends RepositoryBaseTest {

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    //@Test
    // jenkins seems to no longer can connect to people.redhat.com
    public void testJbpmServiceRepositoryServlet() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository)repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("repourl", "http://people.redhat.com/tsurdilo/repository/");
        params.put("asset", "Rewardsystem");
        params.put("profile", "jbpm");
        params.put("category", "Rewards");
        params.put("action", "install");

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);

        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));

        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> serviceAssets = repository.listAssets("/global");
        assertNotNull(serviceAssets);
        assertEquals(2, serviceAssets.size());

        Asset<String> form = repository.loadAsset(serviceAssets.iterator().next().getUniqueId());
        assertNotNull(form.getAssetContent());
    }
}
