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
package org.jbpm.designer.server;

import bpsim.impl.BpsimFactoryImpl;
import org.eclipse.bpmn2.Definitions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BusinessProcessCopyHelperTest {

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private BusinessProcessCopyHelper helper;
    private Bpmn2TypeDefinition bpmn2ResourceType = new Bpmn2TypeDefinition();
    private Path pathSource;
    private Path pathDestination;

    public static final String DEFAULT_PROCESS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
            "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:drools=\"http://www.jboss.org/drools\" \n" +
            "id=\"Definition\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\"> \n" +
            "   <bpmn2:process id=\"testdir.myprocess\" drools:packageName=\"org.jbpm\" drools:version=\"1.0\" name=\"\" isExecutable=\"true\"> \n" +
            "      <bpmn2:startEvent id=\"processStartEvent\" drools:bgcolor=\"#9acd32\" drools:selectable=\"true\" name=\"\"/> \n" +
            "   </bpmn2:process> \n" +
            "   <bpmndi:BPMNDiagram> \n" +
            "      <bpmndi:BPMNPlane bpmnElement=\"testdir.myprocess\"> \n" +
            "         <bpmndi:BPMNShape bpmnElement=\"processStartEvent\"> \n" +
            "            <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"120.0\" y=\"165.0\"/> \n" +
            "         </bpmndi:BPMNShape> \n" +
            "      </bpmndi:BPMNPlane> \n" +
            "   </bpmndi:BPMNDiagram> \n" +
            "</bpmn2:definitions>";

    @Before
    public void setup() {
        pathSource = mock(Path.class);
        pathDestination = mock(Path.class);
        helper = new BusinessProcessCopyHelper(ioService,
                                               bpmn2ResourceType,
                                               commentedOptionFactory);
    }

    @Test
    public void testCopy() {
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyProcess.bpmn2");
        when(pathDestination.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyNewProcess.bpmn2");
        when(pathDestination.getFileName()).thenReturn("MyNewProcess.bpmn2");
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn(DEFAULT_PROCESS);

        helper.postProcess(pathSource,
                           pathDestination);

        final ArgumentCaptor<String> bpmn2ArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               bpmn2ArgumentCaptor.capture(),
                               any(CommentedOption.class));

        final String newBPMN2 = bpmn2ArgumentCaptor.getValue();

        assertNotNull(newBPMN2);

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        try {
            Definitions def = new JbpmProfileImpl().getDefinitions(newBPMN2);
            org.eclipse.bpmn2.Process process = helper.getRootProcess(def);
            assertNotNull(process);
            assertNotNull(process.getId());
            assertThat(process.getId(),
                       containsString("MyNewProcess"));
        } catch (Exception e) {
            fail("Cannot parse new process: " + e.getMessage());
        }
    }

    @Test
    public void testCopyIDWithMultibyteCharsAndSpaces() {
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyProcess.bpmn2");
        when(pathDestination.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyNewProcess.bpmn2");
        when(pathDestination.getFileName()).thenReturn("Эож ты дольорэ     My New Process  어디야.bpmn2");
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn(DEFAULT_PROCESS);

        helper.postProcess(pathSource,
                           pathDestination);

        final ArgumentCaptor<String> bpmn2ArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               bpmn2ArgumentCaptor.capture(),
                               any(CommentedOption.class));

        final String newBPMN2 = bpmn2ArgumentCaptor.getValue();

        assertNotNull(newBPMN2);

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        try {
            Definitions def = new JbpmProfileImpl().getDefinitions(newBPMN2);
            org.eclipse.bpmn2.Process process = helper.getRootProcess(def);
            assertNotNull(process);
            assertNotNull(process.getId());
            assertThat(process.getId(),
                       containsString("MyNewProcessEC96B4EB9494EC95BC"));
        } catch (Exception e) {
            fail("Cannot parse new process: " + e.getMessage());
        }
    }

    @Test
    public void testCopyIDWithInvalidID() {
        when(pathSource.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyProcess.bpmn2");
        when(pathDestination.toURI()).thenReturn("default://p0/Evaluation/src/main/resources/MyNewProcess.bpmn2");
        when(pathDestination.getFileName()).thenReturn("  << my process    >>");
        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn(DEFAULT_PROCESS);

        helper.postProcess(pathSource,
                           pathDestination);

        final ArgumentCaptor<String> bpmn2ArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               bpmn2ArgumentCaptor.capture(),
                               any(CommentedOption.class));

        final String newBPMN2 = bpmn2ArgumentCaptor.getValue();

        assertNotNull(newBPMN2);

        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        try {
            Definitions def = new JbpmProfileImpl().getDefinitions(newBPMN2);
            org.eclipse.bpmn2.Process process = helper.getRootProcess(def);
            assertNotNull(process);
            assertNotNull(process.getId());
            assertThat(process.getId(),
                       containsString("3C3Cmyprocess"));
        } catch (Exception e) {
            fail("Cannot parse new process: " + e.getMessage());
        }
    }
}
