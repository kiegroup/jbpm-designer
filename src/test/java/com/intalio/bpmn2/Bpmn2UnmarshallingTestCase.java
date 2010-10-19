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
package com.intalio.bpmn2;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.junit.Test;

import com.intalio.bpmn2.impl.Bpmn2JsonUnmarshaller;


/**
 * @author Antoine Toulme Intalio, Inc.
 *
 * A series of tests to check the unmarshalling of json to bpmn2.
 */
public class Bpmn2UnmarshallingTestCase {
    
    private static File getTestJsonFile(String filename) {
        URL fileURL = Bpmn2UnmarshallingTestCase.class.getResource(filename);
        return new File(fileURL.getFile());
    }
    
    @Test
    public void testSimpleDefinitionsUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("empty.json"));
        assertEquals("sampleDef", definitions.getName());
        assertEquals("my doc", definitions.getDocumentation().iterator().next().getText());
        assertEquals("http://www.w3.org/1999/XPath", definitions.getExpressionLanguage());
        assertEquals("http://www.omg.org/bpmn20", definitions.getTargetNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema", definitions.getTypeLanguage());
        assertTrue(definitions.getRootElements().isEmpty());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testSimpleGlobalTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("oneTask.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof GlobalTask);
        GlobalTask task = (GlobalTask) definitions.getRootElements().iterator().next();
        assertEquals("oneTask", task.getName());
        assertEquals("my task doc", task.getDocumentation().iterator().next().getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testTwoGlobalTasksUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("twoTasks.json"));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("pool.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = (Process) definitions.getRootElements().get(0);
        assertEquals("pool", process.getName());
        assertEquals(ProcessType.PRIVATE, process.getProcessType());
        assertTrue(process.isIsClosed());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testLaneUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("pool.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().get(0) instanceof Process);
        Process process = (Process) definitions.getRootElements().get(0);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        Lane l = process.getLaneSets().get(0).getLanes().get(0);
        assertEquals("my first lane", l.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testSequenceFlowUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("sequenceFlow.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
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
    
    @Test
    public void testScriptTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("scriptTask.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalScriptTask task = (GlobalScriptTask) definitions.getRootElements().get(0);
        assertEquals("my script", task.getName());
        assertEquals("git status | grep modified | awk '{print $3}' | xargs echo | xargs git add", task.getScript());
        assertEquals("bash", task.getScriptLanguage());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testUserTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("userTask.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalUserTask task = (GlobalUserTask) definitions.getRootElements().get(0);
        assertEquals("ask user", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testBusinessRuleTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("businessRuleTask.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalBusinessRuleTask task = (GlobalBusinessRuleTask) definitions.getRootElements().get(0);
        assertEquals("call business rule", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testManualTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("manualTask.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalManualTask task = (GlobalManualTask) definitions.getRootElements().get(0);
        assertEquals("pull a lever", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("gateway.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ExclusiveGateway g = (ExclusiveGateway) process.getFlowElements().get(0);
        assertEquals("xor gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testParallelGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("parallelGateway.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ParallelGateway g = (ParallelGateway) process.getFlowElements().get(0);
        assertEquals("parallel gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEventBasedGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("eventBasedGateway.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EventBasedGateway g = (EventBasedGateway) process.getFlowElements().get(0);
        assertEquals("event-based gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testInclusiveGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("inclusiveGateway.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        InclusiveGateway g = (InclusiveGateway) process.getFlowElements().get(0);
        assertEquals("inclusive gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startMessageEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start message event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startEscalationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start escalation event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartCompensationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startCompensationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start compensation event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartParallelMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startParallelMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start parallel multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartSignalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startSignalEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start signal event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartTimerEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startTimerEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start timer event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testGroupUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("group.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        assertTrue(process.getArtifacts().iterator().next() instanceof Group);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testTextAnnotationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("textAnnotation.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        assertTrue(process.getArtifacts().iterator().next() instanceof TextAnnotation);
        TextAnnotation ta = (TextAnnotation) process.getArtifacts().iterator().next();
        assertEquals("text annotation", ta.getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testDataObjectUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("dataObject.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        assertTrue(process.getFlowElements().iterator().next() instanceof DataObject);
        DataObject da = (DataObject) process.getFlowElements().iterator().next();
        assertEquals("data object", da.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testDataStoreUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("dataStore.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof DataStore);
        DataStore da = (DataStore) definitions.getRootElements().iterator().next();
        assertEquals("data store", da.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testMessageUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("message.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof Message);
        Message msg = (Message) definitions.getRootElements().iterator().next();
        assertEquals("message", msg.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endMessageEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endEscalationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndErrorEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endErrorEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndSignalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endSignalEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndTerminateEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endTerminateEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("terminate end event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TerminateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndCompensationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("endCompensationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testSimpleChainUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("startEvent-task-endEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        assertTrue(process.getFlowElements().size() == 5);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchMessageEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchTimerEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchTimerEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch timer event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchEscalationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchConditionalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchConditionalEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch conditional event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ConditionalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchLinkEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchLinkEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchErrorEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchErrorEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchCancelEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchCancelEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch cancel event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CancelEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchCompensationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchCompensationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchParallelMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateCatchParallelMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch parallel multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowMessageEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowEscalationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowLinkEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowLinkEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowCompensationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowCompensationEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowSignalUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowSignalEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowMultipleUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("intermediateThrowMultipleEvent.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testAssociationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("association.json"));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = (Process) definitions.getRootElements().get(0);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getArtifacts().get(0);
        Association association = (Association) process.getArtifacts().get(1);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.NONE, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testAssociationUnidirectionalUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("associationOne.json"));
        Process process = (Process) definitions.getRootElements().get(0);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getArtifacts().get(0);
        Association association = (Association) process.getArtifacts().get(1);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.ONE, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testAssociationBidirectionalUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("associationBoth.json"));
        Process process = (Process) definitions.getRootElements().get(0);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getArtifacts().get(0);
        Association association = (Association) process.getArtifacts().get(1);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.BOTH, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testDoubleLaneUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("doubleLane.json"));
        Process process = (Process) definitions.getRootElements().get(0);
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
    }
    
    /* Disabling test that doesn't pass.
    @Test
    public void testUserTaskDataPassing() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = unmarshaller.unmarshall(getTestJsonFile("userTaskDataPassing.json"));
        Process process = (Process) definitions.getRootElements().get(0);
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
    
    
}
