/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/
package com.intalio.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.ParsedURL;
import org.apache.commons.io.IOUtils;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import sun.misc.BASE64Encoder;

import com.intalio.web.batikprotocolhandler.GuvnorParsedURLProtocolHandler;
import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfileService;
import com.intalio.web.profile.impl.ExternalInfo;
import com.intalio.web.profile.impl.ProfileServiceImpl;

/** 
 * 
 * Transformer for svg process representation to
 * various formats.
 * 
 * @author Tihomir Surdilovic
 */
public class TransformerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger
            .getLogger(TransformerServlet.class);
    private static final String TO_PDF = "pdf";
    private static final String TO_PNG = "png";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String formattedSvg = req.getParameter("fsvg");
        String rawSvg = req.getParameter("rsvg");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String transformto = req.getParameter("transformto");

        IDiagramProfile profile = getProfile(req, profileName);

        if (transformto != null && transformto.equals(TO_PDF)) {
            try {
                String processId = storeToGuvnor(uuid, profile, formattedSvg, rawSvg,
                        transformto);
                
                resp.setContentType("application/pdf");
                if (processId != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + processId + ".pdf\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + uuid + ".pdf\"");
                }
                
                PDFTranscoder t = new PDFTranscoder();
                TranscoderInput input = new TranscoderInput(new StringReader(
                        formattedSvg));
                TranscoderOutput output = new TranscoderOutput(
                        resp.getOutputStream());
                t.transcode(input, output);
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
            resp.getOutputStream().flush();
        } else if (transformto != null && transformto.equals(TO_PNG)) {
            try {
                ParsedURL.registerHandler(new GuvnorParsedURLProtocolHandler(profile));
                String processName = storeToGuvnor(uuid, profile, formattedSvg, rawSvg,
                        transformto);

                resp.setContentType("image/png");
                if (processName != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + processName + ".png\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + uuid + ".png\"");
                }
                
                PNGTranscoder t = new PNGTranscoder();
                t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                TranscoderInput input = new TranscoderInput(new StringReader(
                        formattedSvg));
                TranscoderOutput output = new TranscoderOutput(
                        resp.getOutputStream());
                t.transcode(input, output);

            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        }
    }

    private String storeToGuvnor(String uuid, IDiagramProfile profile,
            String formattedSvg, String rawSvg, String transformto) {
        String[] packageAssetName = findPackageAndAssetNameForUUID(uuid,
                profile);
        String processContent = getProcessContent(packageAssetName[0],
                packageAssetName[1], uuid, profile);
        String processId = null;

        if (processContent.length() > 0) {
            Definitions def = getDefinitions(processContent);
            // we need the process id
            for (RootElement rootElement : def.getRootElements()) {
                if (rootElement instanceof Process) {
                    processId = rootElement.getId();
                    if (processId != null && processId.length() > 0) {
                        guvnorStore(packageAssetName[0], processId,
                                profile, formattedSvg, rawSvg, transformto);
                    } else {
                        _logger.error("Cannot store to guvnor because process does not have it's id set");
                    }
                    break;
                }
            }
        }
        return processId;
    }

    private void guvnorStore(String packageName, String assetName,
            IDiagramProfile profile, String formattedSvg, String rawSvg, String transformto) {
        try {
            String assetExt = "";
            String assetFileExt = "";
            if (transformto.equals(TO_PDF)) {
                assetExt = "-pdf";
                assetFileExt = ".pdf";
            }
            if (transformto.equals(TO_PNG)) {
                assetExt = "-image";
                assetFileExt = ".png";
            }

            String pngURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + packageName + "/assets/" + assetName
                    + assetExt;

            String packageAssetsURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + packageName + "/assets/";

            String deleteURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + packageName + "/assets/" + assetName
                    + assetExt;

            // check if the image already exists
            URL checkURL = new URL(pngURL);
            HttpURLConnection checkConnection = (HttpURLConnection) checkURL
                    .openConnection();
            applyAuth(profile, checkConnection);
            checkConnection.setRequestMethod("GET");
            checkConnection
                    .setRequestProperty("Accept", "application/atom+xml");
            checkConnection.connect();
            _logger.info("check connection response code: " + checkConnection.getResponseCode());
            if (checkConnection.getResponseCode() == 200) {
                URL deleteAssetURL = new URL(deleteURL);
                HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
                        .openConnection();
                applyAuth(profile, deleteConnection);
                deleteConnection.setRequestMethod("DELETE");
                deleteConnection.connect();
                _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
            }
            // create new
            URL createURL = new URL(packageAssetsURL);
            HttpURLConnection createConnection = (HttpURLConnection) createURL
                    .openConnection();
            applyAuth(profile, createConnection);
            createConnection.setRequestMethod("POST");
            createConnection.setRequestProperty("Content-Type",
                    "application/octet-stream");
            createConnection.setRequestProperty("Accept",
                    "application/atom+xml");
            createConnection.setRequestProperty("Slug", assetName + assetExt + assetFileExt);
            createConnection.setDoOutput(true);

            if (transformto.equals(TO_PDF)) {
                PDFTranscoder t = new PDFTranscoder();
                TranscoderInput input = new TranscoderInput(new StringReader(
                        rawSvg));
                TranscoderOutput output = new TranscoderOutput(
                        createConnection.getOutputStream());
                t.transcode(input, output);
            }

            if (transformto.equals(TO_PNG)) {
                PNGTranscoder t = new PNGTranscoder();
                t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                TranscoderInput input = new TranscoderInput(new StringReader(
                        rawSvg));
                TranscoderOutput output = new TranscoderOutput(
                        createConnection.getOutputStream());
                t.transcode(input, output);
            }
            createConnection.connect();
            _logger.info("create connection response code: " + createConnection.getResponseCode());
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
    }

    private String getProcessContent(String packageName, String assetName,
            String uuid, IDiagramProfile profile) {
        String assetSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + packageName + "/assets/" + assetName
                + "/source/";

        try {
            InputStream in = getInputStreamForURL(assetSourceURL, "GET",
                    profile);
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            return writer.toString();
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return "";
        }
    }

    private String[] findPackageAndAssetNameForUUID(String uuid,
            IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(getInputStreamForURL(packagesURL,
                            "GET", profile));
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        packages.add(reader.getElementText());
                    }
                }
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }

        boolean gotPackage = false;
        String[] pkgassetnames = new String[2];
        for (String nextPackage : packages) {
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + nextPackage + "/assets/";
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory
                        .createXMLStreamReader(getInputStreamForURL(
                                packageAssetURL, "GET", profile));
                String title = "";
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("title".equals(reader.getLocalName())) {
                            title = reader.getElementText();
                        }
                        if ("uuid".equals(reader.getLocalName())) {
                            String eleText = reader.getElementText();
                            if (uuid.equals(eleText)) {
                                pkgassetnames[0] = nextPackage;
                                pkgassetnames[1] = title;
                                gotPackage = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        return pkgassetnames;
    }

    private InputStream getInputStreamForURL(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection
                .setRequestProperty("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);

        applyAuth(profile, connection);

        connection.connect();

        BufferedReader sreader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        return new ByteArrayInputStream(stringBuilder.toString().getBytes(
                "UTF-8"));
    }

    private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }

    private IDiagramProfile getProfile(HttpServletRequest req,
            String profileName) {
        IDiagramProfile profile = null;

        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(getServletContext());
        profile = service.findProfile(req, profileName);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }

    private Definitions getDefinitions(String xml) {
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet
                    .getResourceFactoryRegistry()
                    .getExtensionToFactoryMap()
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                            new Bpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put(
                    "http://www.omg.org/spec/BPMN/20100524/MODEL",
                    Bpmn2Package.eINSTANCE);
            XMLResource resource = (XMLResource) resourceSet.createResource(URI
                    .createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING,
                    "UTF-8");
            resource.setEncoding("UTF-8");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(XMLResource.OPTION_ENCODING, "UTF-8");
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is, options);
            return ((DocumentRoot) resource.getContents().get(0))
                    .getDefinitions();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
