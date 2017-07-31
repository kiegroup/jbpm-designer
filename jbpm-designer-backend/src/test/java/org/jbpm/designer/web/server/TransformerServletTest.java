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

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class TransformerServletTest extends RepositoryBaseTest {

    private static final String BP_CONTENT = "test process";
    private static final String BPMN2_FILE_TYPE = "bpmn2";
    private static final String BP_NAME = "bp1";
    private static final String JBPM_PROFILE_NAME = "jbpm";
    private static final String LOCATION = "/global";
    private static final String SVG_WIDTH = "197.28";
    private static final String SVG_HEIGHT = "235.92";

    private static final String FORMATTED_SVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:oryx=\"http://oryx-editor.org\" id=\"_A3F7A04F-E2E8-43FD-964F-F700B3D42348\" width=\"197.28125\" height=\"235.921875\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:svg=\"http://www.w3.org/2000/svg\"><defs/><g stroke=\"none\" font-family=\"Verdana, sans-serif\" font-size-adjust=\"none\" font-style=\"normal\" font-variant=\"normal\" font-weight=\"normal\" line-heigth=\"normal\" font-size=\"12\"><g class=\"stencils\" transform=\"translate(15, 15.921875)\"><g class=\"me\"/><g class=\"children\"><g id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8\" bpmn2nodeid=\"processStartEvent\"><g class=\"stencils\" transform=\"translate(120, 165)\"><g class=\"me\"><g pointer-events=\"fill\" id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8\">        <defs id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8__D1450722-93B9-4CD1-9CFB-0FC82DE31DF8_5\">         <radialGradient id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8background\" cx=\"10%\" cy=\"10%\" r=\"100%\" fx=\"10%\" fy=\"10%\">             <stop offset=\"0%\" stop-color=\"#ffffff\" stop-opacity=\"1\" id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8__D1450722-93B9-4CD1-9CFB-0FC82DE31DF8_6\"/>             <stop id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8fill_el\" offset=\"100%\" stop-color=\"#9acd32\" stop-opacity=\"1\"/>         </radialGradient>     </defs>          <circle id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8bg_frame\" cx=\"15\" cy=\"15\" r=\"15\" stroke=\"#000000\" fill=\"url(#_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8background) white\" stroke-width=\"1\" style=\"stroke-dasharray: 5.5, 3\"/>      <circle id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8frame\" cx=\"15\" cy=\"15\" r=\"15\" stroke=\"#000000\" fill=\"none\" stroke-width=\"1\" display=\"inherit\"/>      <text font-size=\"8\" id=\"_D1450722-93B9-4CD1-9CFB-0FC82DE31DF8text_name\" x=\"15\" y=\"32\" oryx:align=\"top center\" stroke=\"black\" stroke-width=\"0pt\" letter-spacing=\"-0.01px\" fill=\"#000000\" text-anchor=\"middle\" transform=\"rotate(0 15 32)\" visibility=\"inherit\" oryx:fontSize=\"11\"/>   </g></g><g class=\"children\" style=\"overflow:hidden\"/><g class=\"edge\"/></g><g class=\"controls\"><g class=\"dockers\"/><g class=\"magnets\" transform=\"translate(120, 165)\"><g pointer-events=\"all\" display=\"none\" transform=\"translate(7, 7)\"><circle cx=\"8\" cy=\"8\" r=\"4\" stroke=\"none\" fill=\"red\" fill-opacity=\"0.3\"/></g></g></g></g></g><g class=\"edge\"/><text id=\"_3260CEE5-4A05-4F4B-91A1-A3CE143D86E0\" style=\"stroke-width:1;fill:rgb(177,194,214);font-family:arial;font-weight:bold\" font-size=\"8\" onclick=\"ORYX.Plugins.CanvasTitle.openTextualAnalysis()\" onmouseover=\"ORYX.Plugins.CanvasTitle.addToolTip('_3260CEE5-4A05-4F4B-91A1-A3CE143D86E0')\" transform=\"translate(10, 20)\">bp1 v.1.0 (Evaluation.bp1)</text></g></g></svg>";
    private static final String TEST_HTML = "<html><head></head><body><div id=\"pagecontainercore\"><p>Test String</p></div></body>";
    private static String FORMATTED_SVG_ENCODED;
    private static String TEST_HTML_ENCODED;

    {
        try {
            FORMATTED_SVG_ENCODED = new String(Base64.encodeBase64(FORMATTED_SVG.getBytes("UTF-8")));
            TEST_HTML_ENCODED = new String(Base64.encodeBase64(TEST_HTML.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
        }
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
    public void testTransformToPng() throws Exception {
        Repository repository = createRepository();
        String id = createAsset(repository,
                                LOCATION,
                                BP_NAME,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        String targetType = "png";
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg",
                   FORMATTED_SVG_ENCODED);
        params.put("uuid",
                   id);
        params.put("profile",
                   JBPM_PROFILE_NAME);
        params.put("transformto",
                   targetType);
        params.put("processid",
                   BP_NAME);

        TestHttpServletResponse response = new TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);

        Collection<Asset> assets = repository.listAssets(LOCATION,
                                                         new FilterByExtension(targetType));
        Asset<String> asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());
    }

    @Test
    public void testTransformToPdf() throws Exception {
        Repository repository = createRepository();
        String id = createAsset(repository,
                                LOCATION,
                                BP_NAME,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        String targetType = "pdf";
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg",
                   FORMATTED_SVG_ENCODED);
        params.put("uuid",
                   id);
        params.put("profile",
                   JBPM_PROFILE_NAME);
        params.put("transformto",
                   targetType);
        params.put("processid",
                   BP_NAME);

        TestHttpServletResponse response = new TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);

        Collection<Asset> assets = repository.listAssets(LOCATION,
                                                         new FilterByExtension(targetType));
        Asset<String> asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());
    }

    @Test
    public void testTransformHtmlToPdf() throws Exception {
        Repository repository = createRepository();
        String id = createAsset(repository,
                                LOCATION,
                                BP_NAME,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        String transformType = "html2pdf";
        Map<String, String> params = new HashMap<String, String>();
        params.put("htmlenc",
                   TEST_HTML_ENCODED);
        params.put("uuid",
                   id);
        params.put("profile",
                   JBPM_PROFILE_NAME);
        params.put("transformto",
                   transformType);
        params.put("processid",
                   BP_NAME);
        params.put("headerstr",
                   "");

        TestHttpServletResponse response = new TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);
    }

    @Test
    public void testSharePng() throws Exception {
        Repository repository = createRepository();
        String id = createAsset(repository,
                                LOCATION,
                                BP_NAME,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        String targetType = "png";
        String respAction = "showurl";
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg",
                   FORMATTED_SVG_ENCODED);
        params.put("uuid",
                   id);
        params.put("profile",
                   JBPM_PROFILE_NAME);
        params.put("transformto",
                   targetType);
        params.put("respaction",
                   respAction);
        params.put("processid",
                   BP_NAME);

        TestHttpServletResponse response = new TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);

        assertTrue(responseText.startsWith("<img"));
        assertTrue(responseText.endsWith("\">"));
        assertTrue(responseText.length() > 1000);
    }

    @Test
    public void testSharePdf() throws Exception {
        Repository repository = createRepository();
        String id = createAsset(repository,
                                LOCATION,
                                BP_NAME,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        // setup parameters
        String targetType = "pdf";
        String respAction = "showurl";
        Map<String, String> params = new HashMap<String, String>();
        params.put("fsvg",
                   FORMATTED_SVG_ENCODED);
        params.put("uuid",
                   id);
        params.put("profile",
                   JBPM_PROFILE_NAME);
        params.put("transformto",
                   targetType);
        params.put("respaction",
                   respAction);
        params.put("processid",
                   BP_NAME);
        params.put("svgwidth",
                   SVG_WIDTH);
        params.put("svgheight",
                   SVG_HEIGHT);
        TestHttpServletResponse response = new TestHttpServletResponse();
        transformerServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertTrue(responseText.startsWith("<object"));
        assertTrue(responseText.contains("style=\"width:197.28px;height:235.92px;\""));
        assertTrue(responseText.endsWith("</object>"));
        assertTrue(responseText.length() > 1000);
    }

    @Test
    public void testStoreInRepository() throws Exception {
        Repository repository = createRepository();

        // Get the logger's listAppender so we can check the log at the end
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        ListAppender listAppender = (ListAppender) root.getAppender("listAppender");

        String bpName = "mytestbp1";
        String id = createAsset(repository,
                                LOCATION,
                                bpName,
                                BPMN2_FILE_TYPE,
                                BP_CONTENT);

        TransformerServlet transformerServlet = new TransformerServlet();
        transformerServlet.setProfile(profile);

        String targetType = "png";
        transformerServlet.storeInRepository(id,
                                             FORMATTED_SVG,
                                             targetType,
                                             bpName,
                                             repository);

        Collection<Asset> assets = repository.listAssets(LOCATION,
                                                         new FilterByExtension(targetType));
        assertEquals(1,
                     assets.size());
        Asset<String> asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());

        transformerServlet.storeInRepository(id,
                                             FORMATTED_SVG,
                                             targetType,
                                             bpName,
                                             repository);

        // Test no FileAlreadyExistsException errors were logged after the 2nd call to storeInRepository
        List logList = listAppender.list;
        assertNotNull(logList);
        Iterator itLogList = logList.iterator();
        while (itLogList.hasNext()) {
            Object oLoggingEvent = itLogList.next();
            if (oLoggingEvent instanceof LoggingEvent) {
                LoggingEvent loggingEvent = (LoggingEvent) oLoggingEvent;
                String message = loggingEvent.getMessage();
                if (message != null) {
                    assertFalse(message.contains("FileAlreadyExistsException") && message.contains(bpName));
                }
            }
        }

        assets = repository.listAssets(LOCATION,
                                       new FilterByExtension(targetType));
        assertEquals(1,
                     assets.size());
        asset = repository.loadAsset(assets.iterator().next().getUniqueId());
        assertNotNull(asset);
        assertNotNull(asset.getAssetContent());
    }

    @Test
    public void testScalePDFImage() throws Exception {
        TransformerServlet transformerServlet = new TransformerServlet();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Document pdfDoc = new Document(PageSize.A4);
        pdfDoc.open();

        PNGTranscoder t = new PNGTranscoder();
        t.addTranscodingHint(ImageTranscoder.KEY_MEDIA,
                             "screen");
        TranscoderInput input = new TranscoderInput(new StringReader(FORMATTED_SVG));
        TranscoderOutput output = new TranscoderOutput(bout);
        t.transcode(input,
                    output);

        Image processImage1 = Image.getInstance(bout.toByteArray());
        transformerServlet.scalePDFImage(pdfDoc,
                                         processImage1);
        pdfDoc.add(processImage1);
        pdfDoc.close();

        assertEquals(197.0,
                     processImage1.getWidth(),
                     0);
        assertEquals(236.0,
                     processImage1.getHeight(),
                     0);
    }
}
