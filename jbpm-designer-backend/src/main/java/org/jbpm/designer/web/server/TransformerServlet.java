/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.*;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import bpsim.impl.BpsimFactoryImpl;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.migration.JbpmMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Transformer for svg process representation to
 * various formats.
 *
 * @author Tihomir Surdilovic
 */
public class TransformerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(TransformerServlet.class);
    private static final String TO_PDF = "pdf";
    private static final String TO_PNG = "png";
    private static final String TO_SVG = "svg";
    private static final String JPDL_TO_BPMN2 = "jpdl2bpmn2";
    private static final String BPMN2_TO_JSON = "bpmn2json";
    private static final String JSON_TO_BPMN2 = "json2bpmn2";
    private static final String HTML_TO_PDF = "html2pdf";
    private static final String RESPACTION_SHOWURL = "showurl";

    private static final String SVG_WIDTH_PARAM = "svgwidth";
    private static final String SVG_HEIGHT_PARAM = "svgheight";
    private static final float DEFAULT_PDF_WIDTH = (float) 750.0;
    private static final float DEFAULT_PDF_HEIGHT = (float) 500.0;

    static {
        StringTokenizer html2pdfTagsSupported = new StringTokenizer("ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike h1 h2 h3 h4 h5 h6");
        HTMLWorker.tagsSupported.clear();
        while(html2pdfTagsSupported.hasMoreTokens()) {
            HTMLWorker.tagsSupported.put(html2pdfTagsSupported.nextToken(), null);
        }
    }

    private IDiagramProfile profile;
    // For unit testing purpose only
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

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
        String formattedSvgEncoded = req.getParameter("fsvg");
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String transformto = req.getParameter("transformto");
        String jpdl = req.getParameter("jpdl");
        String gpd = req.getParameter("gpd");
        String bpmn2in = req.getParameter("bpmn2");
        String jsonin = req.getParameter("json");
        String preprocessingData = req.getParameter("pp");
        String respaction = req.getParameter("respaction");
        String pp = req.getParameter("pp");
        String processid = req.getParameter("processid");
        String sourceEnc = req.getParameter("enc");
        String convertServiceTasks = req.getParameter("convertservicetasks");
        String htmlSourceEnc = req.getParameter("htmlenc");

        String formattedSvg = ( formattedSvgEncoded == null ? "" : new String(Base64.decodeBase64(formattedSvgEncoded), "UTF-8") );

        String htmlSource = ( htmlSourceEnc == null ? "" : new String(Base64.decodeBase64(htmlSourceEnc), "UTF-8") );

        if(sourceEnc != null && sourceEnc.equals("true")) {
            bpmn2in = new String(Base64.decodeBase64(bpmn2in), "UTF-8");
        }

        if (profile == null) {
            profile = _profileService.findProfile(req, profileName);
        }

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        Repository repository = profile.getRepository();

        if (transformto != null && transformto.equals(TO_PDF)) {
            if(respaction != null && respaction.equals(RESPACTION_SHOWURL)) {

                try {
                    ByteArrayOutputStream pdfBout = new ByteArrayOutputStream();
                    Document pdfDoc = new Document(PageSize.A4);
                    PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, pdfBout);
                    pdfDoc.open();
                    pdfDoc.addCreationDate();

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");

                    float widthHint = getFloatParam(req, SVG_WIDTH_PARAM, DEFAULT_PDF_WIDTH);
                    float heightHint = getFloatParam(req, SVG_HEIGHT_PARAM, DEFAULT_PDF_HEIGHT);
                    String objStyle = "style=\"width:" + widthHint + "px;height:" + heightHint + "px;\"";
                    t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, widthHint);
                    t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, heightHint);


                    ByteArrayOutputStream imageBout = new ByteArrayOutputStream();
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(imageBout);
                    t.transcode(input, output);

                    Image processImage = Image.getInstance(imageBout.toByteArray());
                    scalePDFImage(pdfDoc, processImage);
                    pdfDoc.add(processImage);

                    pdfDoc.close();

                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("text/plain");

                    resp.getWriter().write("<object type=\"application/pdf\" " + objStyle + " data=\"data:application/pdf;base64," + Base64.encodeBase64String(pdfBout.toByteArray()) + "\"></object>");
                } catch(Exception e) {
                    resp.sendError(500, e.getMessage());
                }
            } else {
                storeInRepository(uuid, formattedSvg, transformto, processid, repository);

                try {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/pdf");
                    if (processid != null) {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + processid + ".pdf\"");
                    } else {
                        resp.setHeader("Content-Disposition",
                                "attachment; filename=\"" + uuid + ".pdf\"");
                    }

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    Document pdfDoc = new Document(PageSize.A4);
                    PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, resp.getOutputStream());
                    pdfDoc.open();
                    pdfDoc.addCreationDate();

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            formattedSvg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);

                    Image processImage = Image.getInstance(bout.toByteArray());
                    scalePDFImage(pdfDoc, processImage);
                    pdfDoc.add(processImage);

                    pdfDoc.close();
                } catch(Exception e) {
                    resp.sendError(500, e.getMessage());
                }
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
                    if(req.getParameter(SVG_WIDTH_PARAM) != null && req.getParameter(SVG_HEIGHT_PARAM) != null) {
                        int widthHint = (int) getFloatParam(req, SVG_WIDTH_PARAM, DEFAULT_PDF_WIDTH);
                        int heightHint = (int) getFloatParam(req, SVG_HEIGHT_PARAM, DEFAULT_PDF_HEIGHT);
                        resp.getWriter().write("<img width=\"" + widthHint + "\" height=\"" + heightHint + "\" src=\"data:image/png;base64," + Base64.encodeBase64String(bout.toByteArray()) + "\">");
                    } else {
                        resp.getWriter().write("<img src=\"data:image/png;base64," + Base64.encodeBase64String(bout.toByteArray()) + "\">");
                    }
                } else {
                    storeInRepository(uuid, formattedSvg, transformto, processid, repository);
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
            storeInRepository(uuid, formattedSvg, transformto, processid, repository);
        } else if (transformto != null && transformto.equals(JPDL_TO_BPMN2)) {
            try {
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
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().print(json);
            } catch(Exception e) {
                _logger.error(e.getMessage());
                resp.setContentType("application/json");
                resp.getWriter().print("{}");
            }
        }  else if (transformto != null && transformto.equals(BPMN2_TO_JSON)) {
            try {
                if(convertServiceTasks != null && convertServiceTasks.equals("true")) {
                    bpmn2in = bpmn2in.replaceAll("drools:taskName=\".*?\"", "drools:taskName=\"ReadOnlyService\"");
                    bpmn2in = bpmn2in.replaceAll("tns:taskName=\".*?\"", "tns:taskName=\"ReadOnlyService\"");
                }

                Definitions def = ((JbpmProfileImpl) profile).getDefinitions(bpmn2in);
                def.setTargetNamespace("http://www.omg.org/bpmn20");

                if(convertServiceTasks != null && convertServiceTasks.equals("true")) {
                    // fix the data input associations for converted tasks
                    List<RootElement> rootElements =  def.getRootElements();
                    for(RootElement root : rootElements) {
                        if(root instanceof Process) {
                            updateTaskDataInputs((Process) root, def);
                        }
                    }
                }

                // get the xml from Definitions
                ResourceSet rSet = new ResourceSetImpl();
                rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2", new JBPMBpmn2ResourceFactoryImpl());
                JBPMBpmn2ResourceImpl bpmn2resource = (JBPMBpmn2ResourceImpl) rSet.createResource(URI.createURI("virtual.bpmn2"));
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION, true);
                bpmn2resource.getDefaultLoadOptions().put( JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY, true );
                bpmn2resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF, JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);
                bpmn2resource.setEncoding("UTF-8");
                rSet.getResources().add(bpmn2resource);
                bpmn2resource.getContents().add(def);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bpmn2resource.save(outputStream, new HashMap<Object, Object>());
                String revisedXmlModel =  outputStream.toString();
                String json = profile.createUnmarshaller().parseModel(revisedXmlModel, profile, pp);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().print(json);
            } catch(Exception e) {
                e.printStackTrace();
                _logger.error(e.getMessage());
                resp.setContentType("application/json");
                resp.getWriter().print("{}");
            }
        } else if (transformto != null && transformto.equals(JSON_TO_BPMN2)) {
            try {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();
                if(preprocessingData == null) {
                    preprocessingData = "";
                }
                String processXML = profile.createMarshaller().parseModel(jsonin, preprocessingData);
                resp.setContentType("application/xml");
                resp.getWriter().print(processXML);
            } catch(Exception e) {
                e.printStackTrace();
                _logger.error(e.getMessage());
                resp.setContentType("application/xml");
                resp.getWriter().print("");
            }

        } else if (transformto != null && transformto.equals(HTML_TO_PDF)) {
            try {
                resp.setContentType("application/pdf");
                if (processid != null) {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + processid + ".pdf\"");
                } else {
                    resp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + uuid + ".pdf\"");
                }

                Document pdfDoc = new Document(PageSize.A4);
                PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, resp.getOutputStream());
                pdfDoc.open();
                pdfDoc.addCreator("jBPM Designer");
                pdfDoc.addSubject("Business Process Documentation");
                pdfDoc.addCreationDate();
                pdfDoc.addTitle("Process Documentation");

                HTMLWorker htmlWorker = new HTMLWorker(pdfDoc);
                htmlWorker.parse(new StringReader(htmlSource));
                pdfDoc.close();
            } catch(DocumentException e) {
                resp.sendError(500, e.getMessage());
            }
        }
    }

    private void updateTaskDataInputs(FlowElementsContainer container, Definitions def) {
        List<FlowElement> flowElements = container.getFlowElements();
        for(FlowElement fe : flowElements) {
            if(fe instanceof Task && !(fe instanceof UserTask)) {
                Task task = (Task) fe;
                boolean foundReadOnlyServiceTask = false;
                Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
                while(iter.hasNext()) {
                    FeatureMap.Entry entry = iter.next();
                    if(entry.getEStructuralFeature().getName().equals("taskName")) {
                        if(entry.getValue().equals("ReadOnlyService")) {
                            foundReadOnlyServiceTask = true;
                        }
                    }
                }

                if(foundReadOnlyServiceTask) {
                    if(task.getDataInputAssociations() != null) {
                        List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();
                        for(DataInputAssociation dia : dataInputAssociations) {
                            if(dia.getTargetRef().getId().endsWith("TaskNameInput")) {
                                ((FormalExpression) dia.getAssignment().get(0).getFrom()).setBody("ReadOnlyService");
                            }
                        }
                    }
                }
            } else if(fe instanceof FlowElementsContainer) {
                updateTaskDataInputs((FlowElementsContainer) fe, def);
            }
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

    protected void storeInRepository(String uuid, String svg, String transformto, String processid, Repository repository) {
        String assetFullName = "";
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

                if(processid.startsWith(".")) {
                    processid = processid.substring(1, processid.length());
                }
                assetFullName = processid + assetExt + assetFileExt;

                repository.deleteAssetFromPath(processAsset.getAssetLocation() + File.separator +  assetFullName);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                if (transformto.equals(TO_PDF)) {

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    Document pdfDoc = new Document(PageSize.A4);
                    PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, outputStream);
                    pdfDoc.open();
                    pdfDoc.addCreationDate();

                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            svg));
                    TranscoderOutput output = new TranscoderOutput(bout);
                    t.transcode(input, output);

                    Image processImage = Image.getInstance(bout.toByteArray());
                    scalePDFImage(pdfDoc, processImage);
                    pdfDoc.add(processImage);

                    pdfDoc.close();
                } else if (transformto.equals(TO_PNG)) {
                    PNGTranscoder t = new PNGTranscoder();
                    t.addTranscodingHint(ImageTranscoder.KEY_MEDIA, "screen");
                    TranscoderInput input = new TranscoderInput(new StringReader(
                            svg));
                    TranscoderOutput output = new TranscoderOutput(outputStream);
                    try {
                        t.transcode(input, output);
                    } catch (Exception e) {
                        // issue with batik here..do not make a big deal
                        _logger.debug(e.getMessage());
                    }
                } else if(transformto.equals(TO_SVG)) {
                    OutputStreamWriter outStreamWriter = new OutputStreamWriter(outputStream);
                    outStreamWriter.write(svg);
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
            // just log that error happened
            if (e.getMessage() != null) {
                _logger.error(e.getMessage());
            }
            else {
                _logger.error(e.getClass().toString() + " " + assetFullName);
            }
            e.printStackTrace();
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

    private float getFloatParam(final HttpServletRequest req, final String paramName, final float defaultValue) {
        float value = defaultValue;
        String paramValue = req.getParameter(paramName);
        if (paramValue != null && !paramValue.isEmpty()) {
            try {
                value = Float.parseFloat(paramValue);
            } catch (NumberFormatException nfe) {
                value = defaultValue;
            }
        }
        return value;
    }

    public void scalePDFImage(Document document, Image image) {
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                - document.rightMargin()) / image.getWidth()) * 100;

        image.scalePercent(scaler);
    }
}
