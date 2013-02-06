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

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.*;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;
import org.jbpm.migration.JbpmMigration;
import sun.misc.BASE64Encoder;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private static final String TO_SVG = "svg";
    private static final String JPDL_TO_BPMN2 = "jpdl2bpmn2";
    private static final String BPMN2_TO_JSON = "bpmn2json";
    private static final String RESPACTION_SHOWURL = "showurl";
    private static final String RESPACTION_SHOWEMBEDDABLE = "showembeddable";

    @Inject
    private IDiagramProfileService _profileService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String formattedSvg = req.getParameter("fsvg");
        String rawSvg = req.getParameter("rsvg");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String transformto = req.getParameter("transformto");
        String jpdl = req.getParameter("jpdl");
        String gpd = req.getParameter("gpd");
        String bpmn2in = req.getParameter("bpmn2");
        String respaction = req.getParameter("respaction");
        String pp = req.getParameter("pp");
        String processid = req.getParameter("processid");

        IDiagramProfile profile = _profileService.findProfile(req, profileName);
        DroolsFactoryImpl.init();

        Repository repository = profile.getRepository();

        if (transformto != null && transformto.equals(TO_PDF)) {
            try {
                if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    PDFTranscoder t = new PDFTranscoder();
                    TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("text/plain");
                    resp.getWriter().write("<object data=\"data:application/pdf;base64," + Base64.encodeBase64(bout.toByteArray()) +  "\" type=\"application/pdf\"></object>");
                } else {
                    storeInRepository(uuid, rawSvg, transformto, processid, repository);

                    resp.setContentType("application/pdf");
                    if (processid != null) {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + processid + ".pdf\"");
                    } else {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + uuid + ".pdf\"");
                    }

                    PDFTranscoder t = new PDFTranscoder();
                    TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(resp.getOutputStream());
                    t.transcode(input, output);
                }
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        } else if (transformto != null && transformto.equals(TO_PNG)) {
            try {
                if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("text/plain");
                    BASE64Encoder enc = new BASE64Encoder();
                    resp.getWriter().write("<img src=\"data:image/png;base64," + enc.encode(bout.toByteArray()) + "\">");
                } else {
                    storeInRepository(uuid, rawSvg, transformto, processid, repository);
                    resp.setContentType("image/png");
                    if (processid != null) {
                        resp.setHeader("Content-Disposition", "attachment; filename=\"" + processid + ".png\"");
                    } else {
                        resp.setHeader("Content-Disposition", "attachment; filename=\"" + uuid + ".png\"");
                    }

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(
                            resp.getOutputStream());
                    t.transcode(input, output);
                }
            } catch (TranscoderException e) {
                resp.sendError(500, e.getMessage());
            }
        } else if (transformto != null && transformto.equals(TO_SVG)) {
            storeInRepository(uuid, rawSvg, transformto, processid, repository);
        } else if (transformto != null && transformto.equals(JPDL_TO_BPMN2)) {
            String bpmn2 = JbpmMigration.transform(jpdl);
            Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2);
            // add bpmndi info to Definitions with help of gpd
            addBpmnDiInfo(def, gpd);
            // hack for now
            revisitSequenceFlows(def, bpmn2);
            // another hack if id == name
            revisitNodeNames(def);

            // get the xml from Definitions
            ResourceSet rSet = new ResourceSetImpl();
            rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
            JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
            rSet.getResources().add(bpmn2resource);
            bpmn2resource.getContents().add(def);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bpmn2resource.save(outputStream, new HashMap<Object, Object>());
            String fullXmlModel =  outputStream.toString();
            // convert to json and write response
            String json = profile.createUnmarshaller().parseModel(fullXmlModel, profile, pp);
            resp.setContentType("application/json");
            resp.getWriter().print(json);
        }  else if (transformto != null && transformto.equals(BPMN2_TO_JSON)) {
            // fix package name if needed
            String packageName = null;
            try {
                Asset<String> processAsset = repository.loadAsset(uuid);

                // build package name based on the asset location
                packageName = processAsset.getAssetLocation().replaceAll(File.separator, ".");
            } catch (AssetNotFoundException e) {
                _logger.error("Process with uuid " + uuid + " was not found");
            }
            Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2in);
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
            String json = profile.createUnmarshaller().parseModel(revisedXmlModel, profile, pp);
            resp.setContentType("application/json");
            resp.getWriter().print(json);
        } else if(transformto == null && respaction != null && respaction.equals(RESPACTION_SHOWEMBEDDABLE)) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/plain");

            // TODO what is this???

            String editorURL = RepositoryInfo.getRepositoryProtocol(profile)
                    + "://"
                    + RepositoryInfo.getRepositoryHost(profile)
                    + "/"
                    + RepositoryInfo.getRepositorySubdomain(profile).substring(0,
                    RepositoryInfo.getRepositorySubdomain(profile).indexOf("/"))
                    + "/org.drools.guvnor.Guvnor/standaloneEditorServlet?assetsUUIDs="
                    + uuid
                    + "&client=oryx";
            resp.getWriter().write("<iframe id=\"processFrame\" src=\"" + editorURL + "\" width=\"650\" height=\"580\"/>");
        }
    }

    private void revisitNodeNames(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe.getName() != null && fe.getId().equals(fe.getName())) {
                        // change the name so they are not the same
                        fe.setName("_" + fe.getName());
                    }
                }
            }
        }
    }

    private void revisitSequenceFlows(Definitions def, String orig) {
        try {
            Map<String, Map<String, String>> sequenceFlowMapping = new HashMap<String, Map<String,String>>();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(orig));
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("sequenceFlow".equals(reader.getLocalName())) {
                        String id = "";
                        String source = "";
                        String target = "";
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("id".equals(reader.getAttributeLocalName(i))) {
                                id = reader.getAttributeValue(i);
                            }
                            if ("sourceRef".equals(reader.getAttributeLocalName(i))) {
                                source = reader.getAttributeValue(i);
                            }
                            if ("targetRef".equals(reader.getAttributeLocalName(i))) {
                                target = reader.getAttributeValue(i);
                            }
                        }
                        Map<String, String> valueMap = new HashMap<String, String>();
                        valueMap.put("sourceRef", source);
                        valueMap.put("targetRef", target);
                        sequenceFlowMapping.put(id, valueMap);
                    }
                }
            }
            List<RootElement> rootElements =  def.getRootElements();
            for(RootElement root : rootElements) {
                if(root instanceof Process) {
                    Process process = (Process) root;
                    List<FlowElement> flowElements = process.getFlowElements();
                    for(FlowElement fe : flowElements) {
                        if(fe instanceof SequenceFlow) {
                            SequenceFlow sf = (SequenceFlow) fe;
                            if(sequenceFlowMapping.containsKey(sf.getId())) {
                                sf.setSourceRef(getFlowNode(def, sequenceFlowMapping.get(sf.getId()).get("sourceRef")));
                                sf.setTargetRef(getFlowNode(def, sequenceFlowMapping.get(sf.getId()).get("targetRef")));
                            } else {
                                _logger.error("Could not find mapping for sequenceFlow: " + sf.getId());
                            }
                        }
                    }
                }
            }
        } catch (FactoryConfigurationError e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private FlowNode getFlowNode(Definitions def, String nodeId) {
        List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe instanceof FlowNode) {
                        if(fe.getId().equals(nodeId)) {
                            return (FlowNode) fe;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void addBpmnDiInfo(Definitions def, String gpd) {
        try {
            Map<String, Bounds> _bounds = new HashMap<String, Bounds>();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(gpd));
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("node".equals(reader.getLocalName())) {
                        Bounds b = DcFactory.eINSTANCE.createBounds();
                        String nodeName = null;
                        String nodeX = null;
                        String nodeY = null;
                        String nodeWidth = null;
                        String nodeHeight = null;
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("name".equals(reader.getAttributeLocalName(i))) {
                                nodeName = reader.getAttributeValue(i);
                            } else if("x".equals(reader.getAttributeLocalName(i))) {
                                nodeX = reader.getAttributeValue(i);
                            } else if("y".equals(reader.getAttributeLocalName(i))) {
                                nodeY = reader.getAttributeValue(i);
                            } else if("width".equals(reader.getAttributeLocalName(i))) {
                                nodeWidth = reader.getAttributeValue(i);
                            } else if("height".equals(reader.getAttributeLocalName(i))) {
                                nodeHeight = reader.getAttributeValue(i);
                            }
                        }
                        b.setX(new Float(nodeX).floatValue());
                        b.setY(new Float(nodeY).floatValue());
                        b.setWidth(new Float(nodeWidth).floatValue());
                        b.setHeight(new Float(nodeHeight).floatValue());
                        _bounds.put(nodeName, b);
                    }
                }
            }

            for (RootElement rootElement: def.getRootElements()) {
                if (rootElement instanceof Process) {
                    Process process = (Process) rootElement;
                    BpmnDiFactory diFactory = BpmnDiFactory.eINSTANCE;
                    BPMNDiagram diagram = diFactory.createBPMNDiagram();
                    BPMNPlane plane = diFactory.createBPMNPlane();
                    plane.setBpmnElement(process);
                    diagram.setPlane(plane);
                    for (FlowElement flowElement: process.getFlowElements()) {
                        if (flowElement instanceof FlowNode) {
                            Bounds b = _bounds.get(flowElement.getId());
                            if (b != null) {
                                BPMNShape shape = diFactory.createBPMNShape();
                                shape.setBpmnElement(flowElement);
                                shape.setBounds(b);
                                plane.getPlaneElement().add(shape);
                            }
                        } else if (flowElement instanceof SequenceFlow) {
                            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                            BPMNEdge edge = diFactory.createBPMNEdge();
                            edge.setBpmnElement(flowElement);
                            DcFactory dcFactory = DcFactory.eINSTANCE;
                            Point point = dcFactory.createPoint();
                            if(sequenceFlow.getSourceRef() != null) {
                                Bounds sourceBounds = _bounds.get(sequenceFlow.getSourceRef().getId());
                                point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
                                point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
                            }
                            edge.getWaypoint().add(point);
//	    					List<Point> dockers = _dockers.get(sequenceFlow.getId());
//	    					for (int i = 1; i < dockers.size() - 1; i++) {
//	    						edge.getWaypoint().add(dockers.get(i));
//	    					}
                            point = dcFactory.createPoint();
                            if(sequenceFlow.getTargetRef() != null) {
                                Bounds targetBounds = _bounds.get(sequenceFlow.getTargetRef().getId());
                                point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
                                point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
                            }
                            edge.getWaypoint().add(point);
                            plane.getPlaneElement().add(edge);
                        }
                    }

                    def.getDiagrams().add(diagram);
                }
            }
        } catch (FactoryConfigurationError e) {
            _logger.error("Exception adding bpmndi info: " + e.getMessage());
        } catch (Exception e) {
            _logger.error("Exception adding bpmndi info: " + e.getMessage());
        }
    }

    private void storeInRepository(String uuid, String rawSvg, String transformto, String processid, Repository repository) {
        try {
            if(processid != null) {
                Asset<byte[]> processAsset = repository.loadAsset(uuid);


                String assetExt = "";
                String assetFileExt = "";
                if(transformto.equals(TO_PDF)) {
                    assetExt = "-pdf";
                    assetFileExt = ".pdf";
                }
                if(transformto.equals(TO_PNG)) {
                    assetExt = "-image";
                    assetFileExt = ".png";
                }
                if(transformto.equals(TO_SVG)) {
                    assetExt = "-svg";
                    assetFileExt = ".svg";
                }
                String assetFullName = processid + assetExt + assetFileExt;
                repository.deleteAssetFromPath(processAsset.getAssetLocation() + assetFullName);


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                if (transformto.equals(TO_PDF)) {
                    PDFTranscoder t = new PDFTranscoder();
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            rawSvg));
                    TranscoderOutput output = new TranscoderOutput(outputStream);
                    t.transcode(input, output);
                } else if (transformto.equals(TO_PNG)) {
                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            rawSvg));
                    TranscoderOutput output = new TranscoderOutput(outputStream);
                    try {
                        t.transcode(input, output);
                    } catch (Exception e) {
                        // issue with batik here..do not make a big deal
                        _logger.debug(e.getMessage());
                    }
                } else if(transformto.equals(TO_SVG)) {
                    OutputStreamWriter outStreamWriter = new OutputStreamWriter(outputStream);
                    outStreamWriter.write(rawSvg);
                    outStreamWriter.close();
                }
                AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);

                builder.name(processid + assetExt)
                        .type(assetFileExt.substring(1))
                        .location(processAsset.getAssetLocation())
                        .version(processAsset.getVersion())
                        .content(outputStream.toByteArray());

                Asset<byte[]> resourceAsset = builder.getAsset();

                repository.createAsset(resourceAsset);
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
    }

    private String getProcessContent(String uuid, Repository repository) {
        try {

            Asset<String> processAsset = repository.loadAsset(uuid);
            return processAsset.getAssetContent();
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return "";
        }
    }
}
