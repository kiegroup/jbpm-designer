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
package org.jbpm.designer.test.bpmn2;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
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
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.junit.Test;

/**
 * @author Antoine Toulme
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
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("empty.json"), "").getContents().get(0));
        assertEquals("<![CDATA[my doc]]>", definitions.getDocumentation().iterator().next().getText());
        assertEquals("http://www.w3.org/1999/XPath", definitions.getExpressionLanguage());
        assertEquals("http://www.omg.org/bpmn20", definitions.getTargetNamespace());
        assertEquals("http://www.w3.org/2001/XMLSchema", definitions.getTypeLanguage());
        assertTrue(definitions.getRootElements().size() == 1);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    //@Test
    // removing until we start supporting global tasks
    public void testSimpleGlobalTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("oneTask.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("twoTask.json"), "").getContents().get(0));
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
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("pool.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("pool.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("sequenceFlow.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("scriptTask.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalScriptTask task = (GlobalScriptTask) definitions.getRootElements().get(0);
        assertEquals("my script", task.getName());
        assertEquals("git status | grep modified | awk '{print $3}' | xargs echo | xargs git add", task.getScript());
        assertEquals("bash", task.getScriptLanguage());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    //@Test
    // removing until we start supporting global tasks
    public void testUserTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("userTask.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalUserTask task = (GlobalUserTask) definitions.getRootElements().get(0);
        assertEquals("ask user", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    //@Test
    // removing until we start supporting global tasks
    public void testBusinessRuleTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("businessRuleTask.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalBusinessRuleTask task = (GlobalBusinessRuleTask) definitions.getRootElements().get(0);
        assertEquals("call business rule", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    //@Test
    // removing until we start supporting global tasks
    public void testManualTaskUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("manualTask.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        GlobalManualTask task = (GlobalManualTask) definitions.getRootElements().get(0);
        assertEquals("pull a lever", task.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("gateway.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ExclusiveGateway g = (ExclusiveGateway) process.getFlowElements().get(0);
        assertEquals("xor gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testParallelGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("parallelGateway.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ParallelGateway g = (ParallelGateway) process.getFlowElements().get(0);
        assertEquals("parallel gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEventBasedGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("eventBasedGateway.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EventBasedGateway g = (EventBasedGateway) process.getFlowElements().get(0);
        assertEquals("event-based gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testInclusiveGatewayUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("inclusiveGateway.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        InclusiveGateway g = (InclusiveGateway) process.getFlowElements().get(0);
        assertEquals("inclusive gateway", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startMessageEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start message event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startEscalationEvent.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startCompensationEvent.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartParallelMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startParallelMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start parallel multiple event", g.getName());
        //TODO multiple event definitions ???
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testStartSignalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startSignalEvent.json"), "").getContents().get(0));
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
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startTimerEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        StartEvent g = (StartEvent) process.getFlowElements().get(0);
        assertEquals("start timer event", g.getName());
        assertTrue(g.getEventDefinitions().size() == 1);
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    /**@Test
     * this test needs to be revised (the json) 
    public void testGroupUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("group.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getArtifacts().iterator().next() instanceof Group);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }**/
    
    @Test
    public void testTextAnnotationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("textAnnotation.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        assertTrue(process.getArtifacts().iterator().next() instanceof TextAnnotation);
        TextAnnotation ta = (TextAnnotation) process.getArtifacts().iterator().next();
        assertEquals("text annotation", ta.getText());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testDataObjectUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("dataObject.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        assertTrue(process.getFlowElements().iterator().next() instanceof DataObject);
        DataObject da = (DataObject) process.getFlowElements().iterator().next();
        assertEquals("data object", da.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testDataStoreUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("dataStore.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        assertTrue(definitions.getRootElements().iterator().next() instanceof DataStore);
        DataStore da = (DataStore) definitions.getRootElements().iterator().next();
        assertEquals("data store", da.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testMessageUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("message.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        assertTrue(definitions.getRootElements().iterator().next() instanceof Message);
        Message msg = (Message) definitions.getRootElements().iterator().next();
        assertEquals("message", msg.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endMessageEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endEscalationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndErrorEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endErrorEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndSignalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endSignalEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndTerminateEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endTerminateEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("terminate end event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TerminateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testEndCompensationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("endCompensationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        EndEvent g = (EndEvent) process.getFlowElements().get(0);
        assertEquals("end compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testSimpleChainUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("startEvent-task-endEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions); 
        assertTrue(process.getFlowElements().size() == 5);
        assertTrue(process.getLaneSets().size() == 1);
        assertTrue(process.getLaneSets().get(0).getLanes().size() == 1);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchMessageEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchTimerEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchTimerEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch timer event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof TimerEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchEscalationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchConditionalEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchConditionalEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch conditional event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ConditionalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchLinkEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchLinkEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchErrorEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchErrorEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch error event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof ErrorEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchCancelEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchCancelEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch cancel event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CancelEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchCompensationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchCompensationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateCatchParallelMultipleEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateCatchParallelMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        CatchEvent g = (CatchEvent) process.getFlowElements().get(0);
        assertEquals("catch parallel multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowMessageEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowMessageEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 3);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw message event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof MessageEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowEscalationEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowEscalationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 2);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw escalation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof EscalationEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowLinkEventUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowLinkEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw link event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof LinkEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowCompensationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowCompensationEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw compensation event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof CompensateEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowSignalUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowSignalEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw signal event", g.getName());
        assertTrue(g.getEventDefinitions().iterator().next() instanceof SignalEventDefinition);
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testIntermediateThrowMultipleUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("intermediateThrowMultipleEvent.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
        ThrowEvent g = (ThrowEvent) process.getFlowElements().get(0);
        assertEquals("throw multiple event", g.getName());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    @Test
    public void testAssociationUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("association.json"), "").getContents().get(0));
        assertTrue(definitions.getRootElements().size() == 1);
        Process process = getRootProcess(definitions);
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
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("associationOne.json"), "").getContents().get(0));
        Process process = getRootProcess(definitions);
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
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("associationBoth.json"), "").getContents().get(0));
        Process process = getRootProcess(definitions);
        Task g = (Task) process.getFlowElements().get(0);
        assertEquals("task", g.getName());
        TextAnnotation textA = (TextAnnotation) process.getArtifacts().get(0);
        Association association = (Association) process.getArtifacts().get(1);
        assertEquals(g, association.getSourceRef());
        assertEquals(textA, association.getTargetRef());
        assertEquals(AssociationDirection.BOTH, association.getAssociationDirection());
        definitions.eResource().save(System.out, Collections.emptyMap());
    }
    
    /* Disabling test as no support for child lanes yet
    @Test
    public void testDoubleLaneUnmarshalling() throws Exception {
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions definitions = ((Definitions) unmarshaller.unmarshall(getTestJsonFile("doubleLane.json"), "").getContents().get(0));
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
    
}
