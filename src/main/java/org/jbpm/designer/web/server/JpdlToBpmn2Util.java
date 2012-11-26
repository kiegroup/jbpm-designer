package org.jbpm.designer.web.server;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.*;

import org.apache.log4j.Logger;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.*;
import org.eclipse.dd.dc.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.server.TransformerServlet.Parameters;
import org.jbpm.migration.JbpmMigration;

public class JpdlToBpmn2Util {

    private static final Logger _logger = Logger.getLogger(JpdlToBpmn2Util.class);
    
    public static void transformFromJpdlToBpmn2(Parameters info, HttpServletResponse resp, IDiagramProfile profile) throws IOException { 
        String jpdl = info.jpdl;
        String gpd = info.gpd;
        String pp = info.pp;
        
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
    }
    
    private static void revisitNodeNames(Definitions def) {
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
    
    private static void revisitSequenceFlows(Definitions def, String orig) {
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
    
    private static FlowNode getFlowNode(Definitions def, String nodeId) {
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
    
    private static void addBpmnDiInfo(Definitions def, String gpd) {
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
//                          List<Point> dockers = _dockers.get(sequenceFlow.getId());
//                          for (int i = 1; i < dockers.size() - 1; i++) {
//                              edge.getWaypoint().add(dockers.get(i));
//                          }
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
    
}
