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
package org.jbpm.designer.server.service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Directory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDesignerAssetServiceTest extends RepositoryBaseTest {

    @Mock
    private IOService ioService;

    @Mock
    MetadataServerSideService metadataService;

    @InjectMocks
    DefaultDesignerAssetService service;

    @Mock
    private Repository repository;

    private XPath xpath = XPathFactory.newInstance().newXPath();

    @BeforeClass
    public static void setupOnce() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
    }

    @AfterClass
    public static void cleanOnce() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
    }

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testUpdateMetaData() throws Exception {
        VFSRepository repository = new VFSRepository(producer.getIoService());
        repository.setDescriptor(descriptor);
        Directory testProjectDir = repository.createDirectory("/mytestproject");

        Path path = Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId()))));

        final Metadata metadata = new Metadata();

        final HashMap<String, Object> map = new HashMap<String, Object>();
        when(metadataService.setUpAttributes(path,
                                             metadata)).thenReturn(map);

        service.updateMetadata(path,
                               metadata);

        verify(ioService).setAttributes(any(org.uberfire.java.nio.file.Path.class),
                                        eq(map));
    }

    @Test
    public void testCreateProcessWithDefaultPackage() throws Exception {
        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyProcess.bpmn2");
        when(pathSource.getFileName()).thenReturn("MyProcess.bpmn2");

        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);

        assetService.createProcess(pathSource,
                                   "MyProcess.bpmn2");

        verify(repository,
               times(1)).createAsset(assetArgumentCaptor.capture());

        Asset<String> asset = assetArgumentCaptor.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        Element element = getProcessElementFromXml(asset.getAssetContent());

        assertNotNull(element);
        String processId = element.getAttribute("id");
        assertNotNull(processId);
        assertEquals("Evaluation.MyProcess",
                     processId);

        String packageName = element.getAttribute("drools:packageName");
        assertNotNull(packageName);
        assertEquals("",
                     packageName);
    }

    @Test
    public void testCreateProcessWithSingleLevelPackage() throws Exception {
        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/org/MyProcess.bpmn2");
        when(pathSource.getFileName()).thenReturn("MyProcess.bpmn2");

        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);

        assetService.createProcess(pathSource,
                                   "MyProcess.bpmn2");

        verify(repository,
               times(1)).createAsset(assetArgumentCaptor.capture());

        Asset<String> asset = assetArgumentCaptor.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        Element element = getProcessElementFromXml(asset.getAssetContent());

        assertNotNull(element);
        String processId = element.getAttribute("id");
        assertNotNull(processId);
        assertEquals("Evaluation.MyProcess",
                     processId);

        String packageName = element.getAttribute("drools:packageName");
        assertNotNull(packageName);
        assertEquals("org",
                     packageName);
    }

    @Test
    public void testCreateProcessWithMultiLevelPackage() throws Exception {
        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/org/jbpm/test/process/MyProcess.bpmn2");
        when(pathSource.getFileName()).thenReturn("MyProcess.bpmn2");

        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);

        assetService.createProcess(pathSource,
                                   "MyProcess.bpmn2");

        verify(repository,
               times(1)).createAsset(assetArgumentCaptor.capture());

        Asset<String> asset = assetArgumentCaptor.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        Element element = getProcessElementFromXml(asset.getAssetContent());

        assertNotNull(element);
        String processId = element.getAttribute("id");
        assertNotNull(processId);
        assertEquals("Evaluation.MyProcess",
                     processId);

        String packageName = element.getAttribute("drools:packageName");
        assertNotNull(packageName);
        assertEquals("org.jbpm.test.process",
                     packageName);
    }

    @Test
    public void testCreateCaseDefinitionWithDefaultPackage() throws Exception {
        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyCase.bpmn2");
        when(pathSource.getFileName()).thenReturn("MyCase.bpmn2");

        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);

        assetService.createCaseDefinition(pathSource,
                                          "MyCase.bpmn2",
                                          "HR");

        verify(repository,
               times(1)).createAsset(assetArgumentCaptor.capture());

        Asset<String> asset = assetArgumentCaptor.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        Element element = getProcessElementFromXml(asset.getAssetContent());

        assertNotNull(element);
        String processId = element.getAttribute("id");
        assertNotNull(processId);
        assertEquals("Evaluation.MyCase",
                     processId);

        String packageName = element.getAttribute("drools:packageName");
        assertNotNull(packageName);
        assertEquals("",
                     packageName);

        String adHoc = element.getAttribute("drools:adHoc");
        assertNotNull(adHoc);
        assertEquals("true",
                     adHoc);

        Element metadataElement = getMetaDataElementFromXml(asset.getAssetContent());
        String caseIdPrefix = metadataElement.getAttribute("name");
        assertNotNull(caseIdPrefix);
        assertEquals("customCaseIdPrefix",
                     caseIdPrefix);

        String caseIdPrefixValue = metadataElement.getFirstChild().getNextSibling().getTextContent();
        assertNotNull(caseIdPrefixValue);
        assertEquals("HR",
                     caseIdPrefixValue);
    }

    @Test
    public void testCreateCaseDefinitionWithPackageNoPrefix() throws Exception {
        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/org/jbpm/test/cases/MyCase.bpmn2");
        when(pathSource.getFileName()).thenReturn("MyCase.bpmn2");

        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);

        assetService.createCaseDefinition(pathSource,
                                          "MyCase.bpmn2",
                                          "");

        verify(repository,
               times(1)).createAsset(assetArgumentCaptor.capture());

        Asset<String> asset = assetArgumentCaptor.getValue();
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        Element element = getProcessElementFromXml(asset.getAssetContent());

        assertNotNull(element);
        String processId = element.getAttribute("id");
        assertNotNull(processId);
        assertEquals("Evaluation.MyCase",
                     processId);

        String packageName = element.getAttribute("drools:packageName");
        assertNotNull(packageName);
        assertEquals("org.jbpm.test.cases",
                     packageName);

        String adHoc = element.getAttribute("drools:adHoc");
        assertNotNull(adHoc);
        assertEquals("true",
                     adHoc);

        Element metadataElement = getMetaDataElementFromXml(asset.getAssetContent());
        String caseIdPrefix = metadataElement.getAttribute("name");
        assertNotNull(caseIdPrefix);
        assertEquals("customCaseIdPrefix",
                     caseIdPrefix);

        String caseIdPrefixValue = metadataElement.getFirstChild().getNextSibling().getTextContent();
        assertNotNull(caseIdPrefixValue);
        assertEquals("CASE",
                     caseIdPrefixValue);
    }

    @Test
    public void testIsCaseProject() throws Exception {
        DefaultDesignerAssetService assetService = new DefaultDesignerAssetService();
        assetService.setRepository(repository);
        assetService.setIoService(ioService);

        final Path packagePath = mock(Path.class);
        when(packagePath.toURI()).thenReturn("default://p0/Evaluation/");

        final Path pathSource = mock(Path.class);
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/.caseproject");
        when(pathSource.getFileName()).thenReturn(".caseproject");
        assetService.createProcess(pathSource,
                                   ".caseproject");

        DirectoryStream directoryStream = Mockito.mock(DirectoryStream.class);
        when(ioService.newDirectoryStream(any(),
                                          any())).thenReturn((DirectoryStream<org.uberfire.java.nio.file.Path>) directoryStream);
        when(directoryStream.iterator()).thenReturn(Arrays.asList(packagePath).iterator());

        assertTrue(assetService.isCaseProject(packagePath));
    }

    private Element getProcessElementFromXml(String content) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));

        Document xml = builder.parse(input);
        XPathExpression expr = xpath.compile("/definitions/process");
        Element element = (Element) expr.evaluate(xml,
                                                  XPathConstants.NODE);

        return element;
    }

    private Element getMetaDataElementFromXml(String content) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"));

        Document xml = builder.parse(input);
        XPathExpression expr = xpath.compile("/definitions/process/extensionElements/metaData");
        Element element = (Element) expr.evaluate(xml,
                                                  XPathConstants.NODE);

        return element;
    }

    private String decodeUniqueId(String uniqueId) {
        if (Base64.isArrayByteBase64(uniqueId.getBytes())) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                String uri = new String(decoded,
                                        "UTF-8");

                return UriUtils.encode(uri);
            } catch (UnsupportedEncodingException e) {

            }
        }

        return UriUtils.encode(uniqueId);
    }
}
