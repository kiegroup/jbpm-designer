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
package org.jbpm.designer.bpmn2.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ControlParameters;
import bpsim.CostParameters;
import bpsim.ElementParameters;
import bpsim.FloatingParameterType;
import bpsim.NormalDistributionType;
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.PoissonDistributionType;
import bpsim.ResourceParameters;
import bpsim.Scenario;
import bpsim.TimeParameters;
import bpsim.UniformDistributionType;
import bpsim.impl.BpsimPackageImpl;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.Resource;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jbpm.designer.bpmn2.BpmnMarshallerHelper;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;

// transform BPMN2 into JSON
public class Bpmn2JsonMarshaller {

    public static final String defaultBgColor_Activities = "#fafad2";
    public static final String defaultBgColor_Events = "#f5deb3";
    public static final String defaultBgColor_StartEvents = "#9acd32";
    public static final String defaultBgColor_EndEvents = "#ff6347";
    public static final String defaultBgColor_DataObjects = "#C0C0C0";
    public static final String defaultBgColor_CatchingEvents = "#f5deb3";
    public static final String defaultBgColor_ThrowingEvents = "#8cabff";
    public static final String defaultBgColor_Gateways = "#f0e68c";
    public static final String defaultBgColor_Swimlanes = "#ffffff";

    public static final String defaultBrColor = "#000000";
    public static final String defaultBrColor_CatchingEvents = "#a0522d";
    public static final String defaultBrColor_ThrowingEvents = "#008cec";
    public static final String defaultBrColor_Gateways = "#a67f00";

    public static final String defaultFontColor = "#000000";
    public static final String defaultSequenceflowColor = "#000000";

    private static final List<String> defaultTypesList = Arrays.asList("Object",
                                                                       "Boolean",
                                                                       "Float",
                                                                       "Integer",
                                                                       "List",
                                                                       "String");

    private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();
    private Map<String, Association> _diagramAssociations = new HashMap<String, Association>();
    private Scenario _simulationScenario = null;
    private static final Logger _logger = LoggerFactory.getLogger(Bpmn2JsonMarshaller.class);
    private IDiagramProfile profile;
    private boolean coordianteManipulation = true;

    private static final float DEFAULT_ECLIPSE_RESOLUTION = 0;
    private static final float DEFAULT_RESOLUTION = 100;
    private static final float DEFAULT_RESOLUTION_X = 102.4F;
    private static final float DEFAULT_RESOLUTION_Y = 112.06F;
    private float resolution = DEFAULT_RESOLUTION;

    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    public String marshall(Definitions def,
                           String preProcessingData) throws IOException {
        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(baos,
                                                        JsonEncoding.UTF8);
        if (def.getRelationships() != null && def.getRelationships().size() > 0) {
            // current support for single relationship
            Relationship relationship = def.getRelationships().get(0);
            for (ExtensionAttributeValue extattrval : relationship.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();
                @SuppressWarnings("unchecked")
                List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>) extensionElements.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                                                                                                  true);
                if (bpsimExtensions != null && bpsimExtensions.size() > 0) {
                    BPSimDataType processAnalysis = bpsimExtensions.get(0);
                    if (processAnalysis.getScenario() != null && processAnalysis.getScenario().size() > 0) {
                        _simulationScenario = processAnalysis.getScenario().get(0);
                    }
                }
            }
        }
        if (preProcessingData == null || preProcessingData.length() < 1) {
            preProcessingData = "ReadOnlyService";
        }

        // this is a temp way to determine if
        // coordinate system changes are necessary
        String bpmn2Exporter = def.getExporter();
        String bpmn2ExporterVersion = def.getExporterVersion();
        boolean haveExporter = bpmn2Exporter != null && bpmn2ExporterVersion != null;
        if (_simulationScenario != null && !haveExporter) {
            coordianteManipulation = false;
        }

        marshallDefinitions(def,
                            generator,
                            preProcessingData);
        generator.close();

        return baos.toString("UTF-8");
    }

    private void linkSequenceFlows(List<FlowElement> flowElements) {
        Map<String, FlowNode> nodes = new HashMap<String, FlowNode>();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof FlowNode) {
                nodes.put(flowElement.getId(),
                          (FlowNode) flowElement);
                if (flowElement instanceof SubProcess) {
                    linkSequenceFlows(((SubProcess) flowElement).getFlowElements());
                }
            }
        }
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                if (sequenceFlow.getSourceRef() == null && sequenceFlow.getTargetRef() == null) {
                    String id = sequenceFlow.getId();
                    try {
                        String[] subids = id.split("-_");
                        String id1 = subids[0];
                        String id2 = "_" + subids[1];
                        FlowNode source = nodes.get(id1);
                        if (source != null) {
                            sequenceFlow.setSourceRef(source);
                        }
                        FlowNode target = nodes.get(id2);
                        if (target != null) {
                            sequenceFlow.setTargetRef(target);
                        }
                    } catch (Throwable t) {
                        // Do nothing
                    }
                }
            }
        }
    }

    protected void marshallDefinitions(Definitions def,
                                       JsonGenerator generator,
                                       String preProcessingData) throws JsonGenerationException, IOException {
        try {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       def.getId());
            /**
             * "properties":{"name":"",
             * "documentation":"",
             * "auditing":"",
             * "monitoring":"",
             * "executable":"true",
             * "package":"com.sample",
             * "vardefs":"a,b,c,d",
             * "lanes" : "a,b,c",
             * "id":"",
             * "version":"",
             * "author":"",
             * "language":"",
             * "namespaces":"",
             * "targetnamespace":"",
             * "expressionlanguage":"",
             * "typelanguage":"",
             * "creationdate":"",
             * "modificationdate":""
             * }
             */
            Map<String, Object> props = new LinkedHashMap<String, Object>();
            props.put("namespaces",
                      "");
            //props.put("targetnamespace", def.getTargetNamespace());
            props.put("targetnamespace",
                      "http://www.omg.org/bpmn20");
            props.put("typelanguage",
                      def.getTypeLanguage());
            props.put("name",
                      unescapeXml(def.getName()));
            props.put("id",
                      def.getId());
            props.put("expressionlanguage",
                      def.getExpressionLanguage());
            props.put("exporter",
                      StringUtils.isNotEmpty(def.getExporter()) ? def.getExporter() : "");
            props.put("exporterversion",
                      StringUtils.isNotEmpty(def.getExporterVersion()) ? def.getExporterVersion() : "");

            // backwards compat for BZ 1048191
            if (def.getDocumentation() != null && def.getDocumentation().size() > 0) {
                props.put("documentation",
                          def.getDocumentation().get(0).getText());
            }

            for (RootElement rootElement : def.getRootElements()) {
                if (rootElement instanceof Process) {
                    // have to wait for process node to finish properties and stencil marshalling
                    props.put("executable",
                              ((Process) rootElement).isIsExecutable() + "");
                    props.put("id",
                              rootElement.getId());
                    if (rootElement.getDocumentation() != null && rootElement.getDocumentation().size() > 0) {
                        props.put("documentation",
                                  rootElement.getDocumentation().get(0).getText());
                    }
                    Process pr = (Process) rootElement;
                    if (pr.getName() != null && pr.getName().length() > 0) {
                        props.put("processn",
                                  unescapeXml(((Process) rootElement).getName()));
                    }

                    List<Property> processProperties = ((Process) rootElement).getProperties();
                    if (processProperties != null && processProperties.size() > 0) {
                        String propVal = "";
                        for (int i = 0; i < processProperties.size(); i++) {
                            Property p = processProperties.get(i);
                            String pKPI = Utils.getMetaDataValue(p.getExtensionValues(),
                                                                 "customKPI");
                            propVal += p.getId();
                            // check the structureRef value
                            if (p.getItemSubjectRef() != null && p.getItemSubjectRef().getStructureRef() != null) {
                                propVal += ":" + p.getItemSubjectRef().getStructureRef();
                            }
                            if (pKPI != null && pKPI.length() > 0) {
                                propVal += ":" + pKPI;
                            }
                            if (i != processProperties.size() - 1) {
                                propVal += ",";
                            }
                        }
                        props.put("vardefs",
                                  propVal);
                    }

                    // packageName and version and adHoc are jbpm-specific extension attribute
                    Iterator<FeatureMap.Entry> iter = ((Process) rootElement).getAnyAttribute().iterator();
                    while (iter.hasNext()) {
                        FeatureMap.Entry entry = iter.next();
                        if (entry.getEStructuralFeature().getName().equals("packageName")) {
                            props.put("package",
                                      entry.getValue());
                        }

                        if (entry.getEStructuralFeature().getName().equals("version")) {
                            props.put("version",
                                      entry.getValue());
                        }

                        if (entry.getEStructuralFeature().getName().equals("adHoc")) {
                            props.put("adhocprocess",
                                      entry.getValue());
                        }
                    }

                    // process imports, custom description and globals extension elements
                    String allImports = "";
                    if ((rootElement).getExtensionValues() != null && (rootElement).getExtensionValues().size() > 0) {
                        String importsStr = "";
                        String globalsStr = "";

                        for (ExtensionAttributeValue extattrval : rootElement.getExtensionValues()) {
                            FeatureMap extensionElements = extattrval.getValue();

                            @SuppressWarnings("unchecked")
                            List<ImportType> importExtensions = (List<ImportType>) extensionElements
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT,
                                         true);
                            @SuppressWarnings("unchecked")
                            List<GlobalType> globalExtensions = (List<GlobalType>) extensionElements
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL,
                                         true);

                            List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                                         true);

                            for (ImportType importType : importExtensions) {
                                importsStr += importType.getName();
                                importsStr += "|default,";
                            }

                            for (GlobalType globalType : globalExtensions) {
                                globalsStr += (globalType.getIdentifier() + ":" + globalType.getType());
                                globalsStr += ",";
                            }

                            for (MetaDataType metaType : metadataExtensions) {
                                if (metaType.getName().equals("customDescription")) {
                                    props.put("customdescription",
                                              metaType.getMetaValue());
                                } else if (metaType.getName().equals("customCaseIdPrefix")) {
                                    props.put("customcaseidprefix",
                                              metaType.getMetaValue());
                                } else if (metaType.getName().equals("customCaseRoles")) {
                                    props.put("customcaseroles",
                                              metaType.getMetaValue());
                                } else if (metaType.getName().equals("customSLADueDate")) {
                                    props.put("customsladuedate",
                                              metaType.getMetaValue());
                                } else {
                                    props.put(metaType.getName(),
                                              metaType.getMetaValue());
                                }
                            }
                        }
                        allImports += importsStr;
                        if (globalsStr.length() > 0) {
                            if (globalsStr.endsWith(",")) {
                                globalsStr = globalsStr.substring(0,
                                                                  globalsStr.length() - 1);
                            }
                            props.put("globals",
                                      globalsStr);
                        }
                    }
                    // definitions imports (wsdl)
                    List<org.eclipse.bpmn2.Import> wsdlImports = def.getImports();
                    if (wsdlImports != null) {
                        for (org.eclipse.bpmn2.Import imp : wsdlImports) {
                            allImports += imp.getLocation() + "|" + imp.getNamespace() + "|wsdl,";
                        }
                    }
                    if (allImports.endsWith(",")) {
                        allImports = allImports.substring(0,
                                                          allImports.length() - 1);
                    }
                    props.put("imports",
                              allImports);

                    // simulation
                    if (_simulationScenario != null && _simulationScenario.getScenarioParameters() != null) {
                        props.put("currency",
                                  _simulationScenario.getScenarioParameters().getBaseCurrencyUnit() == null ? "" : _simulationScenario.getScenarioParameters().getBaseCurrencyUnit());
                        props.put("timeunit",
                                  _simulationScenario.getScenarioParameters().getBaseTimeUnit().getName());
                    }

                    // try to find diagram resolution property
                    // and add it as part of process props
                    for (BPMNDiagram diagram : def.getDiagrams()) {
                        if (diagram != null) {
                            BPMNPlane plane = diagram.getPlane();
                            if (plane != null) {
                                if (plane.getBpmnElement() == rootElement) {
                                    this.resolution = diagram.getResolution();
                                    // set the diagram resolution to default
                                    // we need to only apply the updates based
                                    // on the resolution once!
                                    props.put("diagramresolution", String.valueOf(DEFAULT_ECLIPSE_RESOLUTION));
                                    applyDiagramResolution(plane);
                                    break;
                                }
                            }
                        }
                    }

                    marshallProperties(props,
                                       generator);
                    marshallStencil("BPMNDiagram",
                                    generator);
                    linkSequenceFlows(((Process) rootElement).getFlowElements());
                    marshallProcess((Process) rootElement,
                                    def,
                                    generator,
                                    preProcessingData);
                } else if (rootElement instanceof Interface) {
                    // TODO
                } else if (rootElement instanceof ItemDefinition) {
                    // TODO
                } else if (rootElement instanceof Resource) {
                    // TODO
                } else if (rootElement instanceof Error) {
                    // TODO
                } else if (rootElement instanceof Message) {
                    // TODO
                } else if (rootElement instanceof Signal) {
                    // TODO
                } else if (rootElement instanceof Escalation) {
                    // TODO
                } else if (rootElement instanceof Collaboration) {

                } else {
                    _logger.warn("Unknown root element " + rootElement + ". This element will not be parsed.");
                }
            }

            generator.writeObjectFieldStart("stencilset");
            generator.writeObjectField("url",
                                       this.profile.getStencilSetURL());
            generator.writeObjectField("namespace",
                                       this.profile.getStencilSetNamespaceURL());
            generator.writeEndObject();
            generator.writeArrayFieldStart("ssextensions");
            generator.writeObject(this.profile.getStencilSetExtensionURL());
            generator.writeEndArray();
            generator.writeEndObject();
        } finally {
            _diagramElements.clear();
        }
    }

    public void applyDiagramResolution(BPMNPlane plane) {
        if(this.resolution != DEFAULT_ECLIPSE_RESOLUTION) {
            List<DiagramElement> planeElements = plane.getPlaneElement();
            for(DiagramElement element : planeElements) {
                if(element instanceof BPMNShape) {
                    BPMNShape shape = (BPMNShape) element;
                    Bounds bounds = shape.getBounds();
                    bounds.setX( (DEFAULT_RESOLUTION_X * bounds.getX()) / this.resolution );
                    bounds.setY( (DEFAULT_RESOLUTION_Y * bounds.getY()) / this.resolution );
                    bounds.setWidth( (DEFAULT_RESOLUTION * bounds.getWidth()) / this.resolution );
                    bounds.setHeight( (DEFAULT_RESOLUTION * bounds.getHeight()) / this.resolution );
                } else if(element instanceof BPMNEdge) {
                    BPMNEdge edge = (BPMNEdge) element;
                    List<Point> waypoints = edge.getWaypoint();
                    for(Point point : waypoints) {
                        point.setX( (DEFAULT_RESOLUTION_X * point.getX()) / this.resolution );
                        point.setY( (DEFAULT_RESOLUTION_Y * point.getY()) / this.resolution );
                    }
                }
            }
        }
    }

    public void applyResolutionForDockers(Point p) {
        if(this.resolution != DEFAULT_ECLIPSE_RESOLUTION) {
            p.setX( (DEFAULT_RESOLUTION_X * p.getX()) / this.resolution );
            p.setY( (DEFAULT_RESOLUTION_Y * p.getY()) / this.resolution );
        }
    }

    protected void marshallCallableElement(CallableElement callableElement,
                                           Definitions def,
                                           JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                                   callableElement.getId());

        if (callableElement instanceof Choreography) {
            marshallChoreography((Choreography) callableElement,
                                 generator);
        } else if (callableElement instanceof Conversation) {
            marshallConversation((Conversation) callableElement,
                                 generator);
        } else if (callableElement instanceof GlobalChoreographyTask) {
            marshallGlobalChoreographyTask((GlobalChoreographyTask) callableElement,
                                           generator);
        } else if (callableElement instanceof GlobalTask) {
            marshallGlobalTask((GlobalTask) callableElement,
                               generator);
        } else if (callableElement instanceof Process) {
            marshallProcess((Process) callableElement,
                            def,
                            generator,
                            "");
        } else {
            throw new UnsupportedOperationException("TODO"); //TODO!
        }
        generator.writeEndObject();
    }

    protected void marshallProcess(Process process,
                                   Definitions def,
                                   JsonGenerator generator,
                                   String preProcessingData) throws JsonGenerationException, IOException {
        BPMNPlane plane = null;
        for (BPMNDiagram d : def.getDiagrams()) {
            if (d != null) {
                BPMNPlane p = d.getPlane();
                if (p != null) {
                    if (p.getBpmnElement() == process) {
                        plane = p;
                        break;
                    }
                }
            }
        }
        if (plane == null) {
            throw new IllegalArgumentException("Could not find BPMNDI information");
        }
        generator.writeArrayFieldStart("childShapes");

        List<String> laneFlowElementsIds = new ArrayList<String>();
        for (LaneSet laneSet : process.getLaneSets()) {
            for (Lane lane : laneSet.getLanes()) {
                // we only want to marshall lanes if we have the bpmndi info for them!
                if (findDiagramElement(plane,
                                       lane) != null) {
                    laneFlowElementsIds.addAll(marshallLanes(lane,
                                                             plane,
                                                             generator,
                                                             0,
                                                             0,
                                                             preProcessingData,
                                                             def));
                }
            }
        }
        for (FlowElement flowElement : process.getFlowElements()) {
            if (!laneFlowElementsIds.contains(flowElement.getId())) {
                marshallFlowElement(flowElement,
                                    plane,
                                    generator,
                                    0,
                                    0,
                                    preProcessingData,
                                    def);
            }
        }

        for (Artifact artifact : process.getArtifacts()) {
            marshallArtifact(artifact,
                             plane,
                             generator,
                             0,
                             0,
                             preProcessingData,
                             def);
        }

        generator.writeEndArray();
    }

    private void setCatchEventProperties(CatchEvent event,
                                         Map<String, Object> properties,
                                         Definitions def) {
        if (event.getOutputSet() != null) {
            List<DataOutput> dataOutputs = event.getOutputSet().getDataOutputRefs();
            StringBuffer doutbuff = new StringBuffer();
            for (DataOutput dout : dataOutputs) {
                doutbuff.append(dout.getName());
                String dtype = getAnyAttributeValue(dout,
                                                    "dtype");
                if (dtype != null && !dtype.isEmpty()) {
                    doutbuff.append(":").append(dtype);
                }
                doutbuff.append(",");
            }
            if (doutbuff.length() > 0) {
                doutbuff.setLength(doutbuff.length() - 1);
            }
            properties.put("dataoutput",
                           doutbuff.toString());

            List<DataOutputAssociation> outputAssociations = event.getDataOutputAssociation();
            StringBuffer doutassociationbuff = new StringBuffer();
            for (DataOutputAssociation doa : outputAssociations) {
                String doaName = ((DataOutput) doa.getSourceRef().get(0)).getName();
                if (doaName != null && doaName.length() > 0) {
                    doutassociationbuff.append("[dout]" + updateDataInputOutputDashes(((DataOutput) doa.getSourceRef().get(0)).getName()));
                    doutassociationbuff.append("->");
                    doutassociationbuff.append(doa.getTargetRef().getId());
                    doutassociationbuff.append(",");
                }
            }
            if (doutassociationbuff.length() > 0) {
                doutassociationbuff.setLength(doutassociationbuff.length() - 1);
            }
            properties.put("dataoutputassociations",
                           doutassociationbuff.toString());
        }
        // event definitions
        List<EventDefinition> eventdefs = getEventDefinitionsForEvent(event);
        for (EventDefinition ed : eventdefs) {
            if (ed instanceof TimerEventDefinition) {
                TimerEventDefinition ted = (TimerEventDefinition) ed;
                if (ted.getTimeDate() != null) {
                    try {
                        properties.put("timedate",
                                       ((FormalExpression) ted.getTimeDate()).getBody());
                    } catch (Exception e) {
                        _logger.info("Could not find timedate for : " + ted);
                    }
                }
                if (ted.getTimeDuration() != null) {
                    try {
                        properties.put("timeduration",
                                       ((FormalExpression) ted.getTimeDuration()).getBody());
                    } catch (Exception e) {
                        _logger.info("Could not find timeduration for : " + ted);
                    }
                }
                if (ted.getTimeCycle() != null) {
                    try {
                        properties.put("timecycle",
                                       ((FormalExpression) ted.getTimeCycle()).getBody());
                        if (((FormalExpression) ted.getTimeCycle()).getLanguage() != null) {
                            properties.put("timecyclelanguage",
                                           ((FormalExpression) ted.getTimeCycle()).getLanguage());
                        }
                    } catch (Exception e) {
                        _logger.info("Could not find timecycle for : " + ted);
                    }
                }
            } else if (ed instanceof SignalEventDefinition) {
                if (((SignalEventDefinition) ed).getSignalRef() != null) {
                    // find signal with the corresponding id
                    boolean foundSignalRef = false;
                    List<RootElement> rootElements = def.getRootElements();
                    for (RootElement re : rootElements) {
                        if (re instanceof Signal) {
                            if (re.getId().equals(((SignalEventDefinition) ed).getSignalRef())) {
                                properties.put("signalref",
                                               ((Signal) re).getName());
                                foundSignalRef = true;
                            }
                        }
                    }
                    if (!foundSignalRef) {
                        properties.put("signalref",
                                       "");
                    }
                } else {
                    properties.put("signalref",
                                   "");
                }
            } else if (ed instanceof ErrorEventDefinition) {
                if (((ErrorEventDefinition) ed).getErrorRef() != null && ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() != null) {
                    properties.put("errorref",
                                   ((ErrorEventDefinition) ed).getErrorRef().getErrorCode());
                } else {
                    properties.put("errorref",
                                   "");
                }
            } else if (ed instanceof ConditionalEventDefinition) {
                try {
                    FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
                    if (conditionalExp.getBody() != null) {
                        properties.put("conditionexpression",
                                       conditionalExp.getBody().replaceAll("\n",
                                                                           "\\\\n"));
                    }
                    if (conditionalExp.getLanguage() != null) {
                        String languageVal = conditionalExp.getLanguage();
                        if (languageVal.equals("http://www.jboss.org/drools/rule")) {
                            properties.put("conditionlanguage",
                                           "drools");
                        } else if (languageVal.equals("http://www.mvel.org/2.0")) {
                            properties.put("conditionlanguage",
                                           "mvel");
                        } else {
                            // default to drools
                            properties.put("conditionlanguage",
                                           "drools");
                        }
                    }
                } catch (Exception e) {
                    _logger.info("Could not find conditional expression for: " + ed);
                }
            } else if (ed instanceof EscalationEventDefinition) {
                if (((EscalationEventDefinition) ed).getEscalationRef() != null) {
                    Escalation esc = ((EscalationEventDefinition) ed).getEscalationRef();
                    if (esc.getEscalationCode() != null && esc.getEscalationCode().length() > 0) {
                        properties.put("escalationcode",
                                       esc.getEscalationCode());
                    } else {
                        properties.put("escalationcode",
                                       "");
                    }
                }
            } else if (ed instanceof MessageEventDefinition) {
                setMessageRefProperties(properties,
                                        ((MessageEventDefinition) ed).getMessageRef());
            } else if (ed instanceof CompensateEventDefinition) {
                if (((CompensateEventDefinition) ed).getActivityRef() != null) {
                    Activity act = ((CompensateEventDefinition) ed).getActivityRef();
                    properties.put("activityref",
                                   act.getName());
                }
            }
        }

        // custom SLA due date
        marshalCustomSLADueDateMetadata(event,
                                        properties);
    }

    private void setMessageRefProperties(final Map<String, Object> properties,
                                         final Message message) {
        if (message != null) {
            if (message.getName() != null) {
                properties.put("messageref",
                               message.getName());
            } else {
                properties.put("messageref",
                               message.getId());
            }
        }
    }

    private void setThrowEventProperties(ThrowEvent event,
                                         Map<String, Object> properties,
                                         Definitions def) {
        if (event.getInputSet() != null) {
            List<DataInput> dataInputs = event.getInputSet().getDataInputRefs();
            StringBuffer dinbuff = new StringBuffer();

            for (DataInput din : dataInputs) {
                dinbuff.append(din.getName());
                String dtype = getAnyAttributeValue(din,
                                                    "dtype");
                if (dtype != null && !dtype.isEmpty()) {
                    dinbuff.append(":").append(dtype);
                }
                dinbuff.append(",");
            }
            if (dinbuff.length() > 0) {
                dinbuff.setLength(dinbuff.length() - 1);
            }
            properties.put("datainput",
                           dinbuff.toString());

            StringBuilder associationBuff = new StringBuilder();
            marshallDataInputAssociations(associationBuff,
                                          event.getDataInputAssociation());

            String assignmentString = associationBuff.toString();
            if (assignmentString.endsWith(",")) {
                assignmentString = assignmentString.substring(0,
                                                              assignmentString.length() - 1);
            }
            properties.put("datainputassociations",
                           assignmentString);
        }

        // signal scope
        String signalScope = Utils.getMetaDataValue(event.getExtensionValues(),
                                                    "customScope");
        if (signalScope != null) {
            properties.put("signalscope",
                           signalScope);
        }

        // event definitions
        List<EventDefinition> eventdefs = getEventDefinitionsForEvent(event);
        for (EventDefinition ed : eventdefs) {
            if (ed instanceof TimerEventDefinition) {
                TimerEventDefinition ted = (TimerEventDefinition) ed;
                if (ted.getTimeDate() != null) {
                    try {
                        properties.put("timedate",
                                       ((FormalExpression) ted.getTimeDate()).getBody());
                    } catch (Exception e) {
                        _logger.info("Could not find timedate for: " + ted);
                    }
                }
                if (ted.getTimeDuration() != null) {
                    try {
                        properties.put("timeduration",
                                       ((FormalExpression) ted.getTimeDuration()).getBody());
                    } catch (Exception e) {
                        _logger.info("Could not find timeduration for: " + ted);
                    }
                }
                if (ted.getTimeCycle() != null) {
                    try {
                        properties.put("timecycle",
                                       ((FormalExpression) ted.getTimeCycle()).getBody());
                        if (((FormalExpression) ted.getTimeCycle()).getLanguage() != null) {
                            properties.put("timecyclelanguage",
                                           ((FormalExpression) ted.getTimeCycle()).getLanguage());
                        }
                    } catch (Exception e) {
                        _logger.info("Could not find timecycle for: " + ted);
                    }
                }
            } else if (ed instanceof SignalEventDefinition) {
                if (((SignalEventDefinition) ed).getSignalRef() != null) {
                    // find signal with the corresponding id
                    boolean foundSignalRef = false;
                    List<RootElement> rootElements = def.getRootElements();
                    for (RootElement re : rootElements) {
                        if (re instanceof Signal) {
                            if (re.getId().equals(((SignalEventDefinition) ed).getSignalRef())) {
                                properties.put("signalref",
                                               ((Signal) re).getName());
                                foundSignalRef = true;
                            }
                        }
                    }
                    if (!foundSignalRef) {
                        properties.put("signalref",
                                       "");
                    }
                } else {
                    properties.put("signalref",
                                   "");
                }
            } else if (ed instanceof ErrorEventDefinition) {
                if (((ErrorEventDefinition) ed).getErrorRef() != null && ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() != null) {
                    properties.put("errorref",
                                   ((ErrorEventDefinition) ed).getErrorRef().getErrorCode());
                } else {
                    properties.put("errorref",
                                   "");
                }
            } else if (ed instanceof ConditionalEventDefinition) {
                try {
                    FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
                    if (conditionalExp.getBody() != null) {
                        properties.put("conditionexpression",
                                       conditionalExp.getBody());
                    }
                    if (conditionalExp.getLanguage() != null) {
                        String languageVal = conditionalExp.getLanguage();
                        if (languageVal.equals("http://www.jboss.org/drools/rule")) {
                            properties.put("conditionlanguage",
                                           "drools");
                        } else if (languageVal.equals("http://www.mvel.org/2.0")) {
                            properties.put("conditionlanguage",
                                           "mvel");
                        } else {
                            // default to drools
                            properties.put("conditionlanguage",
                                           "drools");
                        }
                    }
                } catch (Exception e) {
                    _logger.info("Could not find conditionexpression for: " + ed);
                }
            } else if (ed instanceof EscalationEventDefinition) {
                if (((EscalationEventDefinition) ed).getEscalationRef() != null) {
                    Escalation esc = ((EscalationEventDefinition) ed).getEscalationRef();
                    if (esc.getEscalationCode() != null && esc.getEscalationCode().length() > 0) {
                        properties.put("escalationcode",
                                       esc.getEscalationCode());
                    } else {
                        properties.put("escalationcode",
                                       "");
                    }
                }
            } else if (ed instanceof MessageEventDefinition) {
                setMessageRefProperties(properties,
                                        ((MessageEventDefinition) ed).getMessageRef());
            } else if (ed instanceof CompensateEventDefinition) {
                if (((CompensateEventDefinition) ed).getActivityRef() != null) {
                    Activity act = ((CompensateEventDefinition) ed).getActivityRef();
                    properties.put("activityref",
                                   act.getName());
                }
            }
        }
    }

    private List<String> marshallLanes(Lane lane,
                                       BPMNPlane plane,
                                       JsonGenerator generator,
                                       float xOffset,
                                       float yOffset,
                                       String preProcessingData,
                                       Definitions def) throws JsonGenerationException, IOException {
        Bounds bounds = ((BPMNShape) findDiagramElement(plane,
                                                        lane)).getBounds();

        List<String> nodeRefIds = new ArrayList<String>();
        if (bounds != null) {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       lane.getId());
            Map<String, Object> laneProperties = new LinkedHashMap<String, Object>();
            if (lane.getName() != null) {
                laneProperties.put("name",
                                   unescapeXml(lane.getName()));
            } else {
                laneProperties.put("name",
                                   "");
            }

            // overwrite name if elementname extension element is present
            String elementName = Utils.getMetaDataValue(lane.getExtensionValues(),
                                                        "elementname");
            if (elementName != null) {
                laneProperties.put("name",
                                   elementName);
            }
            Documentation doc = getDocumentation(lane);
            if (doc != null) {
                laneProperties.put("documentation",
                                   doc.getText());
            }

            Iterator<FeatureMap.Entry> iter = lane.getAnyAttribute().iterator();
            boolean foundBgColor = false;
            boolean foundBrColor = false;
            boolean foundFontColor = false;
            boolean foundSelectable = false;
            while (iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if (entry.getEStructuralFeature().getName().equals("background-color") || entry.getEStructuralFeature().getName().equals("bgcolor")) {
                    laneProperties.put("bgcolor",
                                       entry.getValue());
                    foundBgColor = true;
                }
                if (entry.getEStructuralFeature().getName().equals("border-color") || entry.getEStructuralFeature().getName().equals("bordercolor")) {
                    laneProperties.put("bordercolor",
                                       entry.getValue());
                    foundBrColor = true;
                }
                if (entry.getEStructuralFeature().getName().equals("fontsize")) {
                    laneProperties.put("fontsize",
                                       entry.getValue());
                    foundBrColor = true;
                }
                if (entry.getEStructuralFeature().getName().equals("color") || entry.getEStructuralFeature().getName().equals("fontcolor")) {
                    laneProperties.put("fontcolor",
                                       entry.getValue());
                    foundFontColor = true;
                }
                if (entry.getEStructuralFeature().getName().equals("selectable")) {
                    laneProperties.put("isselectable",
                                       entry.getValue());
                    foundSelectable = true;
                }
            }
            if (!foundBgColor) {
                laneProperties.put("bgcolor",
                                   defaultBgColor_Swimlanes);
            }

            if (!foundBrColor) {
                laneProperties.put("bordercolor",
                                   defaultBrColor);
            }

            if (!foundFontColor) {
                laneProperties.put("fontcolor",
                                   defaultFontColor);
            }

            if (!foundSelectable) {
                laneProperties.put("isselectable",
                                   "true");
            }

            marshallProperties(laneProperties,
                               generator);
            generator.writeObjectFieldStart("stencil");
            generator.writeObjectField("id",
                                       "Lane");
            generator.writeEndObject();
            generator.writeArrayFieldStart("childShapes");
            for (FlowElement flowElement : lane.getFlowNodeRefs()) {
                nodeRefIds.add(flowElement.getId());
                if (coordianteManipulation) {
                    marshallFlowElement(flowElement,
                                        plane,
                                        generator,
                                        bounds.getX(),
                                        bounds.getY(),
                                        preProcessingData,
                                        def);
                } else {
                    marshallFlowElement(flowElement,
                                        plane,
                                        generator,
                                        0,
                                        0,
                                        preProcessingData,
                                        def);
                }
            }
            generator.writeEndArray();
            generator.writeArrayFieldStart("outgoing");
            Process process = (Process) plane.getBpmnElement();
            writeAssociations(process,
                              lane.getId(),
                              generator);
            generator.writeEndArray();
            generator.writeObjectFieldStart("bounds");
            generator.writeObjectFieldStart("lowerRight");
            generator.writeObjectField("x",
                                           bounds.getX() + bounds.getWidth() - xOffset );
            generator.writeObjectField("y",
                                           bounds.getY() + bounds.getHeight() - yOffset);
            generator.writeEndObject();
            generator.writeObjectFieldStart("upperLeft");
            generator.writeObjectField("x",
                                           bounds.getX() - xOffset);
            generator.writeObjectField("y",
                                           bounds.getY() - yOffset);
            generator.writeEndObject();
            generator.writeEndObject();
            generator.writeEndObject();
        } else {
            // dont marshall the lane unless it has BPMNDI info (eclipse editor does not generate it for lanes currently.
            for (FlowElement flowElement : lane.getFlowNodeRefs()) {
                nodeRefIds.add(flowElement.getId());
                // we dont want an offset here!
                marshallFlowElement(flowElement,
                                    plane,
                                    generator,
                                    0,
                                    0,
                                    preProcessingData,
                                    def);
            }
        }

        return nodeRefIds;
    }

    protected void marshallFlowElement(FlowElement flowElement,
                                       BPMNPlane plane,
                                       JsonGenerator generator,
                                       float xOffset,
                                       float yOffset,
                                       String preProcessingData,
                                       Definitions def) throws JsonGenerationException, IOException {
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                                   flowElement.getId());

        Map<String, Object> flowElementProperties = new LinkedHashMap<String, Object>();
        Iterator<FeatureMap.Entry> iter = flowElement.getAnyAttribute().iterator();
        boolean foundBgColor = false;
        boolean foundBrColor = false;
        boolean foundFontColor = false;
        boolean foundSelectable = false;
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("background-color") || entry.getEStructuralFeature().getName().equals("bgcolor")) {
                flowElementProperties.put("bgcolor",
                                          entry.getValue());
                foundBgColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("border-color") || entry.getEStructuralFeature().getName().equals("bordercolor")) {
                flowElementProperties.put("bordercolor",
                                          entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("fontsize")) {
                flowElementProperties.put("fontsize",
                                          entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("color") || entry.getEStructuralFeature().getName().equals("fontcolor")) {
                flowElementProperties.put("fontcolor",
                                          entry.getValue());
                foundFontColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("selectable")) {
                flowElementProperties.put("isselectable",
                                          entry.getValue());
                foundSelectable = true;
            }
        }
        if (!foundBgColor) {
            if (flowElement instanceof Activity || flowElement instanceof SubProcess) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_Activities);
            } else if (flowElement instanceof StartEvent) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_StartEvents);
            } else if (flowElement instanceof EndEvent) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_EndEvents);
            } else if (flowElement instanceof DataObject) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_DataObjects);
            } else if (flowElement instanceof CatchEvent) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_CatchingEvents);
            } else if (flowElement instanceof ThrowEvent) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_ThrowingEvents);
            } else if (flowElement instanceof Gateway) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_Gateways);
            } else if (flowElement instanceof Lane) {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_Swimlanes);
            } else {
                flowElementProperties.put("bgcolor",
                                          defaultBgColor_Events);
            }
        }

        if (!foundBrColor) {
            if (flowElement instanceof CatchEvent && !(flowElement instanceof StartEvent)) {
                flowElementProperties.put("bordercolor",
                                          defaultBrColor_CatchingEvents);
            } else if (flowElement instanceof ThrowEvent && !(flowElement instanceof EndEvent)) {
                flowElementProperties.put("bordercolor",
                                          defaultBrColor_ThrowingEvents);
            } else if (flowElement instanceof Gateway) {
                flowElementProperties.put("bordercolor",
                                          defaultBrColor_Gateways);
            } else {
                flowElementProperties.put("bordercolor",
                                          defaultBrColor);
            }
        }

        if (!foundFontColor) {
            flowElementProperties.put("fontcolor",
                                      defaultFontColor);
        }

        if (!foundSelectable) {
            flowElementProperties.put("isselectable",
                                      "true");
        }

        Map<String, Object> catchEventProperties = new LinkedHashMap<String, Object>(flowElementProperties);
        Map<String, Object> throwEventProperties = new LinkedHashMap<String, Object>(flowElementProperties);
        if (flowElement instanceof CatchEvent) {
            setCatchEventProperties((CatchEvent) flowElement,
                                    catchEventProperties,
                                    def);
        }
        if (flowElement instanceof ThrowEvent) {
            setThrowEventProperties((ThrowEvent) flowElement,
                                    throwEventProperties,
                                    def);
        }
        if (flowElement instanceof StartEvent) {
            marshallStartEvent((StartEvent) flowElement,
                               plane,
                               generator,
                               xOffset,
                               yOffset,
                               catchEventProperties);
        } else if (flowElement instanceof EndEvent) {
            marshallEndEvent((EndEvent) flowElement,
                             plane,
                             generator,
                             xOffset,
                             yOffset,
                             throwEventProperties);
        } else if (flowElement instanceof IntermediateThrowEvent) {
            marshallIntermediateThrowEvent((IntermediateThrowEvent) flowElement,
                                           plane,
                                           generator,
                                           xOffset,
                                           yOffset,
                                           throwEventProperties);
        } else if (flowElement instanceof IntermediateCatchEvent) {
            marshallIntermediateCatchEvent((IntermediateCatchEvent) flowElement,
                                           plane,
                                           generator,
                                           xOffset,
                                           yOffset,
                                           catchEventProperties);
        } else if (flowElement instanceof BoundaryEvent) {
            marshallBoundaryEvent((BoundaryEvent) flowElement,
                                  plane,
                                  generator,
                                  xOffset,
                                  yOffset,
                                  catchEventProperties);
        } else if (flowElement instanceof Task) {
            marshallTask((Task) flowElement,
                         plane,
                         generator,
                         xOffset,
                         yOffset,
                         preProcessingData,
                         def,
                         flowElementProperties);
        } else if (flowElement instanceof TextAnnotation) {
            marshallTextAnnotation((TextAnnotation) flowElement,
                                   plane,
                                   generator,
                                   xOffset,
                                   yOffset,
                                   preProcessingData,
                                   def,
                                   flowElementProperties);
        } else if (flowElement instanceof SequenceFlow) {
            marshallSequenceFlow((SequenceFlow) flowElement,
                                 plane,
                                 generator,
                                 xOffset,
                                 yOffset);
        } else if (flowElement instanceof ParallelGateway) {
            marshallParallelGateway((ParallelGateway) flowElement,
                                    plane,
                                    generator,
                                    xOffset,
                                    yOffset,
                                    flowElementProperties);
        } else if (flowElement instanceof ExclusiveGateway) {
            marshallExclusiveGateway((ExclusiveGateway) flowElement,
                                     plane,
                                     generator,
                                     xOffset,
                                     yOffset,
                                     flowElementProperties);
        } else if (flowElement instanceof InclusiveGateway) {
            marshallInclusiveGateway((InclusiveGateway) flowElement,
                                     plane,
                                     generator,
                                     xOffset,
                                     yOffset,
                                     flowElementProperties);
        } else if (flowElement instanceof EventBasedGateway) {
            marshallEventBasedGateway((EventBasedGateway) flowElement,
                                      plane,
                                      generator,
                                      xOffset,
                                      yOffset,
                                      flowElementProperties);
        } else if (flowElement instanceof ComplexGateway) {
            marshallComplexGateway((ComplexGateway) flowElement,
                                   plane,
                                   generator,
                                   xOffset,
                                   yOffset,
                                   flowElementProperties);
        } else if (flowElement instanceof CallActivity) {
            marshallCallActivity((CallActivity) flowElement,
                                 plane,
                                 generator,
                                 xOffset,
                                 yOffset,
                                 flowElementProperties);
        } else if (flowElement instanceof SubProcess) {
            marshallSubProcess((SubProcess) flowElement,
                               plane,
                               generator,
                               xOffset,
                               yOffset,
                               preProcessingData,
                               def,
                               flowElementProperties);
        } else if (flowElement instanceof DataObject) {
            // only marshall if we can find DI info for it - BZ 800346
            if (findDiagramElement(plane,
                                   (DataObject) flowElement) != null) {
                marshallDataObject((DataObject) flowElement,
                                   plane,
                                   generator,
                                   xOffset,
                                   yOffset,
                                   flowElementProperties);
            } else {
                _logger.info("Could not marshall Data Object " + (DataObject) flowElement + " because no DI information could be found.");
            }
        } else {
            throw new UnsupportedOperationException("Unknown flow element " + flowElement);
        }
        generator.writeEndObject();
    }

    protected void marshallStartEvent(StartEvent startEvent,
                                      BPMNPlane plane,
                                      JsonGenerator generator,
                                      float xOffset,
                                      float yOffset,
                                      Map<String, Object> properties) throws JsonGenerationException, IOException {
        // custom SLA due date
        marshalCustomSLADueDateMetadata(startEvent,
                                        properties);

        setSimulationProperties(startEvent.getId(),
                                properties);

        List<EventDefinition> eventDefinitions = getEventDefinitionsForEvent(startEvent);
        properties.put("isinterrupting",
                       startEvent.isIsInterrupting());
        if (eventDefinitions == null || eventDefinitions.size() == 0) {
            marshallNode(startEvent,
                         properties,
                         "StartNoneEvent",
                         plane,
                         generator,
                         xOffset,
                         yOffset);
        } else if (eventDefinitions.size() == 1) {
            EventDefinition eventDefinition = eventDefinitions.get(0);
            if (eventDefinition instanceof ConditionalEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartConditionalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof SignalEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartSignalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof MessageEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartMessageEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof TimerEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartTimerEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ErrorEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartErrorEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ConditionalEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartConditionalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartEscalationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(startEvent,
                             properties,
                             "StartCompensationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else {
                throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
            }
        } else {
            throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }

    protected void marshallEndEvent(EndEvent endEvent,
                                    BPMNPlane plane,
                                    JsonGenerator generator,
                                    float xOffset,
                                    float yOffset,
                                    Map<String, Object> properties) throws JsonGenerationException, IOException {
        setSimulationProperties(endEvent.getId(),
                                properties);

        List<EventDefinition> eventDefinitions = getEventDefinitionsForEvent(endEvent);
        if (eventDefinitions == null || eventDefinitions.size() == 0) {
            marshallNode(endEvent,
                         properties,
                         "EndNoneEvent",
                         plane,
                         generator,
                         xOffset,
                         yOffset);
        } else if (eventDefinitions.size() == 1) {
            EventDefinition eventDefinition = eventDefinitions.get(0);
            if (eventDefinition instanceof TerminateEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndTerminateEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof SignalEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndSignalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof MessageEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndMessageEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ErrorEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndErrorEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndEscalationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndCompensationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CancelEventDefinition) {
                marshallNode(endEvent,
                             properties,
                             "EndCancelEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else {
                throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
            }
        } else {
            throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }

    protected void marshallIntermediateCatchEvent(IntermediateCatchEvent catchEvent,
                                                  BPMNPlane plane,
                                                  JsonGenerator generator,
                                                  float xOffset,
                                                  float yOffset,
                                                  Map<String, Object> properties) throws JsonGenerationException, IOException {
        List<EventDefinition> eventDefinitions = getEventDefinitionsForEvent(catchEvent);
        // simulation properties
        setSimulationProperties(catchEvent.getId(),
                                properties);
        if (eventDefinitions.size() == 1) {
            EventDefinition eventDefinition = eventDefinitions.get(0);
            if (eventDefinition instanceof SignalEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateSignalEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof MessageEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateMessageEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof TimerEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateTimerEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ConditionalEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateConditionalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ErrorEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateErrorEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateEscalationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(catchEvent,
                             properties,
                             "IntermediateCompensationEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else {
                throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
            }
        } else {
            throw new UnsupportedOperationException("Intermediate catch event does not have event definition.");
        }
    }

    protected void marshallBoundaryEvent(BoundaryEvent boundaryEvent,
                                         BPMNPlane plane,
                                         JsonGenerator generator,
                                         float xOffset,
                                         float yOffset,
                                         Map<String, Object> catchEventProperties) throws JsonGenerationException, IOException {
        List<EventDefinition> eventDefinitions = getEventDefinitionsForEvent(boundaryEvent);
        if (boundaryEvent.isCancelActivity()) {
            catchEventProperties.put("boundarycancelactivity",
                                     "true");
        } else {
            catchEventProperties.put("boundarycancelactivity",
                                     "false");
        }

        // custom SLA due date
        marshalCustomSLADueDateMetadata(boundaryEvent,
                                        catchEventProperties);

        // simulation properties
        setSimulationProperties(boundaryEvent.getId(),
                                catchEventProperties);

        if (eventDefinitions.size() == 1) {
            EventDefinition eventDefinition = eventDefinitions.get(0);
            if (eventDefinition instanceof SignalEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateSignalEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateEscalationEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ErrorEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateErrorEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof TimerEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateTimerEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateCompensationEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof ConditionalEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateConditionalEvent",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof MessageEventDefinition) {
                marshallNode(boundaryEvent,
                             catchEventProperties,
                             "IntermediateMessageEventCatching",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else {
                throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
            }
        } else {
            throw new UnsupportedOperationException("None or multiple event definitions not supported for boundary event");
        }
    }

    protected void marshallIntermediateThrowEvent(IntermediateThrowEvent throwEvent,
                                                  BPMNPlane plane,
                                                  JsonGenerator generator,
                                                  float xOffset,
                                                  float yOffset,
                                                  Map<String, Object> properties) throws JsonGenerationException, IOException {
        List<EventDefinition> eventDefinitions = getEventDefinitionsForEvent(throwEvent);

        // simulation properties
        setSimulationProperties(throwEvent.getId(),
                                properties);

        if (eventDefinitions.size() == 0) {
            marshallNode(throwEvent,
                         properties,
                         "IntermediateEvent",
                         plane,
                         generator,
                         xOffset,
                         yOffset);
        } else if (eventDefinitions.size() == 1) {
            EventDefinition eventDefinition = eventDefinitions.get(0);
            if (eventDefinition instanceof SignalEventDefinition) {
                marshallNode(throwEvent,
                             properties,
                             "IntermediateSignalEventThrowing",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof MessageEventDefinition) {
                marshallNode(throwEvent,
                             properties,
                             "IntermediateMessageEventThrowing",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(throwEvent,
                             properties,
                             "IntermediateEscalationEventThrowing",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else if (eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(throwEvent,
                             properties,
                             "IntermediateCompensationEventThrowing",
                             plane,
                             generator,
                             xOffset,
                             yOffset);
            } else {
                throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
            }
        } else {
            throw new UnsupportedOperationException("None or multiple event definitions not supported for intermediate throw event");
        }
    }

    protected void marshallCallActivity(CallActivity callActivity,
                                        BPMNPlane plane,
                                        JsonGenerator generator,
                                        float xOffset,
                                        float yOffset,
                                        Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);

        Iterator<FeatureMap.Entry> iter = callActivity.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("independent")) {
                properties.put("independent",
                               entry.getValue());
            }

            if (entry.getEStructuralFeature().getName().equals("waitForCompletion")) {
                properties.put("waitforcompletion",
                               entry.getValue());
            }
        }

        if (callActivity.getCalledElement() != null && callActivity.getCalledElement().length() > 0) {
            properties.put("calledelement",
                           callActivity.getCalledElement());
        }

        // custom async
        String customAsyncMetaData = Utils.getMetaDataValue(callActivity.getExtensionValues(),
                                                            "customAsync");
        String customAsync = (customAsyncMetaData != null && customAsyncMetaData.length() > 0) ? customAsyncMetaData : "false";
        properties.put("isasync",
                       customAsync);

        // custom abortParent
        String customAbortParentMetaData = Utils.getMetaDataValue(callActivity.getExtensionValues(),
                                                                  "customAbortParent");
        String customAbortParent = (customAbortParentMetaData != null && customAbortParentMetaData.length() > 0) ? customAbortParentMetaData : "true";
        properties.put("isabortparent",
                       customAbortParent);

        // custom SLA due date
        marshalCustomSLADueDateMetadata(callActivity,
                                        properties);

        // data inputs
        marshallDataInputSet(callActivity,
                             properties);

        // data outputs
        marshallDataOutputSet(callActivity,
                              properties);

        // assignments
        StringBuilder associationBuff = new StringBuilder();
        List<DataInputAssociation> inputAssociations = callActivity.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = callActivity.getDataOutputAssociations();

        marshallDataInputAssociations(associationBuff,
                                      inputAssociations);
        marshallDataOutputAssociations(associationBuff,
                                       outputAssociations);

        String assignmentString = associationBuff.toString();
        if (assignmentString.endsWith(",")) {
            assignmentString = assignmentString.substring(0,
                                                          assignmentString.length() - 1);
        }
        properties.put("assignments",
                       assignmentString);

        // on-entry and on-exit actions
        marshallEntryExitActions(callActivity,
                                 properties);

        // simulation properties
        setSimulationProperties(callActivity.getId(),
                                properties);

        marshallNode(callActivity,
                     properties,
                     "ReusableSubprocess",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallTask(Task task,
                                BPMNPlane plane,
                                JsonGenerator generator,
                                float xOffset,
                                float yOffset,
                                String preProcessingData,
                                Definitions def,
                                Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
        String taskType = "None";
        if (task instanceof BusinessRuleTask) {
            taskType = "Business Rule";
            Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
            while (iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if (entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
                    properties.put("ruleflowgroup",
                                   entry.getValue());
                }
            }
            String ruleLanguage = ((BusinessRuleTask) task).getImplementation();
            if (RuleSetNode.DMN_LANG.equals(ruleLanguage)) {
                ruleLanguage = BpmnMarshallerHelper.RULE_LANG_DMN;
            } else {
                ruleLanguage = BpmnMarshallerHelper.RULE_LANG_DRL;
            }
            properties.put("rulelanguage",
                           ruleLanguage);
        } else if (task instanceof ScriptTask) {
            ScriptTask scriptTask = (ScriptTask) task;
            properties.put("script",
                           scriptTask.getScript() != null ? scriptTask.getScript().replace("\\",
                                                                                           "\\\\").replace("\n",
                                                                                                           "\\n") : "");
            String format = scriptTask.getScriptFormat();
            if (format != null && format.length() > 0) {
                String formatToWrite = getScriptLanguageFormat(format);
                properties.put("script_language",
                               formatToWrite);
            }
            taskType = "Script";
        } else if (task instanceof ServiceTask) {
            taskType = "Service";
            ServiceTask serviceTask = (ServiceTask) task;
            if (serviceTask.getOperationRef() != null && serviceTask.getImplementation() != null) {
                properties.put("serviceimplementation",
                               serviceTask.getImplementation());
                properties.put("serviceoperation",
                               serviceTask.getOperationRef().getName() == null ? serviceTask.getOperationRef().getImplementationRef() : serviceTask.getOperationRef().getName());
                if (def != null) {
                    List<RootElement> roots = def.getRootElements();
                    for (RootElement root : roots) {
                        if (root instanceof Interface) {
                            Interface inter = (Interface) root;
                            List<Operation> interOperations = inter.getOperations();
                            for (Operation interOper : interOperations) {
                                if (interOper.getId().equals(serviceTask.getOperationRef().getId())) {
                                    properties.put("serviceinterface",
                                                   inter.getName() == null ? inter.getImplementationRef() : inter.getName());
                                }
                            }
                        }
                    }
                }
            }
        } else if (task instanceof ManualTask) {
            taskType = "Manual";
        } else if (task instanceof UserTask) {
            taskType = "User";
            // get the user task actors
            List<ResourceRole> roles = task.getResources();
            StringBuilder sb = new StringBuilder();
            for (ResourceRole role : roles) {
                if (role instanceof PotentialOwner) {
                    FormalExpression fe = (FormalExpression) ((PotentialOwner) role).getResourceAssignmentExpression().getExpression();
                    if (fe.getBody() != null && fe.getBody().length() > 0) {
                        sb.append(fe.getBody());
                        sb.append(",");
                    }
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            properties.put("actors",
                           sb.toString());
        } else if (task instanceof SendTask) {
            taskType = "Send";
            SendTask st = (SendTask) task;
            setMessageRefProperties(properties,
                                    st.getMessageRef());
        } else if (task instanceof ReceiveTask) {
            taskType = "Receive";
            ReceiveTask rt = (ReceiveTask) task;
            setMessageRefProperties(properties,
                                    rt.getMessageRef());
        }

        // custom async
        String customAsyncMetaData = Utils.getMetaDataValue(task.getExtensionValues(),
                                                            "customAsync");
        String customAsync = (customAsyncMetaData != null && customAsyncMetaData.length() > 0) ? customAsyncMetaData : "false";
        properties.put("isasync",
                       customAsync);

        // custom autostart
        String customAutoStartMetaData = Utils.getMetaDataValue(task.getExtensionValues(),
                                                                "customAutoStart");
        String customAutoStart = (customAutoStartMetaData != null && customAutoStartMetaData.length() > 0) ? customAutoStartMetaData : "false";
        properties.put("customautostart",
                       customAutoStart);

        // custom SLA due date
        marshalCustomSLADueDateMetadata(task,
                                        properties);

        // backwards compatibility with jbds editor
        boolean foundTaskName = false;
        if (task instanceof UserTask && task.getIoSpecification() != null && task.getIoSpecification().getDataInputs() != null) {
            List<DataInput> taskDataInputs = task.getIoSpecification().getDataInputs();
            for (DataInput din : taskDataInputs) {
                if (din.getName() != null && din.getName().equals("TaskName")) {
                    List<DataInputAssociation> taskDataInputAssociations = task.getDataInputAssociations();
                    for (DataInputAssociation dia : taskDataInputAssociations) {
                        if (dia.getTargetRef() != null && dia.getTargetRef().getId().equals(din.getId()) &&
                                dia.getAssignment() != null && !dia.getAssignment().isEmpty() &&
                                dia.getAssignment().get(0).getFrom() != null) {
                            properties.put("taskname",
                                           ((FormalExpression) dia.getAssignment().get(0).getFrom()).getBody());
                            foundTaskName = true;
                        }
                    }
                    break;
                }
            }
        }

        if (!foundTaskName) {
            // try the drools specific attribute set on the task
            Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
            while (iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if (entry.getEStructuralFeature().getName().equals("taskName")) {
                    String tname = (String) entry.getValue();
                    if (tname != null && tname.length() > 0) {
                        properties.put("taskname",
                                       tname);
                    }
                }
            }
        }

        // check if we are dealing with a custom task
        boolean isCustomElement = isCustomElement((String) properties.get("taskname"),
                                                  preProcessingData);
        if (isCustomElement) {
            properties.put("tasktype",
                           properties.get("taskname"));
        } else {
            properties.put("tasktype",
                           taskType);
        }

        // multiple instance
        if (task.getLoopCharacteristics() != null) {
            properties.put("multipleinstance",
                           "true");
            MultiInstanceLoopCharacteristics taskmi = (MultiInstanceLoopCharacteristics) task.getLoopCharacteristics();
            if (taskmi.getLoopDataInputRef() != null) {
                ItemAwareElement iedatainput = taskmi.getLoopDataInputRef();

                List<DataInputAssociation> taskInputAssociations = task.getDataInputAssociations();
                for (DataInputAssociation dia : taskInputAssociations) {
                    if (dia.getTargetRef().equals(iedatainput)) {
                        properties.put("multipleinstancecollectioninput",
                                       dia.getSourceRef().get(0).getId());
                        break;
                    }
                }
            }
            if (taskmi.getLoopDataOutputRef() != null) {
                ItemAwareElement iedataoutput = taskmi.getLoopDataOutputRef();

                List<DataOutputAssociation> taskOutputAssociations = task.getDataOutputAssociations();
                for (DataOutputAssociation dout : taskOutputAssociations) {
                    if (dout.getSourceRef().get(0).equals(iedataoutput)) {
                        properties.put("multipleinstancecollectionoutput",
                                       dout.getTargetRef().getId());
                        break;
                    }
                }
            }

            if (taskmi.getInputDataItem() != null && taskmi.getInputDataItem().getItemSubjectRef() != null) {
                List<DataInput> taskDataInputs = task.getIoSpecification().getDataInputs();
                for (DataInput din : taskDataInputs) {
                    if (din != null && din.getItemSubjectRef() != null && taskmi.getInputDataItem() != null && taskmi.getInputDataItem().getItemSubjectRef() != null) {
                        if (din.getItemSubjectRef().getId().equals(taskmi.getInputDataItem().getItemSubjectRef().getId())) {
                            properties.put("multipleinstancedatainput",
                                           din.getName());
                        }
                    }
                }
            }

            if (taskmi.getOutputDataItem() != null && taskmi.getOutputDataItem().getItemSubjectRef() != null) {
                List<DataOutput> taskDataOutputs = task.getIoSpecification().getDataOutputs();
                for (DataOutput dout : taskDataOutputs) {
                    if (dout != null && dout.getItemSubjectRef() != null && taskmi.getOutputDataItem() != null && taskmi.getOutputDataItem().getItemSubjectRef() != null) {
                        if (dout.getItemSubjectRef().getId().equals(taskmi.getOutputDataItem().getItemSubjectRef().getId())) {
                            properties.put("multipleinstancedataoutput",
                                           dout.getName());
                        }
                    }
                }
            }

            if (taskmi.getCompletionCondition() != null) {
                try {
                    if (taskmi.getCompletionCondition() instanceof FormalExpression) {
                        properties.put("multipleinstancecompletioncondition",
                                       ((FormalExpression) taskmi.getCompletionCondition()).getBody());
                    }
                } catch (Exception e) {
                    _logger.info("Could not find multipleinstancecompletioncondition for: " + taskmi);
                }
            }
        } else {
            properties.put("multipleinstance",
                           "false");
        }

        // data inputs
        List<String> disallowedInputs = new ArrayList<String>();
        disallowedInputs.add("miinputCollection");
        if ((task instanceof UserTask) || isCustomElement) {
            disallowedInputs.add("TaskName");
        }
        marshallDataInputSet(task,
                             properties,
                             disallowedInputs);

        DataInput groupDataInput = null;
        DataInput skippableDataInput = null;
        DataInput commentDataInput = null;
        DataInput descriptionDataInput = null;
        DataInput contentDataInput = null;
        DataInput priorityDataInput = null;
        DataInput localeDataInput = null;
        DataInput createdByDataInput = null;
        DataInput notCompletedReassignInput = null;
        DataInput notStartedReassignInput = null;
        DataInput notCompletedNotificationInput = null;
        DataInput notStartedNotificationInput = null;
        if (task.getIoSpecification() != null) {
            List<InputSet> inputSetList = task.getIoSpecification().getInputSets();
            for (InputSet inset : inputSetList) {
                List<DataInput> dataInputList = inset.getDataInputRefs();
                for (DataInput dataIn : dataInputList) {
                    // dont add "TaskName" as that is added manually
                    String dataInName = dataIn.getName();

                    if (task instanceof UserTask && dataInName != null) {
                        if (dataInName.equals("GroupId")) {
                            groupDataInput = dataIn;
                        } else if (dataInName.equals("Skippable")) {
                            skippableDataInput = dataIn;
                        } else if (dataInName.equals("Comment")) {
                            commentDataInput = dataIn;
                        } else if (dataInName.equals("Description")) {
                            descriptionDataInput = dataIn;
                        } else if (dataInName.equals("Content")) {
                            contentDataInput = dataIn;
                        } else if (dataInName.equals("Priority")) {
                            priorityDataInput = dataIn;
                        } else if (dataInName.equals("Locale")) {
                            localeDataInput = dataIn;
                        } else if (dataInName.equals("CreatedBy")) {
                            createdByDataInput = dataIn;
                        } else if (dataInName.equals("NotCompletedReassign")) {
                            notCompletedReassignInput = dataIn;
                        } else if (dataInName.equals("NotStartedReassign")) {
                            notStartedReassignInput = dataIn;
                        } else if (dataInName.equals("NotCompletedNotify")) {
                            notCompletedNotificationInput = dataIn;
                        } else if (dataInName.equals("NotStartedNotify")) {
                            notStartedNotificationInput = dataIn;
                        }
                    }
                }
            }
        }

        // data outputs
        marshallDataOutputSet(task,
                              properties,
                              Arrays.asList("mioutputCollection"));

        // assignments
        StringBuilder associationBuff = new StringBuilder();
        List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = task.getDataOutputAssociations();
        List<String> uniDirectionalAssociations = new ArrayList<String>();
        //List<String> biDirectionalAssociations = new ArrayList<String>();

        for (DataInputAssociation datain : inputAssociations) {

            boolean proceed = true;
            if (task.getLoopCharacteristics() != null) {
                MultiInstanceLoopCharacteristics taskMultiLoop = (MultiInstanceLoopCharacteristics) task.getLoopCharacteristics();
                // dont include associations that include mi loop data inputs
                if (taskMultiLoop.getInputDataItem() != null && taskMultiLoop.getInputDataItem().getId() != null) {
                    if (datain.getSourceRef() != null && datain.getSourceRef().size() > 0 && datain.getSourceRef().get(0).getId().equals(taskMultiLoop.getInputDataItem().getId())) {
                        proceed = false;
                    }
                }

                // dont include associations that include loopDataInputRef as target
                if (taskMultiLoop.getLoopDataInputRef() != null) {
                    if (datain.getTargetRef().equals(taskMultiLoop.getLoopDataInputRef())) {
                        proceed = false;
                    }
                }
            }

            if (proceed) {
                String lhsAssociation = "";
                if (datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
                    if (datain.getTransformation() != null && datain.getTransformation().getBody() != null) {
                        lhsAssociation = datain.getTransformation().getBody();
                    } else {
                        lhsAssociation = datain.getSourceRef().get(0).getId();
                    }
                }

                String rhsAssociation = "";
                if (datain.getTargetRef() != null) {
                    rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
                }

                //boolean isBiDirectional = false;
                boolean isAssignment = false;

                if (datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                    isAssignment = true;
                }
                //            else {
                //                // check if this is a bi-directional association
                //                for(DataOutputAssociation dataout : outputAssociations) {
                //                    if(dataout.getTargetRef().getId().equals(lhsAssociation) &&
                //                       ((DataOutput) dataout.getSourceRef().get(0)).getName().equals(rhsAssociation)) {
                //                        isBiDirectional = true;
                //                        break;
                //                    }
                //                }
                //            }

                if (isAssignment) {
                    // only know how to deal with formal expressions
                    if (datain.getAssignment().get(0).getFrom() instanceof FormalExpression) {
                        String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
                        if (associationValue == null) {
                            associationValue = "";
                        }

                        // Handle properties that have their independent input editors
                        if (isCustomElement((String) properties.get("taskname"),
                                            preProcessingData)) {
                            if (!(rhsAssociation.equals("TaskName"))) {
                                // for custom tasks (workitems) we do not want to modify assiciation value, just encode it
                                // JBPM-7462
                                String replacer = encodeAssociationValue(associationValue);
                                associationBuff.append("[din]" + rhsAssociation).append("=").append(replacer);
                                associationBuff.append(",");

                                properties.put(rhsAssociation.toLowerCase(),
                                               associationValue);
                            }
                        } else {
                            if (!(task instanceof UserTask) ||
                                    !(rhsAssociation.equals("GroupId") ||
                                            rhsAssociation.equals("Skippable") ||
                                            rhsAssociation.equals("Comment") ||
                                            rhsAssociation.equals("Description") ||
                                            rhsAssociation.equals("Priority") ||
                                            rhsAssociation.equals("Content") ||
                                            rhsAssociation.equals("TaskName") ||
                                            rhsAssociation.equals("Locale") ||
                                            rhsAssociation.equals("CreatedBy") ||
                                            rhsAssociation.equals("NotCompletedReassign") ||
                                            rhsAssociation.equals("NotStartedReassign") ||
                                            rhsAssociation.equals("NotCompletedNotify") ||
                                            rhsAssociation.equals("NotStartedNotify")
                                    )) {
                                String replacer = encodeAssociationValue(associationValue);
                                associationBuff.append("[din]" + updateDataInputOutputDashes(rhsAssociation)).append("=").append(replacer);
                                associationBuff.append(",");

                                properties.put(rhsAssociation.toLowerCase(),
                                               associationValue);
                            }
                        }

                        if (rhsAssociation.equalsIgnoreCase("TaskName")) {
                            properties.put("taskname",
                                           associationValue);
                        }

                        if (task instanceof UserTask && datain.getAssignment().get(0).getTo() != null &&
                                ((FormalExpression) datain.getAssignment().get(0).getTo()).getBody() != null &&
                                datain.getAssignment().get(0).getFrom() != null
                                ) {
                            String toBody = ((FormalExpression) datain.getAssignment().get(0).getTo()).getBody();
                            String fromBody = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
                            if (toBody != null) {
                                if (groupDataInput != null && toBody.equals(groupDataInput.getId())) {
                                    properties.put("groupid",
                                                   fromBody == null ? "" : fromBody);
                                } else if (skippableDataInput != null && toBody.equals(skippableDataInput.getId())) {
                                    properties.put("skippable",
                                                   fromBody);
                                } else if (commentDataInput != null && toBody.equals(commentDataInput.getId())) {
                                    properties.put("subject",
                                                   fromBody);
                                } else if (descriptionDataInput != null && toBody.equals(descriptionDataInput.getId())) {
                                    properties.put("description",
                                                   fromBody);
                                } else if (priorityDataInput != null && toBody.equals(priorityDataInput.getId())) {
                                    properties.put("priority",
                                                   fromBody == null ? "" : fromBody);
                                } else if (contentDataInput != null && toBody.equals(contentDataInput.getId())) {
                                    properties.put("content",
                                                   fromBody);
                                } else if (localeDataInput != null && toBody.equals(localeDataInput.getId())) {
                                    properties.put("locale",
                                                   fromBody);
                                } else if (createdByDataInput != null && toBody.equals(createdByDataInput.getId())) {
                                    properties.put("createdby",
                                                   fromBody);
                                } else if (notCompletedReassignInput != null && toBody.equals(notCompletedReassignInput.getId())) {
                                    properties.put("tmpreassignmentnotcompleted",
                                                   updateReassignmentAndNotificationInput(fromBody,
                                                                                          "not-completed"));
                                } else if (notStartedReassignInput != null && toBody.equals(notStartedReassignInput.getId())) {
                                    properties.put("tmpreassignmentnotstarted",
                                                   updateReassignmentAndNotificationInput(fromBody,
                                                                                          "not-started"));
                                } else if (notCompletedNotificationInput != null && toBody.equals(notCompletedNotificationInput.getId())) {
                                    properties.put("tmpnotificationnotcompleted",
                                                   updateReassignmentAndNotificationInput(fromBody,
                                                                                          "not-completed"));
                                } else if (notStartedNotificationInput != null && toBody.equals(notStartedNotificationInput.getId())) {
                                    properties.put("tmpnotificationnotstarted",
                                                   updateReassignmentAndNotificationInput(fromBody,
                                                                                          "not-started"));
                                }
                            }
                        }
                    }
                }
                //            else if(isBiDirectional) {
                //                associationBuff.append(lhsAssociation).append("<->").append(rhsAssociation);
                //                associationBuff.append(",");
                //                biDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
                //            }
                else {
                    if (lhsAssociation != null && lhsAssociation.length() > 0) {
                        associationBuff.append("[din]" + lhsAssociation).append("->").append(updateDataInputOutputDashes(rhsAssociation));
                        associationBuff.append(",");
                        // Handle properties that have their independent input editors
                        if (isCustomElement((String) properties.get("taskname"),
                                            preProcessingData)) {
                            properties.put(rhsAssociation.toLowerCase(),
                                           lhsAssociation);
                        }
                    }
                    uniDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);

//                    if(contentDataInput != null) {
//                        if(rhsAssociation.equals(contentDataInput.getName())) {
//                            properties.put("content", lhsAssociation);
//                        }
//                    }
                }
            }
        }

        if (properties.get("tmpreassignmentnotcompleted") != null && ((String) properties.get("tmpreassignmentnotcompleted")).length() > 0 && properties.get("tmpreassignmentnotstarted") != null && ((String) properties.get("tmpreassignmentnotstarted")).length() > 0) {
            properties.put("reassignment",
                           properties.get("tmpreassignmentnotcompleted") + "^" + properties.get("tmpreassignmentnotstarted"));
        } else if (properties.get("tmpreassignmentnotcompleted") != null && ((String) properties.get("tmpreassignmentnotcompleted")).length() > 0) {
            properties.put("reassignment",
                           properties.get("tmpreassignmentnotcompleted"));
        } else if (properties.get("tmpreassignmentnotstarted") != null && ((String) properties.get("tmpreassignmentnotstarted")).length() > 0) {
            properties.put("reassignment",
                           properties.get("tmpreassignmentnotstarted"));
        }

        if (properties.get("tmpnotificationnotcompleted") != null && ((String) properties.get("tmpnotificationnotcompleted")).length() > 0 && properties.get("tmpnotificationnotstarted") != null && ((String) properties.get("tmpnotificationnotstarted")).length() > 0) {
            properties.put("notifications",
                           properties.get("tmpnotificationnotcompleted") + "^" + properties.get("tmpnotificationnotstarted"));
        } else if (properties.get("tmpnotificationnotcompleted") != null && ((String) properties.get("tmpnotificationnotcompleted")).length() > 0) {
            properties.put("notifications",
                           properties.get("tmpnotificationnotcompleted"));
        } else if (properties.get("tmpnotificationnotstarted") != null && ((String) properties.get("tmpnotificationnotstarted")).length() > 0) {
            properties.put("notifications",
                           properties.get("tmpnotificationnotstarted"));
        }

        for (DataOutputAssociation dataout : outputAssociations) {
            boolean proceed = true;
            if (task.getLoopCharacteristics() != null) {
                MultiInstanceLoopCharacteristics taskMultiLoop = (MultiInstanceLoopCharacteristics) task.getLoopCharacteristics();
                // dont include associations that include mi loop data outputs

                if (taskMultiLoop.getOutputDataItem() != null && taskMultiLoop.getOutputDataItem().getId() != null) {
                    if (dataout.getTargetRef().getId().equals(taskMultiLoop.getOutputDataItem().getId())) {
                        proceed = false;
                    }
                }
                // dont include associations that include loopDataOutputRef as source
                if (taskMultiLoop.getLoopDataOutputRef() != null) {
                    if (dataout.getSourceRef().get(0).equals(taskMultiLoop.getLoopDataOutputRef())) {
                        proceed = false;
                    }
                }
            }

            if (proceed) {
                if (dataout.getSourceRef().size() > 0) {
                    String lhsAssociation = ((DataOutput) dataout.getSourceRef().get(0)).getName();
                    String rhsAssociation = dataout.getTargetRef().getId();

                    boolean wasBiDirectional = false;
                    // check if we already addressed this association as bidirectional
                    //                for(String bda : biDirectionalAssociations) {
                    //                    String[] dbaparts = bda.split( ",\\s*" );
                    //                    if(dbaparts[0].equals(rhsAssociation) && dbaparts[1].equals(lhsAssociation)) {
                    //                        wasBiDirectional = true;
                    //                        break;
                    //                    }
                    //                }

                    if (dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                        rhsAssociation = encodeAssociationValue(dataout.getTransformation().getBody());
                    }

                    if (!wasBiDirectional) {
                        if (lhsAssociation != null && lhsAssociation.length() > 0) {
                            associationBuff.append("[dout]" + updateDataInputOutputDashes(lhsAssociation)).append("->").append(rhsAssociation);
                            associationBuff.append(",");
                        }
                    }
                }
            }
        }

        String assignmentString = associationBuff.toString();
        if (assignmentString.endsWith(",")) {
            assignmentString = assignmentString.substring(0,
                                                          assignmentString.length() - 1);
        }
        properties.put("assignments",
                       assignmentString);

        // on-entry and on-exit actions
        marshallEntryExitActions(task,
                                 properties);

        // simulation properties
        setSimulationProperties(task.getId(),
                                properties);

        // marshall the node out
        if (isCustomElement((String) properties.get("taskname"),
                            preProcessingData)) {
            marshallNode(task,
                         properties,
                         (String) properties.get("taskname"),
                         plane,
                         generator,
                         xOffset,
                         yOffset);
        } else {
            marshallNode(task,
                         properties,
                         "Task",
                         plane,
                         generator,
                         xOffset,
                         yOffset);
        }
    }

    private void marshallEntryExitActions(BaseElement element,
                                          Map<String, Object> properties) {
        if (element.getExtensionValues() != null && element.getExtensionValues().size() > 0) {

            String onEntryStr = "";
            String onExitStr = "";
            for (ExtensionAttributeValue extattrval : element.getExtensionValues()) {

                FeatureMap extensionElements = extattrval.getValue();

                @SuppressWarnings("unchecked")
                List<OnEntryScriptType> onEntryExtensions = (List<OnEntryScriptType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT,
                             true);

                @SuppressWarnings("unchecked")
                List<OnExitScriptType> onExitExtensions = (List<OnExitScriptType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT,
                             true);

                for (OnEntryScriptType onEntryScript : onEntryExtensions) {
                    if (onEntryScript.getScript() != null) {
                        onEntryStr += onEntryScript.getScript().replace("\\",
                                                                        "\\\\").replace("\n",
                                                                                        "\\n");
                        if (!onEntryStr.endsWith("\\n")) {
                            onEntryStr += "\n";
                        }

                        if (onEntryScript.getScriptFormat() != null) {
                            String format = onEntryScript.getScriptFormat();
                            String formatToWrite = getScriptLanguageFormat(format);
                            properties.put("script_language",
                                           formatToWrite);
                        }
                    }
                }

                for (OnExitScriptType onExitScript : onExitExtensions) {
                    if (onExitScript.getScript() != null) {
                        onExitStr += onExitScript.getScript().replace("\\",
                                                                      "\\\\").replace("\n",
                                                                                      "\\n");
                        if (!onExitStr.endsWith("\\n")) {
                            onExitStr += "\n";
                        }

                        if (onExitScript.getScriptFormat() != null) {
                            String format = onExitScript.getScriptFormat();
                            String formatToWrite = getScriptLanguageFormat(format);
                            if (properties.get("script_language") == null) {
                                properties.put("script_language",
                                               formatToWrite);
                            }
                        }
                    }
                }
            }
            if (onEntryStr.length() > 0) {
                if (onEntryStr.endsWith("|")) {
                    onEntryStr = onEntryStr.substring(0,
                                                      onEntryStr.length() - 1);
                }
                properties.put("onentryactions",
                               onEntryStr);
            }
            if (onExitStr.length() > 0) {
                if (onExitStr.endsWith("|")) {
                    onExitStr = onExitStr.substring(0,
                                                    onExitStr.length() - 1);
                }
                properties.put("onexitactions",
                               onExitStr);
            }
        }
    }

    private String getScriptLanguageFormat(String fullQualifiedFormat) {
        return getScriptLanguageFormat(fullQualifiedFormat, "java");
    }

    private String getScriptLanguageFormat(String fullQualifiedFormat, String defaultLanguage) {
        if (fullQualifiedFormat == null) {
            return "";
        } else if (fullQualifiedFormat.equals("http://www.java.com/java")) {
            return "java";
        } else if (fullQualifiedFormat.equals("http://www.mvel.org/2.0")) {
            return "mvel";
        } else if (fullQualifiedFormat.equals("http://www.javascript.com/javascript")) {
            return "javascript";
        } else if (fullQualifiedFormat.equals("http://www.jboss.org/drools/rule")) {
            return "drools";
        } else if (fullQualifiedFormat.equals("http://www.omg.org/spec/FEEL/20140401")) {
            return "FEEL";
        } else {
            return defaultLanguage;
        }
    }

    private void marshallDataInputSet(Activity activity,
                                      Map<String, Object> properties) {
        marshallDataInputSet(activity,
                             properties,
                             new ArrayList<String>());
    }

    private void marshallDataInputSet(Activity activity,
                                      Map<String, Object> properties,
                                      List<String> disallowedNames) {
        if (activity.getIoSpecification() != null) {
            List<InputSet> inputSetList = activity.getIoSpecification().getInputSets();
            StringBuilder dataInBuffer = new StringBuilder();
            for (InputSet inset : inputSetList) {
                List<DataInput> dataInputList = inset.getDataInputRefs();
                marshallItemAwareElements(activity,
                                          dataInputList,
                                          dataInBuffer,
                                          disallowedNames);
            }
            if (dataInBuffer.length() > 0) {
                dataInBuffer.setLength(dataInBuffer.length() - 1);
            }
            properties.put("datainputset",
                           dataInBuffer.toString());
        }
    }

    private void marshallDataOutputSet(Activity activity,
                                       Map<String, Object> properties) {
        marshallDataOutputSet(activity,
                              properties,
                              new ArrayList<String>());
    }

    private void marshallDataOutputSet(Activity activity,
                                       Map<String, Object> properties,
                                       List<String> disallowedNames) {
        if (activity.getIoSpecification() != null) {
            List<OutputSet> outputSetList = activity.getIoSpecification().getOutputSets();
            StringBuilder dataOutBuffer = new StringBuilder();
            for (OutputSet outset : outputSetList) {
                List<DataOutput> dataOutputList = outset.getDataOutputRefs();
                marshallItemAwareElements(activity,
                                          dataOutputList,
                                          dataOutBuffer,
                                          disallowedNames);
            }
            if (dataOutBuffer.length() > 0) {
                dataOutBuffer.setLength(dataOutBuffer.length() - 1);
            }
            properties.put("dataoutputset",
                           dataOutBuffer.toString());
        }
    }

    private void marshallItemAwareElements(Activity activity,
                                           List<? extends ItemAwareElement> elements,
                                           StringBuilder buffer,
                                           List<String> disallowedNames) {
        for (ItemAwareElement element : elements) {
            String name = null;
            if (element instanceof DataInput) {
                name = ((DataInput) element).getName();
            }
            if (element instanceof DataOutput) {
                name = ((DataOutput) element).getName();
            }
            if (name != null && !name.isEmpty() && !disallowedNames.contains(name)) {
                buffer.append(name);
                if (element.getItemSubjectRef() != null && element.getItemSubjectRef().getStructureRef() != null && !element.getItemSubjectRef().getStructureRef().isEmpty()) {
                    buffer.append(":").append(element.getItemSubjectRef().getStructureRef());
                } else if (activity.eContainer() instanceof SubProcess) {
                    // BZ1247105: for Outputs on Tasks inside sub-processes
                    String dtype = getAnyAttributeValue(element,
                                                        "dtype");
                    if (dtype != null && !dtype.isEmpty()) {
                        buffer.append(":").append(dtype);
                    }
                }
                buffer.append(",");
            }
        }
    }

    protected void marshallParallelGateway(ParallelGateway gateway,
                                           BPMNPlane plane,
                                           JsonGenerator generator,
                                           float xOffset,
                                           float yOffset,
                                           Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        marshallNode(gateway,
                     flowElementProperties,
                     "ParallelGateway",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallExclusiveGateway(ExclusiveGateway gateway,
                                            BPMNPlane plane,
                                            JsonGenerator generator,
                                            float xOffset,
                                            float yOffset,
                                            Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        if (gateway.getDefault() != null) {
            SequenceFlow defsf = gateway.getDefault();
            String defGatewayStr = "";
            if (defsf.getName() != null && defsf.getName().length() > 0) {
                defGatewayStr = defsf.getName() + " : " + defsf.getId();
            } else {
                defGatewayStr = defsf.getId();
            }
            flowElementProperties.put("defaultgate",
                                      defGatewayStr);
        }
        marshallNode(gateway,
                     flowElementProperties,
                     "Exclusive_Databased_Gateway",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallInclusiveGateway(InclusiveGateway gateway,
                                            BPMNPlane plane,
                                            JsonGenerator generator,
                                            float xOffset,
                                            float yOffset,
                                            Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        if (gateway.getDefault() != null) {
            SequenceFlow defsf = gateway.getDefault();
            String defGatewayStr = "";
            if (defsf.getName() != null && defsf.getName().length() > 0) {
                defGatewayStr = defsf.getName() + " : " + defsf.getId();
            } else {
                defGatewayStr = defsf.getId();
            }
            flowElementProperties.put("defaultgate",
                                      defGatewayStr);
        }
        marshallNode(gateway,
                     flowElementProperties,
                     "InclusiveGateway",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallEventBasedGateway(EventBasedGateway gateway,
                                             BPMNPlane plane,
                                             JsonGenerator generator,
                                             float xOffset,
                                             float yOffset,
                                             Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        marshallNode(gateway,
                     flowElementProperties,
                     "EventbasedGateway",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallComplexGateway(ComplexGateway gateway,
                                          BPMNPlane plane,
                                          JsonGenerator generator,
                                          float xOffset,
                                          float yOffset,
                                          Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        marshallNode(gateway,
                     flowElementProperties,
                     "ComplexGateway",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallNode(FlowNode node,
                                Map<String, Object> properties,
                                String stencil,
                                BPMNPlane plane,
                                JsonGenerator generator,
                                float xOffset,
                                float yOffset) throws JsonGenerationException, IOException {
        if (properties == null) {
            properties = new LinkedHashMap<String, Object>();
        }
        if (node.getDocumentation() != null && node.getDocumentation().size() > 0) {
            properties.put("documentation",
                           node.getDocumentation().get(0).getText());
        }
        if (node.getName() != null) {
            properties.put("name",
                           unescapeXml(node.getName()));
        } else {
            if (node instanceof TextAnnotation) {
                if (((TextAnnotation) node).getText() != null) {
                    properties.put("name",
                                   ((TextAnnotation) node).getText());
                } else {
                    properties.put("name",
                                   "");
                }
            } else {
                properties.put("name",
                               "");
            }
        }
        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(node.getExtensionValues(),
                                                    "elementname");
        if (elementName != null) {
            properties.put("name",
                           elementName);
        }

        marshallProperties(properties,
                           generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                                   stencil);
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        for (SequenceFlow outgoing : node.getOutgoing()) {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       outgoing.getId());
            generator.writeEndObject();
        }
        // we need to also add associations as outgoing elements
        Process process = (Process) plane.getBpmnElement();
        writeAssociations(process,
                          node.getId(),
                          generator);
        // and boundary events for activities
        List<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
        findBoundaryEvents(process,
                           boundaryEvents);
        for (BoundaryEvent be : boundaryEvents) {
            if (be.getAttachedToRef().getId().equals(node.getId())) {
                generator.writeStartObject();
                generator.writeObjectField("resourceId",
                                           be.getId());
                generator.writeEndObject();
            }
        }

        generator.writeEndArray();

        // boundary events have a docker
        if (node instanceof BoundaryEvent) {
            Iterator<FeatureMap.Entry> iter = node.getAnyAttribute().iterator();
            boolean foundDockerInfo = false;
            while (iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if (entry.getEStructuralFeature().getName().equals("dockerinfo")) {
                    foundDockerInfo = true;
                    String dockerInfoStr = String.valueOf(entry.getValue());
                    if (dockerInfoStr != null && dockerInfoStr.length() > 0) {
                        if (dockerInfoStr.endsWith("|")) {
                            dockerInfoStr = dockerInfoStr.substring(0,
                                                                    dockerInfoStr.length() - 1);
                            String[] dockerInfoParts = dockerInfoStr.split("\\|");
                            String infoPartsToUse = dockerInfoParts[0];
                            String[] infoPartsToUseParts = infoPartsToUse.split("\\^");
                            if (infoPartsToUseParts != null && infoPartsToUseParts.length > 0) {
                                generator.writeArrayFieldStart("dockers");
                                generator.writeStartObject();
                                generator.writeObjectField("x",
                                                           Double.valueOf(infoPartsToUseParts[0]));
                                generator.writeObjectField("y",
                                                           Double.valueOf(infoPartsToUseParts[1]));
                                generator.writeEndObject();
                                generator.writeEndArray();
                            }
                        }
                    }
                }
            }

            // backwards compatibility to older versions -- BZ 1196259
            if (!foundDockerInfo) {
                // find the edge associated with this boundary event
                for (DiagramElement element : plane.getPlaneElement()) {
                    if (element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == node) {
                        List<Point> waypoints = ((BPMNEdge) element).getWaypoint();
                        if (waypoints != null && waypoints.size() > 0) {
                            // one per boundary event
                            Point p = waypoints.get(0);
                            if (p != null) {
                                applyResolutionForDockers(p);
                                generator.writeArrayFieldStart("dockers");
                                generator.writeStartObject();
                                generator.writeObjectField("x",
                                                           p.getX());
                                generator.writeObjectField("y",
                                                           p.getY());
                                generator.writeEndObject();
                                generator.writeEndArray();
                            }
                        }
                    } else if(element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() instanceof BoundaryEvent) {
                        Bounds boundaryEventBounds = ((BPMNShape) findDiagramElement(plane,
                                                                                     ((BPMNShape) element).getBpmnElement())).getBounds();
                        generator.writeArrayFieldStart("dockers");
                        generator.writeStartObject();
                        generator.writeObjectField("x",
                                                   boundaryEventBounds.getWidth() / 2);
                        generator.writeObjectField("y",
                                                   boundaryEventBounds.getHeight() / 2);
                        generator.writeEndObject();
                        generator.writeEndArray();
                    }
                }
            }
        }

        BPMNShape shape = (BPMNShape) findDiagramElement(plane,
                                                         node);
        Bounds bounds = shape.getBounds();
        correctEventNodeSize(shape);
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x",
                                   bounds.getX() + bounds.getWidth() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() + bounds.getHeight() - yOffset);
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x",
                                   bounds.getX() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() - yOffset);
        generator.writeEndObject();
        generator.writeEndObject();
    }


    private void correctEventNodeSize(BPMNShape shape) {
        BaseElement element = shape.getBpmnElement();
        if (element instanceof Event) {
//            // do not "fix" events as they shape is circle - leave bounds as is
//          Bounds bounds = shape.getBounds();
//            float width = bounds.getWidth();
//            float height = bounds.getHeight();
//            if (width != 30 || height != 30) {
//                bounds.setWidth(30);
//                bounds.setHeight(30);
//                float x = bounds.getX();
//                float y = bounds.getY();
//                x = x - ((30 - width)/2);
//                y = y - ((30 - height)/2);
//                bounds.setX(x);
//                bounds.setY(y);
//            }
        } else if (element instanceof Gateway) {
            Bounds bounds = shape.getBounds();
            float width = bounds.getWidth();
            float height = bounds.getHeight();
            if (width != 40 || height != 40) {
                bounds.setWidth(40);
                bounds.setHeight(40);
                float x = bounds.getX();
                float y = bounds.getY();
                x = x - ((40 - width) / 2);
                y = y - ((40 - height) / 2);
                bounds.setX(x);
                bounds.setY(y);
            }
        }
    }

    protected void marshallDataObject(DataObject dataObject,
                                      BPMNPlane plane,
                                      JsonGenerator generator,
                                      float xOffset,
                                      float yOffset,
                                      Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
        if (dataObject.getDocumentation() != null && dataObject.getDocumentation().size() > 0) {
            properties.put("documentation",
                           dataObject.getDocumentation().get(0).getText());
        }
        if (dataObject.getName() != null && dataObject.getName().length() > 0) {
            properties.put("name",
                           unescapeXml(dataObject.getName()));
        } else {
            // we need a name, use id instead
            properties.put("name",
                           dataObject.getId());
        }

        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(dataObject.getExtensionValues(),
                                                    "elementname");
        if (elementName != null) {
            properties.put("name",
                           elementName);
        }

        if (dataObject.getItemSubjectRef().getStructureRef() != null && dataObject.getItemSubjectRef().getStructureRef().length() > 0) {
            if (defaultTypesList.contains(dataObject.getItemSubjectRef().getStructureRef())) {
                properties.put("standardtype",
                               dataObject.getItemSubjectRef().getStructureRef());
            } else {
                properties.put("customtype",
                               dataObject.getItemSubjectRef().getStructureRef());
            }
        }

        Association outgoingAssociaton = findOutgoingAssociation(plane,
                                                                 dataObject);
        Association incomingAssociation = null;

        Process process = (Process) plane.getBpmnElement();
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getTargetRef() == dataObject) {
                    incomingAssociation = association;
                }
            }
        }

        if (outgoingAssociaton != null && incomingAssociation == null) {
            properties.put("input_output",
                           "Input");
        }

        if (outgoingAssociaton == null && incomingAssociation != null) {
            properties.put("input_output",
                           "Output");
        }

        marshallProperties(properties,
                           generator);

        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                                   "DataObject");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");

        List<Association> associations = findOutgoingAssociations(plane,
                                                                  dataObject);
        if (associations != null) {
            for (Association as : associations) {
                generator.writeStartObject();
                generator.writeObjectField("resourceId",
                                           as.getId());
                generator.writeEndObject();
            }
        }

        generator.writeEndArray();

        Bounds bounds = ((BPMNShape) findDiagramElement(plane,
                                                        dataObject)).getBounds();
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x",
                                   bounds.getX() + bounds.getWidth() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() + bounds.getHeight() - yOffset);
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x",
                                   bounds.getX() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() - yOffset);
        generator.writeEndObject();
        generator.writeEndObject();
    }

    protected void marshallSubProcess(SubProcess subProcess,
                                      BPMNPlane plane,
                                      JsonGenerator generator,
                                      float xOffset,
                                      float yOffset,
                                      String preProcessingData,
                                      Definitions def,
                                      Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
        if (subProcess.getName() != null) {
            properties.put("name",
                           unescapeXml(subProcess.getName()));
        } else {
            properties.put("name",
                           "");
        }

        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(subProcess.getExtensionValues(),
                                                    "elementname");
        if (elementName != null) {
            properties.put("name",
                           elementName);
        }

        if (subProcess.getDocumentation() != null && subProcess.getDocumentation().size() > 0) {
            properties.put("documentation",
                           subProcess.getDocumentation().get(0).getText());
        }

        if (subProcess instanceof AdHocSubProcess) {
            AdHocSubProcess ahsp = (AdHocSubProcess) subProcess;
            if (ahsp.getOrdering().equals(AdHocOrdering.PARALLEL)) {
                properties.put("adhocordering",
                               "Parallel");
            } else if (ahsp.getOrdering().equals(AdHocOrdering.SEQUENTIAL)) {
                properties.put("adhocordering",
                               "Sequential");
            } else {
                // default to parallel
                properties.put("adhocordering",
                               "Parallel");
            }
            if (ahsp.getCompletionCondition() != null) {
                try {
                    FormalExpression completionExpression = (FormalExpression) ahsp.getCompletionCondition();
                    if (completionExpression != null) {
                        properties.put("adhoccompletioncondition",
                                       completionExpression.getBody().replaceAll("\n",
                                                                                 "\\\\n"));
                        if (completionExpression.getLanguage() != null) {
                            String completionLanguage = getScriptLanguageFormat(completionExpression.getLanguage());
                            properties.put("script_language",
                                           completionLanguage);
                        }
                    }
                } catch (Exception e) {
                    _logger.info("Could not find adhoccompletioncondition for: " + ahsp);
                }
            }
            
            final String customActivationCondition = Utils.getMetaDataValue(ahsp.getExtensionValues(),
                    "customActivationCondition");
            if (customActivationCondition != null && customActivationCondition.length() > 0) {
                properties.put("adhocactivationcondition", customActivationCondition);
            }
        }

        // custom async
        String customAsyncMetaData = Utils.getMetaDataValue(subProcess.getExtensionValues(),
                                                            "customAsync");
        String customAsync = (customAsyncMetaData != null && customAsyncMetaData.length() > 0) ? customAsyncMetaData : "false";
        properties.put("isasync",
                       customAsync);

        // custom SLA due date
        marshalCustomSLADueDateMetadata(subProcess,
                                        properties);

        // data inputs
        marshallDataInputSet(subProcess,
                             properties);

        // data outputs
        marshallDataOutputSet(subProcess,
                              properties);

        // assignments
        StringBuilder associationBuff = new StringBuilder();
        List<DataInputAssociation> inputAssociations = subProcess.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = subProcess.getDataOutputAssociations();

        marshallDataInputAssociations(associationBuff,
                                      inputAssociations);
        marshallDataOutputAssociations(associationBuff,
                                       outputAssociations);

        String assignmentString = associationBuff.toString();
        if (assignmentString.endsWith(",")) {
            assignmentString = assignmentString.substring(0,
                                                          assignmentString.length() - 1);
        }
        properties.put("assignments",
                       assignmentString);

        // on-entry and on-exit actions
        marshallEntryExitActions(subProcess,
                                 properties);

        // loop characteristics
        boolean haveValidLoopCharacteristics = false;
        if (subProcess.getLoopCharacteristics() != null && subProcess.getLoopCharacteristics() instanceof MultiInstanceLoopCharacteristics) {
            haveValidLoopCharacteristics = true;
            properties.put("mitrigger",
                           "true");
            MultiInstanceLoopCharacteristics taskmi = (MultiInstanceLoopCharacteristics) subProcess.getLoopCharacteristics();
            if (taskmi.getLoopDataInputRef() != null) {
                ItemAwareElement iedatainput = taskmi.getLoopDataInputRef();

                List<DataInputAssociation> taskInputAssociations = subProcess.getDataInputAssociations();
                for (DataInputAssociation dia : taskInputAssociations) {
                    if (dia.getTargetRef().equals(iedatainput)) {
                        properties.put("multipleinstancecollectioninput",
                                       dia.getSourceRef().get(0).getId());
                        break;
                    }
                }
            }
            if (taskmi.getLoopDataOutputRef() != null) {
                ItemAwareElement iedataoutput = taskmi.getLoopDataOutputRef();

                List<DataOutputAssociation> taskOutputAssociations = subProcess.getDataOutputAssociations();
                for (DataOutputAssociation dout : taskOutputAssociations) {
                    if (dout.getSourceRef().get(0).equals(iedataoutput)) {
                        properties.put("multipleinstancecollectionoutput",
                                       dout.getTargetRef().getId());
                        break;
                    }
                }
            }

            if (taskmi.getInputDataItem() != null) {
                List<DataInput> taskDataInputs = subProcess.getIoSpecification().getDataInputs();
                for (DataInput din : taskDataInputs) {

                    if (din.getItemSubjectRef() == null) {
                        // for backward compatibility as the where only input supported
                        properties.put("multipleinstancedatainput",
                                       taskmi.getInputDataItem().getId());
                    }
                    if (din.getItemSubjectRef() != null && din.getItemSubjectRef().getId().equals(taskmi.getInputDataItem().getItemSubjectRef().getId())) {
                        properties.put("multipleinstancedatainput",
                                       din.getName());
                        break;
                    }
                }
            }

            if (taskmi.getOutputDataItem() != null) {
                List<DataOutput> taskDataOutputs = subProcess.getIoSpecification().getDataOutputs();
                for (DataOutput dout : taskDataOutputs) {
                    if (dout.getItemSubjectRef() == null) {
                        properties.put("multipleinstancedataoutput",
                                       taskmi.getOutputDataItem().getId());
                        break;
                    }

                    if (dout.getItemSubjectRef() != null && dout.getItemSubjectRef().getId().equals(taskmi.getOutputDataItem().getItemSubjectRef().getId())) {
                        properties.put("multipleinstancedataoutput",
                                       dout.getName());
                        break;
                    }
                }
            }

            if (taskmi.getCompletionCondition() != null) {
                try {
                    if (taskmi.getCompletionCondition() instanceof FormalExpression) {
                        properties.put("multipleinstancecompletioncondition",
                                       ((FormalExpression) taskmi.getCompletionCondition()).getBody());
                    }
                } catch (Exception e) {
                    _logger.info("Could not find multipleinstancecompletioncondition for : " + taskmi);
                }
            }
        }

        // properties
        List<Property> processProperties = subProcess.getProperties();
        if (processProperties != null && processProperties.size() > 0) {
            String propVal = "";
            for (int i = 0; i < processProperties.size(); i++) {
                Property p = processProperties.get(i);

                String pKPI = Utils.getMetaDataValue(p.getExtensionValues(),
                                                     "customKPI");

                propVal += p.getId();
                // check the structureRef value
                if (p.getItemSubjectRef() != null && p.getItemSubjectRef().getStructureRef() != null) {
                    propVal += ":" + p.getItemSubjectRef().getStructureRef();
                }
                if (pKPI != null && pKPI.length() > 0) {
                    propVal += ":" + pKPI;
                }
                if (i != processProperties.size() - 1) {
                    propVal += ",";
                }
            }
            properties.put("vardefs",
                           propVal);
        }

        // simulation properties
        setSimulationProperties(subProcess.getId(),
                                properties);

        marshallProperties(properties,
                           generator);
        generator.writeObjectFieldStart("stencil");
        if (subProcess instanceof AdHocSubProcess) {
            generator.writeObjectField("id",
                                       "AdHocSubprocess");
        } else {
            if (subProcess.isTriggeredByEvent()) {
                generator.writeObjectField("id",
                                           "EventSubprocess");
            } else {
                if (haveValidLoopCharacteristics) {
                    generator.writeObjectField("id",
                                               "MultipleInstanceSubprocess");
                } else {
                    generator.writeObjectField("id",
                                               "Subprocess");
                }
            }
        }
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        Bounds bounds = ((BPMNShape) findDiagramElement(plane,
                                                        subProcess)).getBounds();
        for (FlowElement flowElement : subProcess.getFlowElements()) {
            if (coordianteManipulation) {
                marshallFlowElement(flowElement,
                                    plane,
                                    generator,
                                    bounds.getX(),
                                    bounds.getY(),
                                    preProcessingData,
                                    def);
            } else {
                marshallFlowElement(flowElement,
                                    plane,
                                    generator,
                                    0,
                                    0,
                                    preProcessingData,
                                    def);
            }
        }
        for (Artifact artifact : subProcess.getArtifacts()) {
            if (coordianteManipulation) {
                marshallArtifact(artifact,
                                 plane,
                                 generator,
                                 bounds.getX(),
                                 bounds.getY(),
                                 preProcessingData,
                                 def);
            } else {
                marshallArtifact(artifact,
                                 plane,
                                 generator,
                                 0,
                                 0,
                                 preProcessingData,
                                 def);
            }
        }
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        for (BoundaryEvent boundaryEvent : subProcess.getBoundaryEventRefs()) {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       boundaryEvent.getId());
            generator.writeEndObject();
        }
        for (SequenceFlow outgoing : subProcess.getOutgoing()) {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       outgoing.getId());
            generator.writeEndObject();
        }
        Process process = (Process) plane.getBpmnElement();
        writeAssociations(process,
                          subProcess.getId(),
                          generator);
        // subprocess boundary events
        List<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
        findBoundaryEvents(process,
                           boundaryEvents);
        for (BoundaryEvent be : boundaryEvents) {
            if (be.getAttachedToRef().getId().equals(subProcess.getId())) {
                generator.writeStartObject();
                generator.writeObjectField("resourceId",
                                           be.getId());
                generator.writeEndObject();
            }
        }

        generator.writeEndArray();
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x",
                                   bounds.getX() + bounds.getWidth() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() + bounds.getHeight() - yOffset);
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x",
                                   bounds.getX() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() - yOffset);
        generator.writeEndObject();
        generator.writeEndObject();
    }

    private void writeAssociations(Process process,
                                   String elementId,
                                   JsonGenerator generator) throws IOException {
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getSourceRef().getId().equals(elementId)) {
                    generator.writeStartObject();
                    generator.writeObjectField("resourceId",
                                               association.getId());
                    generator.writeEndObject();
                }
            }
        }
    }

    private void marshallDataOutputAssociations(StringBuilder associationBuff,
                                                List<DataOutputAssociation> outputAssociations) {
        if (outputAssociations != null) {
            for (DataOutputAssociation dataout : outputAssociations) {
                if (dataout.getSourceRef().size() > 0) {
                    String lhsAssociation = ((DataOutput) dataout.getSourceRef().get(0)).getName();
                    String rhsAssociation = dataout.getTargetRef().getId();

                    if (dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                        rhsAssociation = encodeAssociationValue(dataout.getTransformation().getBody());
                    }

                    if (lhsAssociation != null && lhsAssociation.length() > 0) {
                        associationBuff.append("[dout]" + updateDataInputOutputDashes(lhsAssociation)).append("->").append(rhsAssociation);
                        associationBuff.append(",");
                    }
                }
            }
        }
    }

    private void marshallDataInputAssociations(StringBuilder associationBuff,
                                               List<DataInputAssociation> inputAssociations) {
        if (inputAssociations != null) {
            for (DataInputAssociation datain : inputAssociations) {
                String lhsAssociation = "";
                if (datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
                    if (datain.getTransformation() != null && datain.getTransformation().getBody() != null) {
                        lhsAssociation = datain.getTransformation().getBody();
                    } else {
                        lhsAssociation = datain.getSourceRef().get(0).getId();
                    }
                }

                String rhsAssociation = "";
                if (datain.getTargetRef() != null) {
                    rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
                }

                //boolean isBiDirectional = false;
                boolean isAssignment = false;

                if (datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                    isAssignment = true;
                }

                if (isAssignment) {
                    // only know how to deal with formal expressions
                    if (datain.getAssignment().get(0).getFrom() instanceof FormalExpression) {
                        String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
                        if (associationValue == null) {
                            associationValue = "";
                        }
                        String replacer = encodeAssociationValue(updateDataInputOutputDashes(associationValue));
                        associationBuff.append("[din]" + rhsAssociation).append("=").append(replacer);
                        associationBuff.append(",");
                    }
                } else {
                    if (lhsAssociation != null && lhsAssociation.length() > 0) {
                        associationBuff.append("[din]" + lhsAssociation).append("->").append(updateDataInputOutputDashes(rhsAssociation));
                        associationBuff.append(",");
                    }
                }
            }
        }
    }

    protected void marshallSequenceFlow(SequenceFlow sequenceFlow,
                                        BPMNPlane plane,
                                        JsonGenerator generator,
                                        float xOffset,
                                        float yOffset) throws JsonGenerationException, IOException {
        // dont marshal "dangling" sequence flow..better to just omit than fail
        if (sequenceFlow.getSourceRef() == null || sequenceFlow.getTargetRef() == null) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        // check null for sequence flow name
        if (sequenceFlow.getName() != null && !"".equals(sequenceFlow.getName())) {
            properties.put("name",
                           unescapeXml(sequenceFlow.getName()));
        } else {
            properties.put("name",
                           "");
        }
        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(sequenceFlow.getExtensionValues(),
                                                    "elementname");
        if (elementName != null) {
            properties.put("name",
                           elementName);
        }

        if (sequenceFlow.getDocumentation() != null && sequenceFlow.getDocumentation().size() > 0) {
            properties.put("documentation",
                           sequenceFlow.getDocumentation().get(0).getText());
        }

        if (sequenceFlow.isIsImmediate()) {
            properties.put("isimmediate",
                           "true");
        } else {
            properties.put("isimmediate",
                           "false");
        }

        Expression conditionExpression = sequenceFlow.getConditionExpression();
        try {
            if (conditionExpression instanceof FormalExpression) {
                if (((FormalExpression) conditionExpression).getBody() != null) {
                    properties.put("conditionexpression",
                                   ((FormalExpression) conditionExpression).getBody().replaceAll("\n",
                                                                                                 "\\\\n"));
                }
                if (((FormalExpression) conditionExpression).getLanguage() != null) {
                    String cd = ((FormalExpression) conditionExpression).getLanguage();
                    String cdStr = getScriptLanguageFormat(cd, "mvel");

                    properties.put("conditionexpressionlanguage",
                                   cdStr);
                }
            }
        } catch (Exception e) {
            _logger.info("Could not find conditionexpression for : " + conditionExpression);
        }

        boolean foundBgColor = false;
        boolean foundBrColor = false;
        boolean foundFontColor = false;
        boolean foundSelectable = false;
        Iterator<FeatureMap.Entry> iter = sequenceFlow.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("priority")) {
                String priorityStr = String.valueOf(entry.getValue());
                if (priorityStr != null) {
                    try {
                        Integer priorityInt = Integer.parseInt(priorityStr);
                        if (priorityInt >= 1) {
                            properties.put("priority",
                                           entry.getValue());
                        } else {
                            _logger.error("Priority must be equal or greater than 1.");
                        }
                    } catch (NumberFormatException e) {
                        _logger.error("Priority must be a number.");
                    }
                }
            }
            if (entry.getEStructuralFeature().getName().equals("background-color") || entry.getEStructuralFeature().getName().equals("bgcolor")) {
                properties.put("bgcolor",
                               entry.getValue());
                foundBgColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("border-color") || entry.getEStructuralFeature().getName().equals("bordercolor")) {
                properties.put("bordercolor",
                               entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("fontsize")) {
                properties.put("fontsize",
                               entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("color") || entry.getEStructuralFeature().getName().equals("fontcolor")) {
                properties.put("fontcolor",
                               entry.getValue());
                foundFontColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("selectable")) {
                properties.put("isselectable",
                               entry.getValue());
                foundSelectable = true;
            }
        }
        if (!foundBgColor) {
            properties.put("bgcolor",
                           defaultSequenceflowColor);
        }

        if (!foundBrColor) {
            properties.put("bordercolor",
                           defaultSequenceflowColor);
        }

        if (!foundFontColor) {
            properties.put("fontcolor",
                           defaultSequenceflowColor);
        }

        if (!foundSelectable) {
            properties.put("isselectable",
                           "true");
        }

        // simulation properties
        setSimulationProperties(sequenceFlow.getId(),
                                properties);

        marshallProperties(properties,
                           generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                                   "SequenceFlow");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();

        generator.writeArrayFieldStart("outgoing");
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                                   sequenceFlow.getTargetRef().getId());
        generator.writeEndObject();
        generator.writeEndArray();

        Bounds sourceBounds = ((BPMNShape) findDiagramElement(plane,
                                                              sequenceFlow.getSourceRef())).getBounds();
        Bounds targetBounds = ((BPMNShape) findDiagramElement(plane,
                                                              sequenceFlow.getTargetRef())).getBounds();
        generator.writeArrayFieldStart("dockers");
        generator.writeStartObject();
        generator.writeObjectField("x",
                                   sourceBounds.getWidth() / 2);
        generator.writeObjectField("y",
                                   sourceBounds.getHeight() / 2);
        generator.writeEndObject();
        List<Point> waypoints = ((BPMNEdge) findDiagramElement(plane,
                                                               sequenceFlow)).getWaypoint();
        for (int i = 1; i < waypoints.size() - 1; i++) {
            Point waypoint = waypoints.get(i);
            generator.writeStartObject();
            generator.writeObjectField("x",
                                       waypoint.getX());
            generator.writeObjectField("y",
                                       waypoint.getY());
            generator.writeEndObject();
        }

        generator.writeStartObject();
        generator.writeObjectField("x",
                                   targetBounds.getWidth() / 2);
        generator.writeObjectField("y",
                                   targetBounds.getHeight() / 2);
        generator.writeEndObject();
        generator.writeEndArray();
    }

    private DiagramElement findDiagramElement(BPMNPlane plane,
                                              BaseElement baseElement) {
        DiagramElement result = _diagramElements.get(baseElement.getId());
        if (result != null) {
            return result;
        }
        for (DiagramElement element : plane.getPlaneElement()) {
            if ((element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == baseElement) ||
                    (element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() == baseElement)) {
                _diagramElements.put(baseElement.getId(),
                                     element);
                return element;
            }
        }
        _logger.info("Could not find BPMNDI information for " + baseElement);
        return null;
    }

    protected void marshallGlobalTask(GlobalTask globalTask,
                                      JsonGenerator generator) {
        if (globalTask instanceof GlobalBusinessRuleTask) {

        } else if (globalTask instanceof GlobalManualTask) {

        } else if (globalTask instanceof GlobalScriptTask) {

        } else if (globalTask instanceof GlobalUserTask) {

        } else {

        }
    }

    protected void marshallGlobalChoreographyTask(GlobalChoreographyTask callableElement,
                                                  JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    protected void marshallConversation(Conversation callableElement,
                                        JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    protected void marshallChoreography(Choreography callableElement,
                                        JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    protected void marshallProperties(Map<String, Object> properties,
                                      JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeObjectFieldStart("properties");
        for (Entry<String, Object> entry : properties.entrySet()) {
            generator.writeObjectField(entry.getKey(),
                                       String.valueOf(entry.getValue()));
        }
        generator.writeEndObject();
    }

    protected void marshallArtifact(Artifact artifact,
                                    BPMNPlane plane,
                                    JsonGenerator generator,
                                    float xOffset,
                                    float yOffset,
                                    String preProcessingData,
                                    Definitions def) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                                   artifact.getId());
        if (artifact instanceof Association) {
            marshallAssociation((Association) artifact,
                                plane,
                                generator,
                                xOffset,
                                yOffset,
                                preProcessingData,
                                def);
        } else if (artifact instanceof Group) {
            marshallGroup((Group) artifact,
                          plane,
                          generator,
                          xOffset,
                          yOffset,
                          preProcessingData,
                          def);
        }
        generator.writeEndObject();
    }

    protected void marshallAssociation(Association association,
                                       BPMNPlane plane,
                                       JsonGenerator generator,
                                       float xOffset,
                                       float yOffset,
                                       String preProcessingData,
                                       Definitions def) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        Iterator<FeatureMap.Entry> iter = association.getAnyAttribute().iterator();
        boolean foundBrColor = false;
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("type")) {
                properties.put("type",
                               entry.getValue());
            }
            if (entry.getEStructuralFeature().getName().equals("bordercolor")) {
                properties.put("bordercolor",
                               entry.getValue());
                foundBrColor = true;
            }
        }
        if (!foundBrColor) {
            properties.put("bordercolor",
                           defaultSequenceflowColor);
        }
        if (association.getDocumentation() != null && association.getDocumentation().size() > 0) {
            properties.put("documentation",
                           association.getDocumentation().get(0).getText());
        }

        marshallProperties(properties,
                           generator);
        generator.writeObjectFieldStart("stencil");
        if (association.getAssociationDirection().equals(AssociationDirection.ONE)) {
            generator.writeObjectField("id",
                                       "Association_Unidirectional");
        } else if (association.getAssociationDirection().equals(AssociationDirection.BOTH)) {
            generator.writeObjectField("id",
                                       "Association_Bidirectional");
        } else {
            generator.writeObjectField("id",
                                       "Association_Undirected");
        }
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                                   association.getTargetRef().getId());
        generator.writeEndObject();
        generator.writeEndArray();

        Bounds sourceBounds = ((BPMNShape) findDiagramElement(plane,
                                                              association.getSourceRef())).getBounds();

        Bounds targetBounds = null;
        float tbx = 0;
        float tby = 0;
        if (findDiagramElement(plane,
                               association.getTargetRef()) instanceof BPMNShape) {
            targetBounds = ((BPMNShape) findDiagramElement(plane,
                                                           association.getTargetRef())).getBounds();
        } else if (findDiagramElement(plane,
                                      association.getTargetRef()) instanceof BPMNEdge) {
            // connect it to first waypoint on edge
            List<Point> waypoints = ((BPMNEdge) findDiagramElement(plane,
                                                                   association.getTargetRef())).getWaypoint();
            if (waypoints != null && waypoints.size() > 0) {
                tbx = waypoints.get(0).getX();
                tby = waypoints.get(0).getY();
            }
        }
        generator.writeArrayFieldStart("dockers");
        generator.writeStartObject();
        generator.writeObjectField("x",
                                   sourceBounds.getWidth() / 2);
        generator.writeObjectField("y",
                                   sourceBounds.getHeight() / 2);
        generator.writeEndObject();
        List<Point> waypoints = ((BPMNEdge) findDiagramElement(plane,
                                                               association)).getWaypoint();
        for (int i = 1; i < waypoints.size() - 1; i++) {
            Point waypoint = waypoints.get(i);
            generator.writeStartObject();
            generator.writeObjectField("x",
                                       waypoint.getX());
            generator.writeObjectField("y",
                                       waypoint.getY());
            generator.writeEndObject();
        }
        if (targetBounds != null) {
            generator.writeStartObject();
            // text annotations have to be treated specia
            if (association.getTargetRef() instanceof TextAnnotation) {
                generator.writeObjectField("x",
                                           1);
                generator.writeObjectField("y",
                                           targetBounds.getHeight() / 2);
            } else {
                generator.writeObjectField("x",
                                           targetBounds.getWidth() / 2);
                generator.writeObjectField("y",
                                           targetBounds.getHeight() / 2);
            }
            generator.writeEndObject();
            generator.writeEndArray();
        } else {
            generator.writeStartObject();
            generator.writeObjectField("x",
                                       tbx);
            generator.writeObjectField("y",
                                       tby);
            generator.writeEndObject();
            generator.writeEndArray();
        }
    }

    protected void marshallTextAnnotation(TextAnnotation textAnnotation,
                                          BPMNPlane plane,
                                          JsonGenerator generator,
                                          float xOffset,
                                          float yOffset,
                                          String preProcessingData,
                                          Definitions def,
                                          Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        flowElementProperties.put("name",
                                  textAnnotation.getText());
        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(textAnnotation.getExtensionValues(),
                                                    "elementname");
        if (elementName != null) {
            flowElementProperties.put("name",
                                      elementName);
        }

        if (textAnnotation.getDocumentation() != null && textAnnotation.getDocumentation().size() > 0) {
            flowElementProperties.put("documentation",
                                      textAnnotation.getDocumentation().get(0).getText());
        }
        flowElementProperties.put("artifacttype",
                                  "Annotation");
        marshallNode(textAnnotation,
                     flowElementProperties,
                     "TextAnnotation",
                     plane,
                     generator,
                     xOffset,
                     yOffset);
    }

    protected void marshallGroup(Group group,
                                 BPMNPlane plane,
                                 JsonGenerator generator,
                                 float xOffset,
                                 float yOffset,
                                 String preProcessingData,
                                 Definitions def) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<>();
        if (group.getCategoryValueRef() != null && group.getCategoryValueRef().getValue() != null) {
            properties.put("name",
                           unescapeXml(group.getCategoryValueRef().getValue()));
        }
        Documentation doc = getDocumentation(group);
        if (doc != null) {
            properties.put("documentation",
                           doc.getText());
        }

        marshallProperties(properties,
                           generator);

        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                                   "Group");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();

        generator.writeArrayFieldStart("outgoing");
        if (findOutgoingAssociation(plane,
                                    group) != null) {
            generator.writeStartObject();
            generator.writeObjectField("resourceId",
                                       findOutgoingAssociation(plane,
                                                               group).getId());
            generator.writeEndObject();
        }
        generator.writeEndArray();

        Bounds bounds = ((BPMNShape) findDiagramElement(plane,
                                                        group)).getBounds();
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x",
                                   bounds.getX() + bounds.getWidth() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() + bounds.getHeight() - yOffset);
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x",
                                   bounds.getX() - xOffset);
        generator.writeObjectField("y",
                                   bounds.getY() - yOffset);
        generator.writeEndObject();
        generator.writeEndObject();
    }

    private Documentation getDocumentation(BaseElement element) {
        if (element.getDocumentation() != null && element.getDocumentation().size() > 0) {
            return element.getDocumentation().get(0);
        } else {
            return null;
        }
    }

    protected Association findOutgoingAssociation(BPMNPlane plane,
                                                  BaseElement baseElement) {
        Association result = _diagramAssociations.get(baseElement.getId());
        if (result != null) {
            return result;
        }
        if (!(plane.getBpmnElement() instanceof Process)) {
            throw new IllegalArgumentException("Don't know how to get associations from a non-Process Diagram");
        }

        Process process = (Process) plane.getBpmnElement();
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getSourceRef() == baseElement) {
                    _diagramAssociations.put(baseElement.getId(),
                                             association);
                    return association;
                }
            }
        }
        return null;
    }

    protected List<Association> findOutgoingAssociations(BPMNPlane plane,
                                                         BaseElement baseElement) {
        List<Association> retList = new ArrayList<Association>();
        if (!(plane.getBpmnElement() instanceof Process)) {
            throw new IllegalArgumentException("Don't know how to get associations from a non-Process Diagram");
        }

        Process process = (Process) plane.getBpmnElement();
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getSourceRef() == baseElement) {
                    retList.add(association);
                }
            }
        }
        return retList;
    }

    protected void marshallStencil(String stencilId,
                                   JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                                   stencilId);
        generator.writeEndObject();
    }

    private boolean isCustomElement(String taskType,
                                    String preProcessingData) {
        if (taskType != null && taskType.length() > 0 && preProcessingData != null && preProcessingData.length() > 0) {
            String[] preProcessingDataElements = preProcessingData.split(",\\s*");
            for (String preProcessingDataElement : preProcessingDataElements) {
                if (taskType.equals(preProcessingDataElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String updateReassignmentAndNotificationInput(String inputStr,
                                                          String type) {
        if (inputStr != null && inputStr.length() > 0) {
            String ret = "";
            String[] parts = inputStr.split("\\^\\s*");
            for (String nextPart : parts) {
                ret += nextPart + "@" + type + "^";
            }
            if (ret.endsWith("^")) {
                ret = ret.substring(0,
                                    ret.length() - 1);
            }
            return ret;
        } else {
            return "";
        }
    }

    private void findBoundaryEvents(FlowElementsContainer flc,
                                    List<BoundaryEvent> boundaryList) {
        for (FlowElement fl : flc.getFlowElements()) {
            if (fl instanceof BoundaryEvent) {
                boundaryList.add((BoundaryEvent) fl);
            }

            if (fl instanceof FlowElementsContainer) {
                findBoundaryEvents((FlowElementsContainer) fl,
                                   boundaryList);
            }
        }
    }

    private String getAnyAttributeValue(BaseElement el,
                                        String attrName) {
        if (el == null || attrName == null || attrName.isEmpty()) {
            return null;
        }
        if (el.getAnyAttribute() != null && el.getAnyAttribute().size() > 0) {
            Iterator<FeatureMap.Entry> iter = el.getAnyAttribute().iterator();
            while (iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if (attrName.equals(entry.getEStructuralFeature().getName())) {
                    return entry.getValue().toString();
                }
            }
        }
        return null;
    }

    private String encodeAssociationValue(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLEncoder.encode(s,
                                     "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private void extractCostParamsToProperties(ElementParameters eleType,
                                               Map<String, Object> properties) {
        CostParameters costParams = eleType.getCostParameters();
        if (costParams != null) {
            if (costParams.getUnitCost() != null) {
                extractParamTypeToProperties("unitcost",
                                             costParams.getUnitCost().getParameterValue(),
                                             properties);
            }
        }
    }

    private void extractTimeParamsToProperties(ElementParameters eleType,
                                               Map<String, Object> properties) {
        TimeParameters timeParams = eleType.getTimeParameters();
        if (timeParams != null) {
            Parameter processingTime = timeParams.getProcessingTime();
            if (processingTime != null && processingTime.getParameterValue() != null && processingTime.getParameterValue().size() > 0) {
                ParameterValue paramValue = processingTime.getParameterValue().get(0);
                if (paramValue instanceof NormalDistributionType) {
                    NormalDistributionType ndt = (NormalDistributionType) paramValue;
                    properties.put("mean",
                                   ndt.getMean());
                    properties.put("standarddeviation",
                                   ndt.getStandardDeviation());
                    properties.put("distributiontype",
                                   "normal");
                } else if (paramValue instanceof UniformDistributionType) {
                    UniformDistributionType udt = (UniformDistributionType) paramValue;
                    properties.put("min",
                                   udt.getMin());
                    properties.put("max",
                                   udt.getMax());
                    properties.put("distributiontype",
                                   "uniform");
                } else if (paramValue instanceof PoissonDistributionType) {
                    PoissonDistributionType pdt = (PoissonDistributionType) paramValue;
                    properties.put("mean",
                                   pdt.getMean());
                    properties.put("distributiontype",
                                   "poisson");
                }

                if (timeParams.getWaitTime() != null) {
                    extractParamTypeToProperties("waittime",
                                                 timeParams.getWaitTime().getParameterValue(),
                                                 properties);
                }
            }
        }
    }

    private void extractControlParamsToProperties(ElementParameters eleType,
                                                  Map<String, Object> properties) {
        ControlParameters controlParams = eleType.getControlParameters();
        if (controlParams != null) {
            if (controlParams.getProbability() != null) {
                extractParamTypeToProperties("probability",
                                             controlParams.getProbability().getParameterValue(),
                                             properties);
            }
        }
    }

    private void extractResourceParamsToProperties(ElementParameters eleType,
                                                   Map<String, Object> properties) {
        ResourceParameters resourceParams = eleType.getResourceParameters();
        if (resourceParams != null) {
            if (resourceParams.getQuantity() != null) {
                extractParamTypeToProperties("quantity",
                                             resourceParams.getQuantity().getParameterValue(),
                                             properties);
            }
            if (resourceParams.getAvailability() != null) {
                extractParamTypeToProperties("workinghours",
                                             resourceParams.getAvailability().getParameterValue(),
                                             properties);
            }
        }
    }

    private void extractParamTypeToProperties(String paramName,
                                              EList<ParameterValue> parameterValues,
                                              Map<String, Object> properties) {
        if (parameterValues != null && parameterValues.size() > 0) {
            ParameterValue value = parameterValues.get(0);
            if (value != null && (value instanceof FloatingParameterType)) {
                properties.put(paramName,
                               ((FloatingParameterType) value).getValue());
            }
        }
    }

    private void setSimulationProperties(String elementId,
                                         Map<String, Object> properties) {
        if (_simulationScenario != null && _simulationScenario.getElementParameters() != null) {
            for (ElementParameters eleType : _simulationScenario.getElementParameters()) {
                if (eleType.getElementRef().equals(elementId)) {
                    extractTimeParamsToProperties(eleType,
                                                  properties);
                    extractCostParamsToProperties(eleType,
                                                  properties);
                    extractControlParamsToProperties(eleType,
                                                     properties);
                    extractResourceParamsToProperties(eleType,
                                                      properties);
                }
            }
        }
    }

    private String updateDataInputOutputDashes(String dataInputOutput) {
        return dataInputOutput.replaceAll("-",
                                          " ");
    }

    private List<EventDefinition> getEventDefinitionsForEvent(Event event) {
        List<EventDefinition> eventDefinitions = new ArrayList<>();

        if (event != null && event instanceof CatchEvent) {
            CatchEvent catchEvent = (CatchEvent) event;
            eventDefinitions.addAll(catchEvent.getEventDefinitions());
            eventDefinitions.addAll(catchEvent.getEventDefinitionRefs());
        } else if (event != null && event instanceof ThrowEvent) {
            ThrowEvent throwEvent = (ThrowEvent) event;
            eventDefinitions.addAll(throwEvent.getEventDefinitions());
            eventDefinitions.addAll(throwEvent.getEventDefinitionRefs());
        } else {
            _logger.warn("Unable to get event definitions for event: " + event);
        }

        return eventDefinitions;
    }

    private void marshalCustomSLADueDateMetadata(final BaseElement element,
                                                 final Map<String, Object> properties) {
        final String customSLADueDateMetaData = Utils.getMetaDataValue(element.getExtensionValues(),
                                                                       "customSLADueDate");
        if (customSLADueDateMetaData != null && customSLADueDateMetaData.length() > 0) {
            properties.put("customsladuedate", customSLADueDateMetaData);
        }
    }
}