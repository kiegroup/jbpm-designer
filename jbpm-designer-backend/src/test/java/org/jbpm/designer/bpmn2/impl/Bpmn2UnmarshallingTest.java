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
package org.jbpm.designer.bpmn2.impl;


import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.junit.Test;

/**
 * @author Antoine Toulme
 *
 * A series of tests to check the unmarshalling of json to bpmn2.
 */
public class Bpmn2UnmarshallingTest {

    private Bpmn2Loader loader = new Bpmn2Loader(Bpmn2UnmarshallingTest.class);

    @Test
    public void testSimpleDefinitionsUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("empty.json");
        assertEquals("<![CDATA[my doc]]>", definitions.getRootElements().get(0).getDocumentation().iterator().next().getText());
        assertEquals("http://www.w3.org/1999/XPath", definitions.getExpressionLanguage());
        assertEquals("http://www.omg.org/bpmn20", definitions.getTargetNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema", definitions.getTypeLanguage());
        assertTrue(definitions.getRootElements().size() == 1);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testSimpleGlobalTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("oneTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof GlobalTask);
        GlobalTask task = (GlobalTask) definitions.getRootElements().iterator().next();
        assertEquals("oneTask", task.getName());
        assertEquals("my task doc", task.getDocumentation().iterator().next().getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testTwoGlobalTasksUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("twoTask.json");
        assertTrue(definitions.getRootElements().size() == 2);
        assertTrue(definitions.getRootElements().get(0) instanceof GlobalTask);
        GlobalTask task = (GlobalTask) definitions.getRootElements().get(0);
        assertEquals("firstTask", task.getName());
        assertEquals("my task doc", task.getDocumentation().iterator().next().getText());
        GlobalTask task2 = (GlobalTask) definitions.getRootElements().get(1);
        assertEquals("secondTask", task2.getName());
        assertEquals("my task doc too", task2.getDocumentation().iterator().next().getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testPoolUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("pool.json");
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = getRootProcess(definitions);
        assertEquals("pool", process.getName());
        assertEquals(ProcessType.PRIVATE, process.getProcessType());
        assertTrue(process.isIsClosed());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testLaneUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("pool.json");
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = getRootProcess(definitions);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        Lane l = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("my first lane", l.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testSequenceFlowUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("sequenceFlow.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof Task);
        Task task = (Task) process.getFlowElements().get(0);
        assertEquals("task1", task.getName());
        Task task2 = (Task) process.getFlowElements().get(1);
        assertEquals("task2", task2.getName());
        SequenceFlow flow = (SequenceFlow) process.getFlowElements().get(2);
        assertEquals("seqFlow", flow.getName());
        assertEquals(task, flow.getSourceRef());
        assertEquals(task2, flow.getTargetRef());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testScriptTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("scriptTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalScriptTask task = (GlobalScriptTask) definitions.getRootElements().get(0);
        assertEquals("my script", task.getName());
        assertEquals("git status | grep modified | awk '{print $3}' | xargs echo | xargs git add", task.getScript());
        assertEquals("bash", task.getScriptLanguage());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testBusinessRuleTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("businessRuleTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalBusinessRuleTask task = (GlobalBusinessRuleTask) definitions.getRootElements().get(0);
        assertEquals("call business rule", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    //@Test
    // removing until we start supporting global tasks
    public void testManualTaskUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("manualTask.json");
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalManualTask task = (GlobalManualTask) definitions.getRootElements().get(0);
        assertEquals("pull a lever", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("gateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ExclusiveGateway g = (ExclusiveGateway) process.getFlowElements().get(0);
        assertEquals("xor gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testParallelGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("parallelGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ParallelGateway g = (ParallelGateway) process.getFlowElements().get(0);
        assertEquals("parallel gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEventBasedGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("eventBasedGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EventBasedGateway g = (EventBasedGateway) process.getFlowElements().get(0);
        assertEquals("event-based gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testInclusiveGatewayUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("inclusiveGateway.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        InclusiveGateway g = (InclusiveGateway) process.getFlowElements().get(0);
        assertEquals("inclusive gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startMessageEvent.json");
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start message event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start escalation event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start compensation event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartParallelMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startParallelMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start parallel multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartSignalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start signal event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testStartTimerEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startTimerEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start timer event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testGroupUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("group.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        Group group = (Group) process.getArtifacts().iterator().next();
        assertEquals("Group name is wrong.", group.getCategoryValueRef().getValue(), "group");
        assertEquals(group.getDocumentation().get(0).getText(), "<![CDATA[group documentation]]>");
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testTextAnnotationUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("textAnnotation.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().iterator().next() instanceof TextAnnotation);
        TextAnnotation ta = (TextAnnotation) process.getFlowElements().iterator().next();
        assertEquals("text annotation", ta.getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testDataObjectUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("dataObject.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().iterator().next() instanceof DataObject);
        DataObject da = (DataObject) process.getFlowElements().iterator().next();
        assertEquals("data object", da.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endMessageEvent.json");
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndErrorEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endErrorEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndSignalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndTerminateEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endTerminateEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("terminate end event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TerminateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testEndCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("endCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testSimpleChainUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("startEvent-task-endEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().size() == 5);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchMessageEvent.json");
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchTimerEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchTimerEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch timer event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchConditionalEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchConditionalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch conditional event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ConditionalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchLinkEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchLinkEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchErrorEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchErrorEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchCancelEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchCancelEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch cancel event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CancelEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchCompensationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateCatchParallelMultipleEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCatchParallelMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch parallel multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowMessageEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowMessageEvent.json");
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowEscalationEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowEscalationEvent.json");
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowLinkEventUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowLinkEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowCompensationUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowCompensationEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowSignalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowSignalEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testIntermediateThrowMultipleUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateThrowMultipleEvent.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testAssociationUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("association.json");
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.NONE, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testAssociationUnidirectionalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("associationOne.json");
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.ONE, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testAssociationBidirectionalUnmarshalling() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("associationBoth.json");
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getFlowElements().get(1);
        Association association = (Association) process.getArtifacts().get(0);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.BOTH, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }

    @Test
    public void testBoundaryEventMultiLineName() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("boundaryEventMultiLineName.json");
        Process process = getRootProcess(definitions);
        Boolean foundElementNameExtensionValue = false;
        BoundaryEvent event = (BoundaryEvent) process.getFlowElements().get(1);
        if(event.getExtensionValues() != null && event.getExtensionValues().size() > 0) {
            for(ExtensionAttributeValue extattrval : event.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();

                List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA, true);

                assertNotNull(metadataExtensions);
                assertTrue(metadataExtensions.size() == 1);

                for(MetaDataType metaType : metadataExtensions) {
                    if(metaType.getName()!= null && metaType.getName().equals("elementname") && metaType.getMetaValue() != null && metaType.getMetaValue().length() > 0) {
                        assertNotNull(metaType.getMetaValue());
                        foundElementNameExtensionValue = true;
                    }
                }
            }
            assertTrue(foundElementNameExtensionValue);
        } else {
            fail("Boundary event has no extension element");
        }
    }

    @Test
    public void testFindContainerForBoundaryEvent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("boundaryEventsContainers.json");
        Process process = getRootProcess(definitions);

        for(FlowElement element : process.getFlowElements()) {
            if (element instanceof BoundaryEvent) {
                BoundaryEvent be = (BoundaryEvent) element;
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                if ("Timer1".equals(element.getName())) {
                    SubProcess sp = (SubProcess) unmarshaller.findContainerForBoundaryEvent(process, be);
                    assertEquals("Subprocess1", sp.getName());
                }

                if ("Timer2".equals(element.getName())) {
                    SubProcess sp = (SubProcess) unmarshaller.findContainerForBoundaryEvent(process, be);
                    assertEquals("Subprocess2", sp.getName());
                }

                if ("Timer3".equals(element.getName())) {
                    Process sp = (Process) unmarshaller.findContainerForBoundaryEvent(process, be);
                    assertEquals("DemoProcess", sp.getName());
                }
            }
        }
    }

    @Test
    public void testCompensationThrowingEvent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("intermediateCompensationEventThrowing.json");
        Process process = getRootProcess(definitions);
        ThrowEvent compensationThrowEvent = (ThrowEvent) process.getFlowElements().get(2);
        assertEquals("Compensate", compensationThrowEvent.getName());
        assertNotNull(compensationThrowEvent.getEventDefinitions());
        assertEquals(1, compensationThrowEvent.getEventDefinitions().size());
        EventDefinition ed = compensationThrowEvent.getEventDefinitions().get(0);
        assertTrue(ed instanceof CompensateEventDefinition);
        CompensateEventDefinition ced = (CompensateEventDefinition) ed;
        assertNotNull(ced.getActivityRef());
        assertEquals("User Task", ced.getActivityRef().getName());
    }

    @Test
    public void testBoundaryEvents() throws Exception {
        final String SUBTIMER_NAME = "SubTimer";
        final String SUBPROCESSMESSAGE_NAME = "SubProcessMessage";
        final String OUTTIMER_NAME = "OutTimer";

        Definitions definitions = loader.loadProcessFromJson("boundaryEvents.json");
        Process process = getRootProcess(definitions);

        assertTrue(containerContainsElement(process, OUTTIMER_NAME));
        assertTrue(containerContainsElement(process, SUBPROCESSMESSAGE_NAME));
        assertFalse(containerContainsElement(process, SUBTIMER_NAME));

        SubProcess subProcess = null;
        for(FlowElement flowElement : process.getFlowElements()) {
            if(flowElement instanceof SubProcess) {
                subProcess = (SubProcess) flowElement;
                break;
            }
        }

        assertNotNull(subProcess);
        assertFalse(containerContainsElement(subProcess, OUTTIMER_NAME));
        assertFalse(containerContainsElement(subProcess, SUBPROCESSMESSAGE_NAME));
        assertTrue(containerContainsElement(subProcess, SUBTIMER_NAME));
    }

    @Test
    public void testBoundaryEventsContainers() throws Exception{
        Definitions definitions = loader.loadProcessFromJson("boundaryEventsContainers.json");
        Process process = getRootProcess(definitions);

        final String TIMER_ONE = "Timer1";
        final String TIMER_TWO = "Timer2";
        final String TIMER_THREE = "Timer3";

        assertFalse(containerContainsElement(process, TIMER_ONE));
        assertFalse(containerContainsElement(process, TIMER_TWO));
        assertTrue(containerContainsElement(process, TIMER_THREE));

        for(FlowElement flowElement : process.getFlowElements()) {
            if ("Subprocess1".equals(flowElement.getName()) && (flowElement instanceof SubProcess)) {
                SubProcess subProcess = (SubProcess) flowElement;
                assertTrue(containerContainsElement(subProcess, TIMER_ONE));
                assertFalse(containerContainsElement(subProcess, TIMER_TWO));
                assertFalse(containerContainsElement(subProcess, TIMER_THREE));
            }

            if ("Subprocess2".equals(flowElement.getName()) && (flowElement instanceof SubProcess)) {
                SubProcess subProcess = (SubProcess) flowElement;
                assertFalse(containerContainsElement(subProcess, TIMER_ONE));
                assertTrue(containerContainsElement(subProcess, TIMER_TWO));
                assertFalse(containerContainsElement(subProcess, TIMER_THREE));
            }
        }
    }

    private boolean containerContainsElement(FlowElementsContainer container, String elementName) {
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
        assertEquals("TheStart", startEvent.getName());
        Task task = (Task) process.getFlowElements().get(1);
        assertEquals("HelloWorldService", task.getName());
        SequenceFlow flow = (SequenceFlow) process.getFlowElements().get(2);
        assertEquals("flow1", flow.getName());
        assertEquals(startEvent, flow.getSourceRef());
        assertEquals(task, flow.getTargetRef());
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
        for(RootElement nextRootElement : def.getRootElements()) {
            if(nextRootElement instanceof Process) {
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
                assertTrue(BOUNDARY_EVENT_NAME + " have no documentation.", flow.getDocumentation().size() > 0);
                assertEquals(DOCUMENTATION_VALUE, flow.getDocumentation().get(0).getText());
                documentationChecked = true;
                break;
            }
        }

        assertTrue("Boundary Event '" + BOUNDARY_EVENT_NAME + "' is not found in the process.", documentationChecked);
    }

    @Test
    public void testDocumentationForSwimlane() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("swimlane.json");
        Process process = getRootProcess(definitions);
        Lane lane = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("Swimlane name is wrong.", lane.getName(), "Documented Swimlane");
        assertEquals("<![CDATA[Some documentation for swimlane.]]>", lane.getDocumentation().get(0).getText());
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
        assertEquals("User Task One", userTask.getName());
        assertEquals("assignedActor", ((FormalExpression)userTask.getResources().get(0).getResourceAssignmentExpression().getExpression()).getBody());

        boolean foundTaskName = false;
        boolean foundGroupId = false;
        for(DataInputAssociation association : userTask.getDataInputAssociations()) {
            if(association.getAssignment() != null) {
                for(Assignment assignment : association.getAssignment()) {
                    String from = ((FormalExpression)assignment.getFrom()).getBody();
                    String to = ((FormalExpression)assignment.getTo()).getBody();
                    if(to.contains("TaskName") && from.equals("taskForAssignedActor")) {
                        foundTaskName = true;
                    }
                    if(to.contains("GroupId") && from.equals("<![CDATA[assignedGroup]]>")) {
                        foundGroupId = true;
                    }
                }
            }
        }

        assertTrue(foundTaskName);
        assertTrue(foundGroupId);
    }

    @Test
    public void testSequenceFlowPointsInsideLane() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("sequenceFlowPointsInsideLane.json");
        BPMNPlane plane = definitions.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for(DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNEdge) {
                BPMNEdge edge = (BPMNEdge) dia;
                List<Point> wayPoints = edge.getWaypoint();
                assertNotNull(wayPoints);
                assertEquals(wayPoints.size(), 2);
                assertEquals(Float.valueOf(wayPoints.get(0).getX()), new Float(252.0));
                assertEquals(Float.valueOf(wayPoints.get(0).getY()), new Float(220.0));

                assertEquals(Float.valueOf(wayPoints.get(1).getX()), new Float(357.0));
                assertEquals(Float.valueOf(wayPoints.get(1).getY()), new Float(220.0));
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
        assertEquals("callActivity", callActivity.getName());
        assertEquals("abc.noCalledElementCallActivity", callActivity.getCalledElement());
    }

    @Test
    public void testDefaultMessageRefForStartMessageEvent() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("defaultMessageStartEvent.json");
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof StartEvent);
        StartEvent startEvent = (StartEvent) process.getFlowElements().get(0);

        assertTrue(startEvent.getEventDefinitions().size() == 1);
        assertTrue(startEvent.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);

        MessageEventDefinition messageEventDef = (MessageEventDefinition) startEvent.getEventDefinitions().iterator().next();

        assertNull(messageEventDef.getMessageRef());
    }

    @Test
    public void testDefaultInterfaceForServiceTask() throws Exception {
        Definitions definitions = loader.loadProcessFromJson("defaultServiceTask.json");
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().get(0) instanceof ServiceTask);
        ServiceTask serviceTask = (ServiceTask) process.getFlowElements().get(0);
        String serviceImplementation = null;
        String serviceInterface = null;
        String serviceOperation = null;

        Iterator<FeatureMap.Entry> iter = serviceTask.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("serviceimplementation")) {
                serviceImplementation = (String) entry.getValue();
            }
            if (entry.getEStructuralFeature().getName().equals("serviceoperation")) {
                serviceOperation = (String) entry.getValue();
            }
            if (entry.getEStructuralFeature().getName().equals("serviceinterface")) {
                serviceInterface = (String) entry.getValue();
            }
        }

        assertEquals(serviceImplementation, "Java");
        assertNull(serviceInterface);
        assertNull(serviceOperation);
    }
}