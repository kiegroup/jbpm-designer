/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.*;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.ParsedURL;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.Base64EncodingUtil;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.web.batikprotocolhandler.GuvnorParsedURLProtocolHandler;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;

/** 
 * 
 * Transformer for svg process representation to
 * various formats.
 * 
 * @author Tihomir Surdilovic
 */
public class TransformerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger.getLogger(TransformerServlet.class);
    
    private static final String TO_PDF = "pdf";
    private static final String TO_PNG = "png";
    private static final String JPDL_TO_BPMN2 = "jpdl2bpmn2";
    private static final String BPMN2_TO_JSON = "bpmn2json";
    private static final String RESPACTION_SHOWURL = "showurl";
    private static final String RESPACTION_SHOWEMBEDDABLE = "showembeddable";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        Parameters param = new Parameters(req);
        
        IDiagramProfile profile = ServletUtil.getProfile(req, param.profileName, getServletContext());
        DroolsFactoryImpl.init();

        if( param.transformTo == null ) { 
            return;
        }
        
        if(param.transformTo.equals(TO_PDF)) {
            transformToPDF(param, resp, profile);
        } else if (param.transformTo.equals(TO_PNG)) {
            transformToPNG(param, resp, profile);
        } else if (param.transformTo.equals(JPDL_TO_BPMN2)) { 
            JpdlToBpmn2Util.transformFromJpdlToBpmn2(param, resp, profile);
        }  else if (param.transformTo.equals(BPMN2_TO_JSON)) {
            transformBpmn2ToJson(param, resp, profile);
        } else if(param.transformTo == null && param.action != null && param.action.equals(RESPACTION_SHOWEMBEDDABLE)) {
            showEmbeddable(param, resp, profile);
        }
    }
    
    public static void showEmbeddable(Parameters info, HttpServletResponse resp, IDiagramProfile profile) throws IOException { 
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain");
        String editorURL = GuvnorUtil.getUrl(profile, 
                "/org.drools.guvnor.Guvnor/standaloneEditorServlet?assetsUUIDs="
                + info.uuid
                + "&client=oryx" );
        resp.getWriter().write("<iframe id=\"processFrame\" src=\"" + editorURL + "\" width=\"650\" height=\"580\"/>");
    }
    
    public static void transformToPDF(Parameters param, HttpServletResponse resp, IDiagramProfile profile) throws IOException { 
        try {
            String svgString = null;
            if( param.showUrl ) { 
                svgString = param.formattedSvg;
            } else { 
                svgString = param.rawSvg;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transcodeSvgStringToFormat(svgString, param.transformTo, baos);
            byte [] contentBytes = baos.toByteArray();
            
            if(param.showUrl) { 
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain");
                resp.getWriter().write("<object data=\"data:application/pdf;base64," + Base64EncodingUtil.encode(contentBytes) +  "\" type=\"application/pdf\"></object>");
            } else {
                storeToGuvnor(param.uuid, param.processId, TO_PDF, contentBytes, profile);
                
                resp.setContentType("application/pdf");
                if (param.processId != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + param.processId + ".pdf\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + param.uuid + ".pdf\"");
                }

                resp.getOutputStream().write(contentBytes);
            }
        } catch (TranscoderException e) {
            resp.sendError(500, e.getMessage());
        }
    }
    
    public static void transformToPNG(Parameters param, HttpServletResponse resp, IDiagramProfile profile) throws IOException { 
        try {
            String svgString;
            if( param.showUrl ) { 
                svgString = param.formattedSvg;
            } else {
                svgString = param.rawSvg;
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transcodeSvgStringToFormat(svgString, param.transformTo, baos);
            byte [] contentBytes = baos.toByteArray();

            if(param.showUrl) { 
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain");
                resp.getWriter().write("<img src=\"data:image/png;base64," + Base64EncodingUtil.encode(contentBytes) + "\">");
            } else {
                ParsedURL.registerHandler(new GuvnorParsedURLProtocolHandler(profile));
                storeToGuvnor(param.uuid, param.processId, TO_PNG, contentBytes, profile);
                resp.setContentType("image/png");
                
                if (param.processId != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + param.processId + ".png\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + param.uuid + ".png\"");
                }
            
                resp.getOutputStream().write(contentBytes);
            }
        } catch (TranscoderException e) {
            resp.sendError(500, e.getMessage());
        }
    }
    
    private static void transcodeSvgStringToFormat(String svg, String format, OutputStream outputStream) throws TranscoderException { 
        SVGAbstractTranscoder t = null;
        if( TO_PNG.equals(format) ) { 
            t = new PNGTranscoder();
            t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
        } else if( TO_PDF.equals(format) ) {
            t = new PDFTranscoder();
        } else { 
            throw new RuntimeException("Unknown document format: '" + format + "'");
        }
    
        TranscoderInput input = new TranscoderInput(new StringReader(svg));
        TranscoderOutput output = new TranscoderOutput(outputStream);
        t.transcode(input, output);
    }
    
    public static void transformBpmn2ToJson(Parameters param, HttpServletResponse resp, IDiagramProfile profile) throws IOException { 
        // fix package name if needed
        String[] packageAssetName =  ServletUtil.findPackageAndAssetInfo(param.uuid, profile);
        String packageName = packageAssetName[0];

        Definitions def = ((JbpmProfileImpl) profile).getDefinitions(param.bpmn2ForJson);
        List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
                FeatureMap.Entry toDeleteFeature = null;
                while(iter.hasNext()) {
                    FeatureMap.Entry entry = iter.next();
                    if(entry.getEStructuralFeature().getName().equals("packageName")) {
                        String pname = (String) entry.getValue();
                        if(pname == null || !pname.equals(packageName)) {
                            toDeleteFeature = entry;
                        }
                    }
                }
                if(toDeleteFeature != null) {
                    process.getAnyAttribute().remove(toDeleteFeature);
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                            "http://www.jboss.org/drools", "packageName", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                            packageName);
                    process.getAnyAttribute().add(extensionEntry);
                }
            }
        }
        // get the xml from Definitions
        ResourceSet rSet = new ResourceSetImpl();
        rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
        JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
        rSet.getResources().add(bpmn2resource);
        bpmn2resource.getContents().add(def);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bpmn2resource.save(outputStream, new HashMap<Object, Object>());
        String revisedXmlModel =  outputStream.toString();
        String json = profile.createUnmarshaller().parseModel(revisedXmlModel, profile, param.pp);
        resp.setContentType("application/json");
        resp.getWriter().print(json);
    }
    
    public static void storeToGuvnor(String uuid, String processId, String transformTo, byte [] contentBytes, IDiagramProfile profile) { 
        // GUVNOR TransformerServlet
        if(processId != null) {
            String[] packageAssetName =  ServletUtil.findPackageAndAssetInfo(uuid, profile);
            String packageName = packageAssetName[0];
            try {
                String assetExt = "";
                String assetFileExt = "";
                if (transformTo.equals(TO_PDF)) {
                    assetExt = "-pdf";
                    assetFileExt = ".pdf";
                } else if (transformTo.equals(TO_PNG)) {
                    assetExt = "-image";
                    assetFileExt = ".png";
                }

                String assetURL = GuvnorUtil.getUrl(profile, packageName, processId + assetExt, UrlType.Normal);

                // check if the image already exists
                if (GuvnorUtil.readCheckAssetExists(assetURL, profile)) { 
                    // if the image exists, delete the image
                    GuvnorUtil.deleteAsset(assetURL, profile);
                }

                // create new
                String packageAssetsURL = GuvnorUtil.getUrl(profile, packageName, "", UrlType.Normal);
                GuvnorUtil.createAsset(packageAssetsURL, processId + assetExt, assetFileExt, contentBytes, profile);
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            }
        }
    }

    public static class Parameters { 
        
        public String uuid;
        public String profileName;
        public String transformTo;
        public String action;
        
        public String formattedSvg;
        public String rawSvg;
        
        public String jpdl;
        public String gpd;
        
        public String bpmn2ForJson;
        
        public String pp;
        public String processId;
        
        public boolean showUrl; 
        
        public Parameters(HttpServletRequest req) { 
            uuid = req.getParameter("uuid");
            profileName = req.getParameter("profile");
            transformTo = req.getParameter("transformto");
            
            action = req.getParameter("respaction");
            formattedSvg = req.getParameter("fsvg");
            rawSvg = req.getParameter("rsvg");
            
            jpdl = req.getParameter("jpdl");
            gpd = req.getParameter("gpd");
            
            bpmn2ForJson = req.getParameter("bpmn2");
            
            pp = req.getParameter("pp");
            processId = req.getParameter("processid");
            
            showUrl = (this.action != null && this.action.equals(RESPACTION_SHOWURL));
        }
    }
    
}
