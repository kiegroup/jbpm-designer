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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.ReceiveTask;
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
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.impl.BPMNEdgeImpl;
import org.eclipse.bpmn2.impl.DataInputImpl;
import org.eclipse.bpmn2.impl.DataOutputImpl;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.dd.di.Edge;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.impl.MetaDataTypeImpl;
import org.jbpm.designer.bpmn2.impl.helpers.SimpleEdge;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.junit.Test;

import static org.jbpm.designer.bpmn2.impl.helpers.SimpleEdge.createEdge;
import static org.junit.Assert.*;

/**
 * @author Antoine Toulme
 *         <p>
 *         A series of tests to check the unmarshalling of json to bpmn2.
 */
public class Bpmn2UnmarshallingTest {

    private Bpmn2Loader loader = new Bpmn2Loader(Bpmn2UnmarshallingTest.class);

    @Test
    public void testSimpleDefinitionsUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("empty.json");
        assertEquals("<![CDATA[my doc]]>",
                     definitions.getRootElements().get(0).getDocumentation().iterator().next().getText());
        assertEquals("http://www.w3.org/1999/XPath",
                     definitions.getExpressionLanguage());
        assertEquals("http://www.omg.org/bpmn20",
                     definitions.getTargetNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema",
                     definitions.getTypeLanguage());
        assertTrue(definitions.getRootElements().size() == 1);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testSimpleGlobalTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("oneTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof GlobalTask);
        GlobalTask task = (GlobalTask) definitions.getRootElements().iterator().next();
        assertEquals("oneTask",
                     task.getName());
        assertEquals("my task doc",
                     task.getDocumentation().iterator().next().getText());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testTwoGlobalTasksUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("twoTask.json");
        assertTrue(definitions.getRootElements().size() == 2);
        assertTrue(definitions.getRootElements().get(0) instanceof GlobalTask);
        GlobalTask task = (GlobalTask) definitions.getRootElements().get(0);
        assertEquals("firstTask",
                     task.getName());
        assertEquals("my task doc",
                     task.getDocumentation().iterator().next().getText());
        GlobalTask task2 = (GlobalTask) definitions.getRootElements().get(1);
        assertEquals("secondTask",
                     task2.getName());
        assertEquals("my task doc too",
                     task2.getDocumentation().iterator().next().getText());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testPoolUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("pool.json");
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = getRootProcess(definitions);
        assertEquals("pool",
                     process.getName());
        assertEquals(ProcessType.PRIVATE,
                     process.getProcessType());
        assertTrue(process.isIsClosed());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testLaneUnmarshallingOrdering() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("lane.json",
                                                             "true",
                                                             "true",
                                                             null);
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = getRootProcess(definitions);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("my first lane",
                     lane.getName());
        verifyBpmnShapePresent(lane,
                               definitions);
        Task task = (Task) process.getFlowElements().get(4);
        assertEquals("task",
                     task.getName());
        verifyBpmnShapePresent(task,
                               definitions);
        assertEquals(0,
                     getDIElementOrder(lane,
                                       definitions));
        assertTrue(getDIElementOrder(lane,
                                     definitions) < getDIElementOrder(task,
                                                                      definitions));
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testLaneUnmarshallingWithoutOrdering() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("lane.json",
                                                             "false",
                                                             "true",
                                                             null);
        Process process = getRootProcess(definitions);
        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        verifyBpmnShapePresent(lane,
                               definitions);
        Task task = (Task) process.getFlowElements().get(4);
        verifyBpmnShapePresent(task,
                               definitions);
        assertNotEquals(-1,
                        getDIElementOrder(task,
                                          definitions));
        assertNotEquals(0,
                        getDIElementOrder(lane,
                                          definitions));
        assertTrue(getDIElementOrder(lane,
                                     definitions) > getDIElementOrder(task,
                                                                      definitions));
    }

    @Test
    public void testNestedElementsOrdering() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("nestedElements.json",
                                                             "true",
                                                             "true",
                                                             null);
        Process process = getRootProcess(definitions);
        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals(0,
                     getDIElementOrder(lane,
                                       definitions));
        SubProcess subProcess = (SubProcess) getFlowElement(process.getFlowElements(),
                                                            "subprocess");
        assertNull(subProcess.getLoopCharacteristics());
        assertEquals(1,
                     getDIElementOrder(subProcess,
                                       definitions));
        UserTask task = (UserTask) getFlowElement(subProcess.getFlowElements(),
                                                  "task");
        assertEquals(2,
                     getDIElementOrder(task,
                                       definitions));
        BoundaryEvent event = (BoundaryEvent) getFlowElement(subProcess.getFlowElements(),
                                                             "boundary");
        assertEquals(3,
                     getDIElementOrder(event,
                                       definitions));
    }

    @Test
    public void testSequenceFlowUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("sequenceFlow.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof Task);
        Task task = (Task) process.getFlowElements().get(0);
        assertEquals("task1",
                     task.getName());
        Task task2 = (Task) process.getFlowElements().get(1);
        assertEquals("task2",
                     task2.getName());
        SequenceFlow flow = (SequenceFlow) process.getFlowElements().get(2);
        assertEquals("seqFlow",
                     flow.getName());
        assertEquals(task,
                     flow.getSourceRef());
        assertEquals(task2,
                     flow.getTargetRef());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
        assertEquals(1,
                     getBpmnEdges(flow,
                                  definitions).size());
    }

    @Test
    public void testScriptTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("scriptTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        FlowElement element = getFlowElement(process.getFlowElements(),
                                             "scriptTask");
        if (element == null || !(element instanceof ScriptTask)) {
            fail("Script task not found");
        }
        ScriptTask scriptTask = (ScriptTask) element;
        assertEquals("<![CDATA[System.out.println(\"xyz\");]]>",
                     scriptTask.getScript());
        assertEquals("http://www.java.com/java",
                     scriptTask.getScriptFormat());
        assertEquals("<![CDATA[Prints something to output]]>",
                     scriptTask.getDocumentation().get(0).getText());
        assertEquals("<![CDATA[scriptTask]]>",
                     getMetaDataValue(scriptTask.getExtensionValues(),
                                      "elementname"));
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(scriptTask.getExtensionValues(),
                                      "customAsync"));
        assertEquals("<![CDATA[System.out.println(\"entry\");]]>",
                     getOnEntryScript(scriptTask.getExtensionValues()));
        assertEquals("<![CDATA[System.out.println(\"exit\");]]>",
                     getOnExitScript(scriptTask.getExtensionValues()));
    }

    @Test
    public void testScriptTaskMvelScriptFormat() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("mvelScriptTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        FlowElement element = (ScriptTask) getFlowElement(process.getFlowElements(),
                                                          "mvelScriptTask");

        ScriptTask scriptTask = (ScriptTask) element;
        assertEquals("<![CDATA[m : Message( status == Message.HELLO )]]>",
                     scriptTask.getScript());
        assertEquals("http://www.mvel.org/2.0",
                     scriptTask.getScriptFormat());
    }

    @Test
    public void testBusinessRuleTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("businessRule.json");
        Process process = getRootProcess(definitions);
        FlowElement task = getFlowElement(process.getFlowElements(),
                                          "businessRuleTask");
        assertTrue(task instanceof BusinessRuleTask);
        BusinessRuleTask ruleTask = (BusinessRuleTask) task;
        verifyAttribute(ruleTask,
                        "ruleFlowGroup",
                        "simpleGroup");
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(task.getExtensionValues(),
                                      "customAsync"));
        assertEquals(RuleSetNode.DRL_LANG,
                     ruleTask.getImplementation());
    }

    @Test
    public void testReceiveTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("receiveTask.json");
        Process process = getRootProcess(definitions);
        FlowElement task = getFlowElement(process.getFlowElements(),
                                          "receiveTask");
        assertTrue(task instanceof ReceiveTask);
        ReceiveTask receiveTask = (ReceiveTask) task;
        verifyAttribute(receiveTask,
                        "msgref",
                        "parcel");
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(task.getExtensionValues(),
                                      "customAsync"));
    }

    @Test
    public void testSendTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("sendTask.json");
        Process process = getRootProcess(definitions);
        FlowElement task = getFlowElement(process.getFlowElements(),
                                          "sendTask");
        assertTrue(task instanceof SendTask);
        SendTask sendTask = (SendTask) task;
        verifyAttribute(sendTask,
                        "msgref",
                        "parcel");
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(task.getExtensionValues(),
                                      "customAsync"));
    }

    //@Test
    // removing until we start supporting global tasks
    public void testManualTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("manualTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalManualTask task = (GlobalManualTask) definitions.getRootElements().get(0);
        assertEquals("pull a lever",
                     task.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("gateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ExclusiveGateway g = (ExclusiveGateway) process.getFlowElements().get(0);
        assertEquals("xor gateway",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testParallelGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("parallelGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ParallelGateway g = (ParallelGateway) process.getFlowElements().get(0);
        assertEquals("parallel gateway",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEventBasedGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("eventBasedGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EventBasedGateway g = (EventBasedGateway) process.getFlowElements().get(0);
        assertEquals("event-based gateway",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testInclusiveGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("inclusiveGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        InclusiveGateway g = (InclusiveGateway) process.getFlowElements().get(0);
        assertEquals("inclusive gateway",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startMessageEvent.json");
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start message event",
                     g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start escalation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        EscalationEventDefinition escalationEventDefinition = (EscalationEventDefinition) g.getEventDefinitions().iterator().next();
        assertEquals("com.sample.escalation",
                     escalationEventDefinition.getEscalationRef().getEscalationCode());
        assertEquals("com.sample.escalation",
                     escalationEventDefinition.getEscalationRef().getName());
    }

    @Test
    public void testStartCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start compensation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start multiple event",
                     g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartParallelMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startParallelMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start parallel multiple event",
                     g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartSignalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start signal event",
                     g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testStartTimerEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startTimerEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start timer event",
                     g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testGroupUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("group.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        Group group = (Group) process.getArtifacts().iterator().next();
        assertEquals("Group name is wrong.",
                     group.getCategoryValueRef().getValue(),
                     "group");
        assertEquals(group.getDocumentation().get(0).getText(),
                     "<![CDATA[group documentation]]>");
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testTextAnnotationUnmarshalling() throws Exception {
        List<SimpleEdge> expectedEdges = new ArrayList<SimpleEdge>();
        expectedEdges.add(createEdge("<![CDATA[Start\nAnnotation]]>")
                                  .addPoint(120,
                                            320)
                                  .addPoint(170,
                                            160)
        );
        expectedEdges.add(createEdge("<![CDATA[Task\nIn\nLane\nAnnotation]]>")
                                  .addPoint(155,
                                            125)
                                  .addPoint(380,
                                            184)
                                  .addPoint(331,
                                            184)
                                  .addPoint(380,
                                            130)
        );
        expectedEdges.add(createEdge("<![CDATA[WID\nTask\nannotation]]>")
                                  .addPoint(690,
                                            650)
                                  .addPoint(690,
                                            507)
                                  .addPoint(791,
                                            507)
        );
        expectedEdges.add(createEdge("<![CDATA[User\nTask\nAnnotation]]>")
                                  .addPoint(100,
                                            76)
                                  .addPoint(196,
                                            646)
                                  .addPoint(245,
                                            550)
        );
        expectedEdges.add(createEdge("<![CDATA[Gateway\nin\nlane\nannotation]]>")
                                  .addPoint(270,
                                            125)
                                  .addPoint(409,
                                            125)
        );
        expectedEdges.add(createEdge("<![CDATA[End\nIn\nSwimlane]]>")
                                  .addPoint(270,
                                            36)
                                  .addPoint(915,
                                            231)
                                  .addPoint(964,
                                            133)
        );
        expectedEdges.add(createEdge("<![CDATA[Subprocess's\nAnnotation]]>")
                                  .addPoint(495,
                                            650)
                                  .addPoint(495,
                                            847)
                                  .addPoint(714,
                                            848)
        );
        expectedEdges.add(createEdge("<![CDATA[Swimlane's\nAnnotation]]>")
                                  .addPoint(525,
                                            320)
                                  .addPoint(1066,
                                            320)
                                  .addPoint(1115,
                                            505)
        );

        List<SimpleEdge> actualEdges = new ArrayList<SimpleEdge>();
        Definitions definitions = loader.loadProcessFromJson("textAnnotation.json");
        List<BPMNEdge> edges = getAllEdgesFromDefinition(definitions);
        for (Edge edge : edges) {
            SimpleEdge currentEdge = createEdge(getEdgeName(edge));
            for (Point p : edge.getWaypoint()) {
                currentEdge.addPoint(p.getX(),
                                     p.getY());
            }
            actualEdges.add(currentEdge);
        }

        assertEquals(expectedEdges,
                     actualEdges);
    }

    private String getEdgeName(Edge edge) {
        return ((MetaDataTypeImpl) ((Association) ((BPMNEdgeImpl) edge).getBpmnElement()).getTargetRef().getExtensionValues().get(0).getValue().get(0).getValue()).getMetaValue();
    }

    private List<BPMNEdge> getAllEdgesFromDefinition(Definitions definitions) {
        List<BPMNEdge> edges = new ArrayList<BPMNEdge>();
        List<DiagramElement> elements = getPlaneElementsFormDefinition(definitions);
        for (DiagramElement element : elements) {
            if (isAssociation(element)) {
                edges.add((BPMNEdge) element);
            }
        }
        return edges;
    }

    private List<DiagramElement> getPlaneElementsFormDefinition(Definitions definitions) {
        return definitions.getDiagrams().get(0).getPlane().getPlaneElement();
    }

    private boolean isEdge(DiagramElement element) {
        return element instanceof BPMNEdge;
    }

    private boolean isAssociation(DiagramElement element) {
        return isEdge(element) && ((BPMNEdgeImpl) element).getBpmnElement() instanceof Association;
    }

    @Test
    public void testDataObjectUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("dataObject.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().iterator().next() instanceof DataObject);
        DataObject da = (DataObject) process.getFlowElements().iterator().next();
        assertEquals("data object",
                     da.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endMessageEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end message event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end escalation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndErrorEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endErrorEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end error event",
                     g.getName());
        ErrorEventDefinition error = (ErrorEventDefinition) g.getEventDefinitions().iterator().next();
        assertEquals("com.sample.Error",
                     error.getErrorRef().getName());
        assertEquals("com.sample.Error",
                     error.getErrorRef().getErrorCode());
    }

    @Test
    public void testEndSignalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) getFlowElement(process.getFlowElements(),
                                               "EndSignalEvent");
        assertEquals("EndSignalEvent",
                     g.getName());
        SignalEventDefinition signalEventDefinition = (SignalEventDefinition) g.getEventDefinitions().iterator().next();
        Signal signal = (Signal) definitions.getRootElements().get(1);
        assertEquals(signal.getId(),
                     signalEventDefinition.getSignalRef());
        assertEquals("signalForTestPurposes",
                     signal.getName());
    }

    @Test
    public void testEndTerminateEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endTerminateEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("terminate end event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TerminateEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end multiple event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testEndCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end compensation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testSimpleChainUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEvent-task-endEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().size() == 5);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchMessageEvent.json");
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch message event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchTimerEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchTimerEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch timer event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch escalation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchConditionalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchConditionalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch conditional event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ConditionalEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchLinkEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchLinkEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch link event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchErrorEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchErrorEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch error event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchCancelEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchCancelEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch cancel event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CancelEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch compensation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch multiple event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchParallelMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchParallelMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch parallel multiple event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowMessageEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw message event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw escalation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowLinkEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowLinkEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw link event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowCompensationUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw compensation event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowSignalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw signal event",
                     g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowMultipleUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw multiple event",
                     g.getName());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testAssociationUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("association.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task",
                     g.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(g,
                     association.getSourceRef());
        assertEquals(textA,
                     association.getTargetRef());
        assertEquals(AssociationDirection.NONE,
                     association.getAssociationDirection());
        assertEquals(1,
                     getBpmnEdges(association,
                                  definitions).size());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testAssociationUnidirectionalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("associationOne.json");
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task",
                     g.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(g,
                     association.getSourceRef());
        assertEquals(textA,
                     association.getTargetRef());
        assertEquals(AssociationDirection.ONE,
                     association.getAssociationDirection());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testAssociationBidirectionalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("associationBoth.json");
        Process process = getRootProcess(definitions);
        Task task = (Task) process.getFlowElements().get(0);
        assertEquals("task",
                     task.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(task,
                     association.getSourceRef());
        assertEquals(textA,
                     association.getTargetRef());
        assertEquals(AssociationDirection.BOTH,
                     association.getAssociationDirection());
        definitions.eResource().save(System.out,
                                     Collections.emptyMap());
    }

    @Test
    public void testMultiLineNames() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("multiLineNames.json");

        Process process = getRootProcess(definitions);
        assertEquals("<![CDATA[my\nstart]]>",
                     getElementName(process,
                                    0));
        assertEquals("<![CDATA[my\nflow]]>",
                     getElementName(process,
                                    1));
        assertEquals("<![CDATA[my\ngate]]>",
                     getElementName(process,
                                    2));
        assertEquals("<![CDATA[my\nend]]>",
                     getElementName(process,
                                    3));
        assertEquals("<![CDATA[my\nterminate\nend]]>",
                     getElementName(process,
                                    5));
        assertEquals("<![CDATA[my\nthrowing\nmessage]]>",
                     getElementName(process,
                                    6));
        assertEquals("<![CDATA[my\nflow\nin\nlane]]>",
                     getElementName(process,
                                    9));
        assertEquals("<![CDATA[my\nsubprocess]]>",
                     getElementName(process,
                                    10));
        assertEquals("<![CDATA[my\nuser\ntask]]>",
                     getElementName(process,
                                    14));
        assertEquals("<![CDATA[my\nmessage]]>",
                     getElementName(process,
                                    15));

        FlowElementsContainer embeddedSubProcess = (FlowElementsContainer) process.getFlowElements().get(10);
        assertEquals("<![CDATA[my\nmessage\nstart]]>",
                     getElementName(embeddedSubProcess,
                                    0));
        assertEquals("<![CDATA[my\nmanual\ntask]]>",
                     getElementName(embeddedSubProcess,
                                    1));
        assertEquals("<![CDATA[my\ninner\nend]]>",
                     getElementName(embeddedSubProcess,
                                    2));
        assertEquals("<![CDATA[my\nflow\nin\nsubprocess]]>",
                     getElementName(embeddedSubProcess,
                                    3));
        assertEquals("<![CDATA[my\nescalation\nevent]]>",
                     getElementName(embeddedSubProcess,
                                    5));
        assertNull(((SubProcess) embeddedSubProcess).getLoopCharacteristics());

        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        FlowElementsContainer adHocSubProcess = (FlowElementsContainer) lane.getFlowNodeRefs().get(0);
        assertEquals("<![CDATA[my\nlane]]>",
                     getMetaDataValue(lane.getExtensionValues(),
                                      "elementname"));
        assertEquals("<![CDATA[my\nadhoc\nsubprocess]]>",
                     getMetaDataValue(adHocSubProcess.getExtensionValues(),
                                      "elementname"));
        assertEquals("<![CDATA[my\ntask\nin\nadhoc]]>",
                     getElementName(adHocSubProcess,
                                    0));
        assertEquals("<![CDATA[my\nmessage\nin\nsubprocess\nin\nlane]]>",
                     getElementName(adHocSubProcess,
                                    1));
        assertEquals("<![CDATA[my\nuser\ntask]]>",
                     getMetaDataValue(lane.getFlowNodeRefs().get(1).getExtensionValues(),
                                      "elementname"));
        assertEquals("<![CDATA[my\nmessage]]>",
                     getMetaDataValue(lane.getFlowNodeRefs().get(2).getExtensionValues(),
                                      "elementname"));
        assertNull(((AdHocSubProcess) adHocSubProcess).getLoopCharacteristics());
    }

    private String getElementName(FlowElementsContainer container,
                                  int elementNumber) {
        BaseElement element = container.getFlowElements().get(elementNumber);
        return getMetaDataValue(element.getExtensionValues(),
                                "elementname");
    }

    @Test
    public void testFindContainerForBoundaryEvent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("boundaryEventsContainers.json");
        Process process = getRootProcess(definitions);

        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        boolean foundTimer1 = false;
        boolean foundTimer2 = false;
        boolean foundTimer3 = false;

        for (FlowElement element : process.getFlowElements()) {
            if (element instanceof BoundaryEvent) {
                BoundaryEvent be = (BoundaryEvent) element;
                if ("Timer3".equals(element.getName())) {
                    Process sp = (Process) unmarshaller.findContainerForBoundaryEvent(process,
                                                                                      be);
                    assertEquals("DemoProcess",
                                 sp.getName());
                    verifyAttribute(be,
                                    "dockerinfo",
                                    "17.0^70.0|");
                    foundTimer3 = true;
                }
            }
        }

        SubProcess subProcessOne = (SubProcess) getFlowElement(process.getFlowElements(),
                                                               "Subprocess1");
        assertNull(subProcessOne.getLoopCharacteristics());

        for (FlowElement element : subProcessOne.getFlowElements()) {
            if (element instanceof BoundaryEvent) {
                BoundaryEvent be = (BoundaryEvent) element;
                if ("Timer1".equals(element.getName())) {
                    SubProcess sp = (SubProcess) unmarshaller.findContainerForBoundaryEvent(process,
                                                                                            be);
                    assertEquals("Subprocess1",
                                 sp.getName());
                    verifyAttribute(be,
                                    "dockerinfo",
                                    "47.0^80.0|");
                    foundTimer1 = true;
                }
            }
        }

        SubProcess subProcessTwo = (SubProcess) getFlowElement(process.getFlowElements(),
                                                               "Subprocess2");
        assertNull(subProcessTwo.getLoopCharacteristics());

        for (FlowElement element : subProcessTwo.getFlowElements()) {
            if (element instanceof BoundaryEvent) {
                BoundaryEvent be = (BoundaryEvent) element;
                if ("Timer2".equals(element.getName())) {
                    SubProcess sp = (SubProcess) unmarshaller.findContainerForBoundaryEvent(process,
                                                                                            be);
                    assertNull(sp.getLoopCharacteristics());
                    assertEquals("Subprocess2",
                                 sp.getName());
                    verifyAttribute(be,
                                    "dockerinfo",
                                    "46.0^77.0|");
                    foundTimer2 = true;
                }
            }
        }

        assertTrue(foundTimer1 && foundTimer2 && foundTimer3);
    }

    @Test
    public void testCompensationThrowingEvent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCompensationEventThrowing.json");
        Process process = getRootProcess(definitions);
        ThrowEvent compensationThrowEvent = (ThrowEvent) process.getFlowElements().get(2);
        assertEquals("Compensate",
                     compensationThrowEvent.getName());
        assertNotNull(compensationThrowEvent.getEventDefinitions());
        assertEquals(1,
                     compensationThrowEvent.getEventDefinitions().size());
        EventDefinition ed = compensationThrowEvent.getEventDefinitions().get(0);
        assertTrue(ed instanceof CompensateEventDefinition);
        CompensateEventDefinition ced = (CompensateEventDefinition) ed;
        assertNotNull(ced.getActivityRef());
        assertEquals("User Task",
                     ced.getActivityRef().getName());
    }

    @Test
    public void testBoundaryEvents() throws Exception {
        final String SUBTIMER_NAME = "SubTimer";
        final String SUBPROCESSMESSAGE_NAME = "SubProcessMessage";
        final String OUTTIMER_NAME = "OutTimer";

        Definitions definitions = loader.loadProcessFromJson("boundaryEvents.json");
        Process process = getRootProcess(definitions);

        assertTrue(containerContainsElement(process,
                                            OUTTIMER_NAME));
        assertTrue(containerContainsElement(process,
                                            SUBPROCESSMESSAGE_NAME));
        assertFalse(containerContainsElement(process,
                                             SUBTIMER_NAME));

        SubProcess subProcess = null;
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof SubProcess) {
                subProcess = (SubProcess) flowElement;
                break;
            }
        }

        assertNotNull(subProcess);
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(subProcess.getExtensionValues(),
                                      "customAsync"));
        assertFalse(containerContainsElement(subProcess,
                                             OUTTIMER_NAME));
        assertFalse(containerContainsElement(subProcess,
                                             SUBPROCESSMESSAGE_NAME));
        assertTrue(containerContainsElement(subProcess,
                                            SUBTIMER_NAME));
        assertNull(subProcess.getLoopCharacteristics());

        // There are not BPMNEdges for boundary events
        BoundaryEvent outTimer = (BoundaryEvent) getFlowElement(process.getFlowElements(),
                                                                "OutTimer");
        BoundaryEvent subProcessMessage = (BoundaryEvent) getFlowElement(process.getFlowElements(),
                                                                         "SubProcessMessage");
        assertEquals(0,
                     getBpmnEdges(outTimer,
                                  definitions).size());
        assertEquals(0,
                     getBpmnEdges(subProcessMessage,
                                  definitions).size());
    }

    @Test
    public void testBoundaryEventsContainers() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("boundaryEventsContainers.json");
        Process process = getRootProcess(definitions);

        final String TIMER_ONE = "Timer1";
        final String TIMER_TWO = "Timer2";
        final String TIMER_THREE = "Timer3";

        assertFalse(containerContainsElement(process,
                                             TIMER_ONE));
        assertFalse(containerContainsElement(process,
                                             TIMER_TWO));
        assertTrue(containerContainsElement(process,
                                            TIMER_THREE));

        for (FlowElement flowElement : process.getFlowElements()) {
            if ("Subprocess1".equals(flowElement.getName()) && (flowElement instanceof SubProcess)) {
                SubProcess subProcess = (SubProcess) flowElement;
                assertTrue(containerContainsElement(subProcess,
                                                    TIMER_ONE));
                assertFalse(containerContainsElement(subProcess,
                                                     TIMER_TWO));
                assertFalse(containerContainsElement(subProcess,
                                                     TIMER_THREE));
                assertNull(subProcess.getLoopCharacteristics());
            }

            if ("Subprocess2".equals(flowElement.getName()) && (flowElement instanceof SubProcess)) {
                SubProcess subProcess = (SubProcess) flowElement;
                assertFalse(containerContainsElement(subProcess,
                                                     TIMER_ONE));
                assertTrue(containerContainsElement(subProcess,
                                                    TIMER_TWO));
                assertFalse(containerContainsElement(subProcess,
                                                     TIMER_THREE));
                assertNull(subProcess.getLoopCharacteristics());
            }
        }
    }

    private boolean containerContainsElement(FlowElementsContainer container,
                                             String elementName) {
        for (FlowElement flowElement : container.getFlowElements()) {
            if (elementName.equals(flowElement.getName())) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testWorkItemHandlerNoParams() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("workItemHandlerNoParams.json");
        assertTrue(definitions.getRootElements().size() >= 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof StartEvent);
        StartEvent startEvent = (StartEvent) process.getFlowElements().get(0);
        assertEquals("TheStart",
                     startEvent.getName());
        Task task = (Task) process.getFlowElements().get(1);
        assertEquals("HelloWorldService",
                     task.getName());
        SequenceFlow flow = (SequenceFlow) process.getFlowElements().get(2);
        assertEquals("flow1",
                     flow.getName());
        assertEquals(startEvent,
                     flow.getSourceRef());
        assertEquals(task,
                     flow.getTargetRef());
    }

    /* Disabling test as no support for child lanes yet
    @Test
    public void testDoubleLaneUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("doubleLane.json");
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        Lane firstLane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("First lane", firstLane.getName());
        Lane secondLane = firstLane.getChildLaneSet().getLanes().get(0);
        assertEquals("Second lane", secondLane.getName());
        assertEquals(g, secondLane.getFlowNodeRefs().get(0));
        definitions.eResource().save(System.out, Collections.emptyMap());
    }*/

    /* Disabling test that doesn't pass.
    @Test
    public void testUserTaskDataPassing() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("userTaskDataPassing.json"));
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        Lane firstLane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("First lane", firstLane.getName());
        Lane secondLane = firstLane.getChildLaneSet().getLanes().get(0);
        assertEquals("Second lane", secondLane.getName());
        assertEquals(g, secondLane.getFlowNodeRefs().get(0));
        definitions.eResource().save(System.out, Collections.emptyMap());
    }*/

    private Process getRootProcess(Definitions def) {
        for (RootElement nextRootElement : def.getRootElements()) {
            if (nextRootElement instanceof Process) {
                return (Process) nextRootElement;
            }
        }
        return null;
    }

    @Test
    public void testDocumentationPropertyForBoundaryEvents() throws Exception {
        final String DOCUMENTATION_VALUE = "<![CDATA[Cancel task on timeout.]]>";
        final String BOUNDARY_EVENT_NAME = "CancelOnTimer";

        Definitions definitions = loader.loadProcessFromJson("boundaryEventsDocumentation.json");
        boolean documentationChecked = false;
        Process process = getRootProcess(definitions);

        for (FlowElement flow : process.getFlowElements()) {
            if (BOUNDARY_EVENT_NAME.equals(flow.getName())) {
                assertTrue(BOUNDARY_EVENT_NAME + " have no documentation.",
                           flow.getDocumentation().size() > 0);
                assertEquals(DOCUMENTATION_VALUE,
                             flow.getDocumentation().get(0).getText());
                documentationChecked = true;
                break;
            }
        }

        assertTrue("Boundary Event '" + BOUNDARY_EVENT_NAME + "' is not found in the process.",
                   documentationChecked);
    }

    @Test
    public void testDefaultMessageRefForStartMessageEvent() throws Exception {
        Process process = getRootProcess(loader.loadProcessFromJson("defaultMessageStartEvent.json"));
        assertTrue(process.getFlowElements().get(0) instanceof StartEvent);
        StartEvent startEvent = (StartEvent) process.getFlowElements().get(0);

        assertTrue(startEvent.getEventDefinitions().size() == 1);
        assertTrue(startEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);

        MessageEventDefinition messageEventDef = (MessageEventDefinition) startEvent.getEventDefinitions().iterator().next();

        assertNull(messageEventDef.getMessageRef());
    }

    @Test
    public void testDefaultMessageRefsCombined() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("defaultMessagesCombined.json");
        Process process = getRootProcess(definitions);

        assertTrue(process.getFlowElements().get(0) instanceof SendTask);
        SendTask sendTask = (SendTask) process.getFlowElements().get(0);
        assertNull(sendTask.getMessageRef());

        assertTrue(process.getFlowElements().get(1) instanceof ReceiveTask);
        ReceiveTask receiveTask = (ReceiveTask) process.getFlowElements().get(1);
        assertNull(receiveTask.getMessageRef());

        assertTrue(process.getFlowElements().get(2) instanceof StartEvent);
        StartEvent startEvent = (StartEvent) process.getFlowElements().get(2);
        assertTrue(startEvent.getEventDefinitions().size() == 1);
        assertTrue(startEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        MessageEventDefinition messageEventDefStart = (MessageEventDefinition) startEvent.getEventDefinitions().iterator().next();
        assertNull(messageEventDefStart.getMessageRef());

        assertTrue(process.getFlowElements().get(3) instanceof EndEvent);
        EndEvent endEvent = (EndEvent) process.getFlowElements().get(3);
        assertTrue(endEvent.getEventDefinitions().size() == 1);
        assertTrue(endEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        MessageEventDefinition messageEventDefEnd = (MessageEventDefinition) endEvent.getEventDefinitions().iterator().next();
        assertNull(messageEventDefEnd.getMessageRef());

        assertTrue(process.getFlowElements().get(4) instanceof IntermediateCatchEvent);
        IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) process.getFlowElements().get(4);
        assertTrue(catchEvent.getEventDefinitions().size() == 1);
        assertTrue(catchEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        MessageEventDefinition messageEventDefCatch = (MessageEventDefinition) catchEvent.getEventDefinitions().iterator().next();
        assertNull(messageEventDefCatch.getMessageRef());

        assertTrue(process.getFlowElements().get(5) instanceof IntermediateThrowEvent);
        IntermediateThrowEvent throwEvent = (IntermediateThrowEvent) process.getFlowElements().get(5);
        assertTrue(throwEvent.getEventDefinitions().size() == 1);
        assertTrue(throwEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        MessageEventDefinition messageEventDefThrow = (MessageEventDefinition) throwEvent.getEventDefinitions().iterator().next();
        assertNull(messageEventDefThrow.getMessageRef());
    }

    @Test
    public void testCallActivityAssignments() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("callActivityInSubprocess.json");
        Process process = getRootProcess(definitions);
        FlowElement subProcess = getFlowElement(process.getFlowElements(),
                                                "SubProcess");
        assertTrue(subProcess instanceof SubProcess);
        FlowElement activity = getFlowElement(((SubProcess) subProcess).getFlowElements(),
                                              "callActivity");
        assertTrue(activity instanceof CallActivity);
        CallActivity callActivity = (CallActivity) activity;
        InputOutputSpecification specification = callActivity.getIoSpecification();
        assertNull(((SubProcess) subProcess).getLoopCharacteristics());

        DataInput dataInput = getDataInput(specification.getDataInputs(),
                                           "innerInput");
        verifyAttribute(dataInput,
                        "dtype",
                        "Integer");
        DataOutput dataOutput = getDataOutput(specification.getDataOutputs(),
                                              "innerOutput");
        verifyAttribute(dataOutput,
                        "dtype",
                        "Integer");
    }

    @Test
    public void testSubProcessDiagramElements() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("callActivityInSubprocess.json");
        Process process = getRootProcess(definitions);
        FlowElement subProcess = getFlowElement(process.getFlowElements(),
                                                "SubProcess");
        verifyBpmnShapePresent(subProcess,
                               definitions);
        assertNull(((SubProcess) subProcess).getLoopCharacteristics());
    }

    @Test
    public void testNoDefaultMessageCreated() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("message.json");
        assertTrue(definitions.getRootElements().size() == 1);
    }

    @Test
    public void testDocumentationForSwimlane() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("swimlane.json");
        Process process = getRootProcess(definitions);
        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("Swimlane name is wrong.",
                     lane.getName(),
                     "Documented Swimlane");
        assertEquals("<![CDATA[Some documentation for swimlane.]]>",
                     lane.getDocumentation().get(0).getText());
    }

    @Test
    public void testUserTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("userTask.json");
        Process process = getRootProcess(definitions);
        UserTask userTask = null;
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof UserTask) {
                userTask = (UserTask) flowElement;
                break;
            }
        }
        assertNotNull(userTask);
        assertEquals("User Task One",
                     userTask.getName());
        assertEquals("assignedActor",
                     ((FormalExpression) userTask.getResources().get(0).getResourceAssignmentExpression().getExpression()).getBody());

        boolean foundTaskName = false;
        boolean foundGroupId = false;
        for (DataInputAssociation association : userTask.getDataInputAssociations()) {
            if (association.getAssignment() != null) {
                for (Assignment assignment : association.getAssignment()) {
                    String from = ((FormalExpression) assignment.getFrom()).getBody();
                    String to = ((FormalExpression) assignment.getTo()).getBody();
                    if (to.contains("TaskName") && from.equals("<![CDATA[taskForAssignedActor]]>")) {
                        foundTaskName = true;
                    }
                    if (to.contains("GroupId") && from.equals("<![CDATA[assignedGroup]]>")) {
                        foundGroupId = true;
                    }
                }
            }
        }

        assertTrue(foundTaskName);
        assertTrue(foundGroupId);
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(userTask.getExtensionValues(),
                                      "customAsync"));
        verifyBpmnShapePresent(userTask,
                               definitions);
    }

    @Test
    public void testSequenceFlowPointsInsideLane() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("sequenceFlowPointsInsideLane.json");
        BPMNPlane plane = definitions.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNEdge) {
                BPMNEdge edge = (BPMNEdge) dia;
                List<Point> wayPoints = edge.getWaypoint();
                assertNotNull(wayPoints);
                assertEquals(wayPoints.size(),
                             2);
                assertEquals(Float.valueOf(wayPoints.get(0).getX()),
                             new Float(252.0));
                assertEquals(Float.valueOf(wayPoints.get(0).getY()),
                             new Float(220.0));

                assertEquals(Float.valueOf(wayPoints.get(1).getX()),
                             new Float(357.0));
                assertEquals(Float.valueOf(wayPoints.get(1).getY()),
                             new Float(220.0));
            }
        }
    }

    @Test
    public void testCallActivity() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("callActivity.json");
        Process process = getRootProcess(definitions);
        CallActivity callActivity = null;
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof CallActivity) {
                callActivity = (CallActivity) flowElement;
                break;
            }
        }
        assertNotNull(callActivity);
        assertEquals("callActivity",
                     callActivity.getName());
        assertEquals("abc.noCalledElementCallActivity",
                     callActivity.getCalledElement());
    }

    @Test
    public void testDefaultInterfaceForServiceTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("defaultServiceTask.json");
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof ServiceTask);
        ServiceTask serviceTask = (ServiceTask) process.getFlowElements().get(0);
        verifyServiceTask(serviceTask,
                          "Java",
                          null,
                          null);
    }

    @Test
    public void testServiceTaskInterfaceAndOperation() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("serviceTaskInterfaceAndOperation.json");
        Process process = getRootProcess(definitions);
        FlowElement element = getFlowElement(process.getFlowElements(),
                                             "Send PO");
        assertTrue(element instanceof ServiceTask);
        ServiceTask serviceTask = (ServiceTask) element;
        verifyServiceTask(serviceTask,
                          "Java",
                          "sendInterface",
                          "sendOperation");
        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(serviceTask.getExtensionValues(),
                                      "customAsync"));
    }

    @Test
    public void testSubprocessTaskAssignemtns() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("subprocessTaskAssignments.json");
        Process process = getRootProcess(definitions);
        FlowElement subprocess = getFlowElement(process.getFlowElements(),
                                                "Embedded subprocess");
        assertTrue(subprocess instanceof SubProcess);
        FlowElement element = getFlowElement(((SubProcess) subprocess).getFlowElements(),
                                             "UserTask");
        assertTrue(element instanceof UserTask);
        UserTask userTask = (UserTask) element;
        InputOutputSpecification specification = userTask.getIoSpecification();
        DataInput sInput = getDataInput(specification.getDataInputs(),
                                        "sInput");
        DataOutput iOutput = getDataOutput(specification.getDataOutputs(),
                                           "iOutput");
        verifyAttribute(sInput,
                        "dtype",
                        "String");
        verifyAttribute(iOutput,
                        "dtype",
                        "Integer");
        assertNull(((SubProcess) subprocess).getLoopCharacteristics());
    }

    @Test
    public void testEndEventsAssignments() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("subprocessTaskAssignments.json");
        Process process = getRootProcess(definitions);
        FlowElement subprocess = getFlowElement(process.getFlowElements(),
                                                "Embedded subprocess");
        assertTrue(subprocess instanceof SubProcess);
        FlowElement element = getFlowElement(((SubProcess) subprocess).getFlowElements(),
                                             "SubEnd");
        assertTrue(element instanceof EndEvent);
        EndEvent subEnd = (EndEvent) element;
        List<DataInputAssociation> associations = subEnd.getDataInputAssociation();
        assertEquals(1,
                     associations.size());
        assertEquals("intVar",
                     associations.get(0).getSourceRef().get(0).getId());
        assertTrue(associations.get(0).getTargetRef().getId().contains("intSubInput"));
        verifyAttribute(associations.get(0).getTargetRef(),
                        "dtype",
                        "Integer");
        assertNull(((SubProcess) subprocess).getLoopCharacteristics());

        element = getFlowElement((process).getFlowElements(),
                                 "End Event");
        assertTrue(element instanceof EndEvent);
        EndEvent endEvent = (EndEvent) element;
        associations = endEvent.getDataInputAssociation();
        assertEquals(1,
                     associations.size());
        assertEquals("intVar",
                     associations.get(0).getSourceRef().get(0).getId());
        assertTrue(associations.get(0).getTargetRef().getId().contains("intInput"));
        verifyAttribute(associations.get(0).getTargetRef(),
                        "dtype",
                        "Integer");
    }

    @Test
    public void testRestTaskAssignments() throws Exception {
        String[] taskInputs = {"Content", "ContentType", "ResultClass", "Method", "Username", "Password",
                "ReadTimeout", "ConnectTimeout", "Url"};
        Definitions definitions = loader.loadProcessFromJson("restTask.json");
        Process process = getRootProcess(definitions);
        FlowElement element = getFlowElement(process.getFlowElements(),
                                             "REST");
        assertTrue(element instanceof Task);
        Task restTask = (Task) element;
        InputOutputSpecification specification = restTask.getIoSpecification();
        for (String input : taskInputs) {
            DataInput dataInput = getDataInput(specification.getDataInputs(),
                                               input);
            verifyAttribute(dataInput,
                            "dtype",
                            "String");
        }
        DataOutput dataOutput = getDataOutput(specification.getDataOutputs(),
                                              "Result");
        verifyAttribute(dataOutput,
                        "dtype",
                        "java.lang.Object");
    }

    @Test
    public void testTaskInputOutputSet() throws Exception {
        Process process = getRootProcess(loader.loadProcessFromJson("taskInputOutputSet.json"));
        assertTrue(process.getFlowElements().get(1) instanceof UserTask);
        UserTask task = (UserTask) process.getFlowElements().get(1);
        assertEquals("userTask",
                     task.getName());
        InputSet inputSet = task.getIoSpecification().getInputSets().get(0);
        OutputSet outputSet = task.getIoSpecification().getOutputSets().get(0);
        assertEquals(4,
                     inputSet.getDataInputRefs().size());
        assertEquals("firstInput",
                     inputSet.getDataInputRefs().get(0).getName());
        assertEquals("secondInput",
                     inputSet.getDataInputRefs().get(1).getName());
        assertEquals("TaskName",
                     inputSet.getDataInputRefs().get(2).getName());
        assertEquals("Skippable",
                     inputSet.getDataInputRefs().get(3).getName());
        assertEquals(1,
                     outputSet.getDataOutputRefs().size());
        assertEquals("firstOutput",
                     outputSet.getDataOutputRefs().get(0).getName());
    }

    @Test
    public void testSubprocessDefaultInputOutputSets() throws Exception {
        Process process = getRootProcess(loader.loadProcessFromJson("defaultSubprocessInputOutputSets.json"));
        assertTrue(process.getFlowElements().get(1) instanceof SubProcess);
        SubProcess subProcess = (SubProcess) process.getFlowElements().get(1);
        assertNotNull(subProcess.getLoopCharacteristics());
        InputSet inputSet = subProcess.getIoSpecification().getInputSets().get(0);
        OutputSet outputSet = subProcess.getIoSpecification().getOutputSets().get(0);
        assertEquals(0,
                     inputSet.getDataInputRefs().size());
        assertEquals(0,
                     outputSet.getDataOutputRefs().size());
    }

    @Test
    public void testSubprocessDefaultOutputSet() throws Exception {
        Process process = getRootProcess(loader.loadProcessFromJson("subprocessDefaultOutputSet.json"));
        assertTrue(process.getFlowElements().get(1) instanceof SubProcess);
        SubProcess subProcess = (SubProcess) process.getFlowElements().get(1);
        assertNotNull(subProcess.getLoopCharacteristics());
        InputSet inputSet = subProcess.getIoSpecification().getInputSets().get(0);
        OutputSet outputSet = subProcess.getIoSpecification().getOutputSets().get(0);
        assertEquals(1,
                     inputSet.getDataInputRefs().size());
        assertEquals(0,
                     outputSet.getDataOutputRefs().size());
    }

    private FlowElement getFlowElement(List<FlowElement> elements,
                                       String name) {
        for (FlowElement element : elements) {
            if (element.getName() != null && name.compareTo(element.getName()) == 0) {
                return element;
            }
        }
        return null;
    }

    private String getMetaDataValue(List<ExtensionAttributeValue> extensionValues,
                                    String metaDataName) {
        for (MetaDataType type : this.<MetaDataType>extractFeature(extensionValues,
                                                                   DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA)) {
            if (type.getName() != null && type.getName().equals(metaDataName)) {
                return type.getMetaValue();
            }
        }
        return null;
    }

    private String getOnEntryScript(List<ExtensionAttributeValue> extensionValues) {
        for (OnEntryScriptType type : this.<OnEntryScriptType>extractFeature(extensionValues,
                                                                             DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT)) {
            return type.getScript();
        }
        return null;
    }

    private String getOnExitScript(List<ExtensionAttributeValue> extensionValues) {
        for (OnExitScriptType type : this.<OnExitScriptType>extractFeature(extensionValues,
                                                                           DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT)) {
            return type.getScript();
        }
        return null;
    }

    private <T> List<T> extractFeature(List<ExtensionAttributeValue> extensionValues,
                                       EStructuralFeature feature) {
        List<T> result = new ArrayList<T>();
        if (extensionValues != null) {
            for (ExtensionAttributeValue extattrval : extensionValues) {
                FeatureMap extensionElements = extattrval.getValue();
                result.addAll((List<T>) extensionElements.get(feature,
                                                              true));
            }
        }
        return result;
    }

    private void verifyServiceTask(ServiceTask serviceTask,
                                   String serviceImplementation,
                                   String serviceInterface,
                                   String serviceOperation) {
        String foundServiceImplementation = null;
        String foundServiceInterface = null;
        String foundServiceOperation = null;

        Iterator<FeatureMap.Entry> iter = serviceTask.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("serviceimplementation")) {
                foundServiceImplementation = (String) entry.getValue();
            }
            if (entry.getEStructuralFeature().getName().equals("serviceoperation")) {
                foundServiceOperation = (String) entry.getValue();
            }
            if (entry.getEStructuralFeature().getName().equals("serviceinterface")) {
                foundServiceInterface = (String) entry.getValue();
            }
        }

        assertEquals(serviceImplementation,
                     foundServiceImplementation);
        assertEquals(serviceInterface,
                     foundServiceInterface);
        assertEquals(serviceOperation,
                     foundServiceOperation);
    }

    private DataInput getDataInput(List<DataInput> inputs,
                                   String name) {
        if (inputs != null) {
            for (DataInput input : inputs) {
                if (input.getName() != null && input.getName().equals(name)) {
                    return input;
                }
            }
        }
        return null;
    }

    private DataOutput getDataOutput(List<DataOutput> outputs,
                                     String name) {
        if (outputs != null) {
            for (DataOutput output : outputs) {
                if (output.getName() != null && output.getName().equals(name)) {
                    return output;
                }
            }
        }
        return null;
    }

    private void verifyAttribute(BaseElement element,
                                 String attributeName,
                                 Object attributeValue) {
        Iterator<FeatureMap.Entry> iter = element.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals(attributeName) && entry.getValue().equals(attributeValue)) {
                return;
            }
        }

        fail(attributeName + " with value: " + attributeValue + " was not found");
    }

    @Test
    public void testUserTasksWithNoName() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = loader.loadProcessFromJson("usertaskswithnoname.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        UserTask t1 = (UserTask) process.getFlowElements().get(6);
        assertEquals("Task_1",
                     t1.getName());
        UserTask t2 = (UserTask) process.getFlowElements().get(8);
        assertEquals("Task_2",
                     t2.getName());
    }

    @Test
    public void testMIandEventProcessBoundaryEvents() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("miandeventsubprocessboundary.json");
        Process process = getRootProcess(definitions);

        SubProcess miSubprocess = (SubProcess) process.getFlowElements().get(0);
        assertNotNull(miSubprocess.getLoopCharacteristics());
        assertNotNull(miSubprocess);
        assertEquals("MyMISubprocess",
                     miSubprocess.getName());

        SubProcess eventSubprocess = (SubProcess) process.getFlowElements().get(1);
        assertNotNull(eventSubprocess);
        assertEquals("MyEventSubprocess",
                     eventSubprocess.getName());

        BoundaryEvent subprocessBoundary1 = (BoundaryEvent) process.getFlowElements().get(2);
        assertNotNull(subprocessBoundary1);
        assertNotNull(subprocessBoundary1.getName());
        if (subprocessBoundary1.getName().equals("MyTimberBoundaryEvent1")) {
            assertEquals(subprocessBoundary1.getAttachedToRef().getId(),
                         eventSubprocess.getId());
        } else if (subprocessBoundary1.getName().equals("MyTimberBoundaryEvent2")) {
            assertEquals(subprocessBoundary1.getAttachedToRef().getId(),
                         miSubprocess.getId());
        } else {
            fail("Illegal attached to ref for boundary event");
        }

        BoundaryEvent subprocessBoundary2 = (BoundaryEvent) process.getFlowElements().get(3);
        assertNotNull(subprocessBoundary2);
        assertNotNull(subprocessBoundary2.getName());
        if (subprocessBoundary2.getName().equals("MyTimberBoundaryEvent1")) {
            assertEquals(subprocessBoundary2.getAttachedToRef().getId(),
                         eventSubprocess.getId());
        } else if (subprocessBoundary2.getName().equals("MyTimberBoundaryEvent2")) {
            assertEquals(subprocessBoundary2.getAttachedToRef().getId(),
                         miSubprocess.getId());
        } else {
            fail("Illegal attached to ref for boundary event");
        }
    }

    @Test
    public void testOnEntryOnExitScript() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("onentryonexitactions.json");
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        UserTask task = (UserTask) process.getFlowElements().get(1);
        assertNotNull(task);
        String takOnEntryScript = getOnEntryScript(task.getExtensionValues());
        String taskOnExitScript = getOnExitScript(task.getExtensionValues());
        assertNotNull(takOnEntryScript);
        assertNotNull(taskOnExitScript);

        assertEquals(takOnEntryScript,
                     "<![CDATA[if ( a == null || a ) { System.out.println(a); }]]>");
        assertEquals(taskOnExitScript,
                     "<![CDATA[if ( b == null || b ) { System.out.println(b); }]]>");
    }

    @Test
    public void testMITaskProperties() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("mitaskproperties.json");
        assertTrue(definitions.getRootElements().size() == 6);
        Process process = getRootProcess(definitions);
        UserTask task = (UserTask) process.getFlowElements().get(0);
        assertNotNull(task);
        assertEquals("MyTask",
                     task.getName());
        assertEquals("miin",
                     task.getIoSpecification().getInputSets().get(0).getDataInputRefs().get(0).getName());
        assertEquals("miout",
                     task.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().get(0).getName());

        assertTrue(task.getLoopCharacteristics() instanceof MultiInstanceLoopCharacteristics);
        MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics) task.getLoopCharacteristics();
        assertNotNull(lc);
        assertEquals("_aaItem",
                     lc.getLoopDataInputRef().getItemSubjectRef().getId());
        assertEquals("_bbItem",
                     lc.getLoopDataOutputRef().getItemSubjectRef().getId());
        assertEquals("abcde",
                     ((FormalExpression) lc.getCompletionCondition()).getBody());
        assertNotNull(lc.getInputDataItem());
        assertNotNull(lc.getOutputDataItem());
    }

    @Test
    public void testNoMIOnEmbeddedSubprocess() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("simpleEmbeddedSubprocess.json");
        Process process = getRootProcess(definitions);
        SubProcess subprocess = (SubProcess) getFlowElement(process.getFlowElements(),
                                                            "abc");
        assertNull(subprocess.getLoopCharacteristics().getId());
    }

    @Test
    public void testMISubProcess() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("miSubProcess.json");
        Process process = getRootProcess(definitions);
        SubProcess miSubProcess = (SubProcess) getFlowElement(process.getFlowElements(),
                                                              "miSubProcess");

        assertEquals(1,
                     miSubProcess.getIoSpecification().getDataInputs().size());
        assertEquals(1,
                     miSubProcess.getIoSpecification().getDataOutputs().size());
        assertEquals(1,
                     miSubProcess.getIoSpecification().getInputSets().size());
        assertEquals(1,
                     miSubProcess.getIoSpecification().getOutputSets().size());
        assertEquals(1,
                     miSubProcess.getIoSpecification().getInputSets().get(0).getDataInputRefs().size());
        assertEquals(1,
                     miSubProcess.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().size());

        assertTrue(miSubProcess.getLoopCharacteristics() instanceof MultiInstanceLoopCharacteristics);
        MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics) miSubProcess.getLoopCharacteristics();
        assertEquals(miSubProcess.getId() + "_input",
                     lc.getLoopDataInputRef().getId());
        assertEquals(miSubProcess.getId() + "_output",
                     lc.getLoopDataOutputRef().getId());
        assertEquals("variableOne",
                     ((DataInputImpl) lc.getLoopDataInputRef()).getName());
        assertEquals("variableTwo",
                     ((DataOutputImpl) lc.getLoopDataOutputRef()).getName());
        assertNotNull("defaultDataInput",
                      lc.getInputDataItem().getId());
        assertNotNull("defaultDataOutput",
                      lc.getOutputDataItem().getId());
    }

    @Test
    public void testProcessCustomProperties() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("customProperties.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);

        List<ExtensionAttributeValue> extensionAttributeValues = process.getExtensionValues();
        assertNotNull(extensionAttributeValues);
        assertEquals(1,
                     extensionAttributeValues.size());

        assertEquals("<![CDATA[HR]]>",
                     getMetaDataValue(process.getExtensionValues(),
                                      "customCaseIdPrefix"));
        assertEquals("<![CDATA[owner:1,participant:2]]>",
                     getMetaDataValue(process.getExtensionValues(),
                                      "customCaseRoles"));
    }

    @Test
    public void testTaskCustomProperties() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("customPropertiesTask.json");
        Process process = getRootProcess(definitions);

        UserTask task = (UserTask) process.getFlowElements().get(1);
        assertNotNull(task);
        assertEquals("Task_1",
                     task.getName());

        List<ExtensionAttributeValue> extensionAttributeValues = task.getExtensionValues();
        assertNotNull(extensionAttributeValues);
        assertEquals(1,
                     extensionAttributeValues.size());

        assertEquals("<![CDATA[true]]>",
                     getMetaDataValue(task.getExtensionValues(),
                                      "customAutoStart"));
    }

    @Test
    public void testUserTaskTaskNameProperty() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("userTaskTaskName.json");
        Process process = getRootProcess(definitions);

        UserTask task = (UserTask) process.getFlowElements().get(0);
        assertNotNull(task);
        assertEquals("<![CDATA[strangetaskname~`!@#$*%^()_+=-{}|\\][\":;'?><,./]]>",
                     ((FormalExpression) task.getDataInputAssociations().get(0).getAssignment().get(0).getFrom()).getBody());
    }

    @Test
    public void testUserTaskDataIOForForms() throws Exception {
        List<String> excludeFromForms = Arrays.asList("TaskName",
                                                      "GroupId",
                                                      "Skippable",
                                                      "Comment",
                                                      "Description",
                                                      "Content",
                                                      "Priority",
                                                      "Locale",
                                                      "CreatedBy",
                                                      "NotCompletedReassign",
                                                      "NotStartedReassign",
                                                      "NotCompletedNotify",
                                                      "NotStartedNotify");

        Definitions definitions = loader.loadProcessFromJson("userTaskDataIOForForms.json");
        Process process = getRootProcess(definitions);

        UserTask task = (UserTask) process.getFlowElements().get(1);
        assertNotNull(task);

        List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = task.getDataOutputAssociations();
        assertNotNull(inputAssociations);
        assertNotNull(outputAssociations);

        List<String> dataInputNamesForForms = new ArrayList<>();
        List<String> dataOutputNamesForForm = new ArrayList<>();
        Map<String, String> dataInputOutputTypes = new HashMap<>();

        for (DataInputAssociation dataIn : inputAssociations) {
            DataInput din = (DataInput) dataIn.getTargetRef();
            if (!excludeFromForms.contains(din.getName())) {
                dataInputNamesForForms.add(din.getName());
                dataInputOutputTypes.put(din.getName(),
                                         din.getItemSubjectRef().getStructureRef());
            }
        }

        for (DataOutputAssociation dataOut : outputAssociations) {
            DataOutput dout = (DataOutput) dataOut.getSourceRef().get(0);
            dataOutputNamesForForm.add(dout.getName());
            dataInputOutputTypes.put(dout.getName(),
                                     dout.getItemSubjectRef().getStructureRef());
        }

        assertTrue(dataInputNamesForForms.size() == 3);
        assertTrue(dataOutputNamesForForm.size() == 1);

        assertTrue(dataInputNamesForForms.contains("reason"));
        assertTrue(dataInputNamesForForms.contains("BusinessAdministratorId"));
        assertTrue(dataInputNamesForForms.contains("test"));
        assertEquals("String",
                     dataInputOutputTypes.get("reason"));
        assertEquals("String",
                     dataInputOutputTypes.get("BusinessAdministratorId"));
        assertEquals("String",
                     dataInputOutputTypes.get("test"));

        assertTrue(dataOutputNamesForForm.contains("performance"));
        assertEquals("String",
                     dataInputOutputTypes.get("performance"));
    }

    @Test
    public void testAdHocSubprocessDroolsCompletionCondition() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("adHocSubprocessCompletionCondition.json");
        Process process = getRootProcess(definitions);

        AdHocSubProcess adHocSubProcess = (AdHocSubProcess) process.getFlowElements().get(0);
        assertNotNull(adHocSubProcess);
        assertNotNull(adHocSubProcess.getCompletionCondition());
        assertTrue(adHocSubProcess.getCompletionCondition() instanceof FormalExpression);

        FormalExpression expression = (FormalExpression) adHocSubProcess.getCompletionCondition();
        assertEquals("<![CDATA[org.kie.api.runtime.process.CaseData(data.get(\"claimReportDone\") == true)]]>",
                     expression.getBody());
        assertEquals("http://www.jboss.org/drools/rule",
                     expression.getLanguage());
    }

    @Test
    public void testDMNBusinessRuleTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("dmnBusinessRule.json");
        Process process = getRootProcess(definitions);
        FlowElement task = getFlowElement(process.getFlowElements(),
                                          "test");
        assertTrue(task instanceof BusinessRuleTask);
        BusinessRuleTask ruleTask = (BusinessRuleTask) task;
        assertEquals(RuleSetNode.DMN_LANG,
                     ruleTask.getImplementation());
    }

    @Test
    public void testCallActivityAbortParent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("callActivityAbortParent.json");
        Process process = getRootProcess(definitions);
        CallActivity callActivity = null;
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof CallActivity) {
                callActivity = (CallActivity) flowElement;
                break;
            }
        }
        assertNotNull(callActivity);
        assertEquals("callActivity",
                     callActivity.getName());
        assertEquals("<![CDATA[false]]>",
                     getMetaDataValue(callActivity.getExtensionValues(),
                                      "customAbortParent"));
    }

    @Test
    public void testAdHocSubprocessCompletionCondition() throws Exception {
        Definitions definitionsOne = loader.loadProcessFromJson("adhocSubprocessSetCompletionCondition.json");
        Definitions definitionsTwo = loader.loadProcessFromJson("adhocSubprocessEmptyCompletionCondition.json");

        Process processOne = getRootProcess(definitionsOne);
        Process processTwo = getRootProcess(definitionsTwo);

        AdHocSubProcess subprocessWithSetCompletionCondition = (AdHocSubProcess) processOne.getFlowElements().get(0);
        AdHocSubProcess subprocessWithEmptyCompletionCondition = (AdHocSubProcess) processTwo.getFlowElements().get(0);

        assertNotNull(subprocessWithSetCompletionCondition);
        assertNotNull(subprocessWithEmptyCompletionCondition);

        assertEquals("<![CDATA[autocomplete]]>",
                     ((FormalExpression) subprocessWithSetCompletionCondition.getCompletionCondition()).getBody());
        assertEquals("<![CDATA[autocomplete]]>",
                     ((FormalExpression) subprocessWithEmptyCompletionCondition.getCompletionCondition()).getBody());
    }

    @Test
    public void testUserTaskSimpleAssignment() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("userTaskSimpleAssignment.json");
        Process process = getRootProcess(definitions);
        FlowElement userTaskFlowElement = getFlowElement(process.getFlowElements(),
                                                         "SimpleUserTask");
        assertTrue(userTaskFlowElement instanceof UserTask);
        UserTask userTask = (UserTask) userTaskFlowElement;
        boolean foundTestAssignment = false;
        for (DataInputAssociation association : userTask.getDataInputAssociations()) {
            if (association.getAssignment() != null) {
                for (Assignment assignment : association.getAssignment()) {
                    String from = ((FormalExpression) assignment.getFrom()).getBody();
                    String to = ((FormalExpression) assignment.getTo()).getBody();
                    if (to.contains("_TestInputX")) {
                        assertEquals("<![CDATA[Second Value]]>",
                                     from);
                        foundTestAssignment = true;
                    }
                }
            }
        }
        assertTrue(foundTestAssignment);
    }

    public void testInputOutputSetsOfIoSpecification() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("taskIoSpecification.json");
        Process process = getRootProcess(definitions);

        UserTask task = (UserTask) process.getFlowElements().get(0);
        assertNotNull(task);
        SubProcess subProcess = (SubProcess) process.getFlowElements().get(1);
        assertNotNull(subProcess);
        UserTask task2 = (UserTask) subProcess.getFlowElements().get(0);
        assertNotNull(task2);

        assertNotNull(task.getIoSpecification());
        assertNotNull(task.getIoSpecification().getInputSets());
        assertNotNull(task.getIoSpecification().getOutputSets());

        assertNotNull(task2.getIoSpecification());
        assertNotNull(task2.getIoSpecification().getInputSets());
        assertNotNull(task2.getIoSpecification().getOutputSets());
    }

    @Test
    public void testDisableBpsimDisplayViaProfileSetting() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("userTask.json",
                                                             "false",
                                                             "false",
                                                             null);
        assertEquals(0,
                     definitions.getRelationships().size());
    }

    @Test
    public void testDisableBpsimDisplayViaSystemProperty() throws Exception {
        System.setProperty(EditorHandler.BPSIM_DISPLAY,
                           "false");
        Definitions definitions = loader.loadProcessFromJson("userTask.json",
                                                             "false",
                                                             "true",
                                                             null);
        // should still be 0 even tho "true" is passed for bpsimDisplay
        assertEquals(0,
                     definitions.getRelationships().size());
        System.clearProperty(EditorHandler.BPSIM_DISPLAY);
    }

    @Test
    public void testEnableBpsimDisplayViaSystemProperty() throws Exception {
        System.setProperty(EditorHandler.BPSIM_DISPLAY,
                           "true");
        Definitions definitions = loader.loadProcessFromJson("userTask.json",
                                                             "false",
                                                             "false",
                                                             null);
        // should still be 1 even tho "false" is passed for bpsimDisplay
        assertEquals(1,
                     definitions.getRelationships().size());
        System.clearProperty(EditorHandler.BPSIM_DISPLAY);
    }

    @Test
    public void testBpsimDisplayEnabledByDefault() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("userTask.json");
        assertEquals(1,
                     definitions.getRelationships().size());
    }

    private void verifyBpmnShapePresent(BaseElement element,
                                        Definitions definitions) {
        boolean diagramElementPresent = false;
        for (DiagramElement diagramElement : definitions.getDiagrams().get(0).getPlane().getPlaneElement()) {
            if (diagramElement instanceof BPMNShape && ((BPMNShape) diagramElement).getBpmnElement() == element) {
                diagramElementPresent = true;
            }
        }
        assertTrue(diagramElementPresent);
    }

    private List<BPMNEdge> getBpmnEdges(BaseElement element,
                                        Definitions definitions) {
        List<BPMNEdge> edges = new ArrayList<>();
        for (DiagramElement diagramElement : definitions.getDiagrams().get(0).getPlane().getPlaneElement()) {
            if (diagramElement instanceof BPMNEdge && ((BPMNEdge) diagramElement).getBpmnElement().equals(element)) {
                edges.add((BPMNEdge) diagramElement);
            }
        }
        return edges;
    }

    private int getDIElementOrder(BaseElement element,
                                  Definitions definitions) {
        int counter = 0;
        for (DiagramElement diagramElement : definitions.getDiagrams().get(0).getPlane().getPlaneElement()) {
            if (diagramElement instanceof BPMNShape && ((BPMNShape) diagramElement).getBpmnElement() == element) {
                return counter;
            }
            counter++;
        }
        return -1;
    }

    @Test
    public void testWorkitemAssignments() throws Exception {
        List<String> testWorkItemNames = Arrays.asList("SampleUserWorkitem");
        Definitions definitions = loader.loadProcessFromJson("workitemAssignments.json",
                                                             testWorkItemNames);
        Process process = getRootProcess(definitions);
        FlowElement element = getFlowElement(process.getFlowElements(),
                                             "SampleUserWorkitem");
        assertNotNull(element);
        assertTrue(element instanceof Task);
        Task workitemTask = (Task) element;
        InputOutputSpecification iospec = workitemTask.getIoSpecification();
        assertNotNull(iospec);

        List<InputSet> inSet = iospec.getInputSets();
        List<OutputSet> outSet = iospec.getOutputSets();
        assertNotNull(inSet);
        assertNotNull(outSet);
        assertEquals(1, inSet.size());
        assertEquals(1, outSet.size());

        InputSet firstInSet = inSet.get(0);
        assertNotNull(firstInSet);
        assertNotNull(firstInSet.getDataInputRefs());
        assertEquals(3, firstInSet.getDataInputRefs().size());

        OutputSet firstOutSet = outSet.get(0);
        assertNotNull(firstOutSet);
        assertNotNull(firstOutSet.getDataOutputRefs());
        assertEquals(2, firstOutSet.getDataOutputRefs().size());

        List<DataInputAssociation> workItemDataInputAssociations = workitemTask.getDataInputAssociations();
        List<DataOutputAssociation> workItemDataOutputAssociations = workitemTask.getDataOutputAssociations();

        assertNotNull(workItemDataInputAssociations);
        assertNotNull(workItemDataOutputAssociations);
        assertEquals(3, workItemDataInputAssociations.size());
        assertEquals(2, workItemDataOutputAssociations.size());
    }
}

