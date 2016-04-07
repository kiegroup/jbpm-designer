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
package org.jbpm.designer.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jbpm.designer.repository.*;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.server.service.DefaultDesignerAssetService;
import org.jbpm.designer.util.Base64Backport;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith( MockitoJUnitRunner.class )
public class DefaultDesignerAssetServiceTest extends RepositoryBaseTest {

    @Mock
    private IOService ioService;

    @Mock
    MetadataServerSideService metadataService;

    @InjectMocks
    DefaultDesignerAssetService service;

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
    public void testUpdateMetaData() throws Exception {
        VFSRepository repository = new VFSRepository(producer.getIoService());
        repository.setDescriptor(descriptor);
        Directory testProjectDir = repository.createDirectory("/mytestproject");

        Path path = Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId()))));

        final Metadata metadata = new Metadata();

        final HashMap<String, Object> map = new HashMap<String, Object>();
        when(metadataService.setUpAttributes(path,metadata)).thenReturn( map );

        service.updateMetadata( path, metadata );


        verify( ioService ).setAttributes( any( org.uberfire.java.nio.file.Path.class ),
                eq(map));
    }

    private String decodeUniqueId(String uniqueId) {
        if (Base64Backport.isBase64(uniqueId)) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                String uri = new String(decoded, "UTF-8");

                return UriUtils.encode(uri);
            } catch (UnsupportedEncodingException e) {

            }
        }

        return UriUtils.encode(uniqueId);
    }

}
