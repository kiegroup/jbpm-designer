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
import java.net.URL;
import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.junit.Test;

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
}
