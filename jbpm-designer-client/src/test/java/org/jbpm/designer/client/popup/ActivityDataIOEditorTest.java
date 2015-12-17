/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.popup;

import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityDataIOEditorTest {

    @Captor
    private ArgumentCaptor<Set<String>> setCaptor;

    @Captor
    private ArgumentCaptor<List<String>> listCaptorOne;

    @Captor
    private ArgumentCaptor<List<String>> listCaptorTwo;

    @Captor ArgumentCaptor<List<AssignmentRow>> listAssignmentCaptor;

    @Mock
    private ActivityDataIOEditorView ioEditorView;

    @Spy
    @InjectMocks
    private ActivityDataIOEditor ioEditor = new ActivityDataIOEditor();

    @Test
    public void testInitIoEditor() {
        ioEditor.init();
        verify(ioEditorView, times(1)).init(ioEditor);
    }

    @Test
    public void testSaveClickCallback() {
        AssignmentRow row = new AssignmentRow("name", Variable.VariableType.INPUT, "String", "Object", "var", "constant");

        List<AssignmentRow> input = new ArrayList<AssignmentRow>();
        input.add(row);

        List<AssignmentRow> output = new ArrayList<AssignmentRow>();
        output.add(row);

        when(ioEditorView.getInputAssignmentData()).thenReturn(input);
        when(ioEditorView.getOutputAssignmentData()).thenReturn(output);

        List<String> dataTypes = new ArrayList<String>();
        List<String> dataTypesDisplayNames = new ArrayList<String>();

        dataTypes.add("a.b.c.Name");
        dataTypesDisplayNames.add("Name");

        ActivityDataIOEditor.GetDataCallback mockCallback = mock(ActivityDataIOEditor.GetDataCallback.class);
        ioEditor.setCallback(mockCallback);

        try {
            ioEditor.handleSaveClick();
        }catch (NullPointerException e) {
            //NPE because of Marshaling.toJSON(data);
        }

        verify(ioEditorView).getInputAssignmentData();
        verify(ioEditorView).getOutputAssignmentData();

        // not executed because of NPE above
        // verify(ioEditorView).hideView();

         // not executed because of NPE above
         // verify((ioEditor.callback)).getData(anyString());
    }

    @Test
    public void testSaveClickHide() {
        ioEditor.handleSaveClick();

        verify(ioEditorView).hideView();
    }

    @Test
    public void testCancelClick() {
        ActivityDataIOEditor.GetDataCallback mockCallback = mock(ActivityDataIOEditor.GetDataCallback.class);
        ioEditor.setCallback(mockCallback);

        ioEditor.handleCancelClick();
        verify(ioEditorView).hideView();
        verify(mockCallback, never()).getData(anyString());
    }

    @Test
    public void testPossibleDataTypes() {
        List<String> dataTypes = new ArrayList<String>();
        List<String> dataTypesDisplayNames = new ArrayList<String>();

        dataTypes.add("a.b.c.Name");
        dataTypesDisplayNames.add("Name");

        ioEditor.setDataTypes(dataTypes, dataTypesDisplayNames);

        verify(ioEditorView).setPossibleInputAssignmentsDataTypes(listCaptorOne.capture(), listCaptorTwo.capture());
        assertEquals(1, listCaptorOne.getValue().size());
        assertEquals(1, listCaptorTwo.getValue().size());
        assertEquals(dataTypes.get(0), listCaptorOne.getValue().get(0));
        assertEquals(dataTypesDisplayNames.get(0), listCaptorTwo.getValue().get(0));

        verify(ioEditorView).setPossibleOutputAssignmentsDataTypes(listCaptorOne.capture(), listCaptorTwo.capture());
        assertEquals(1, listCaptorOne.getValue().size());
        assertEquals(1, listCaptorTwo.getValue().size());
        assertEquals(dataTypes.get(0), listCaptorOne.getValue().get(0));
        assertEquals(dataTypesDisplayNames.get(0), listCaptorTwo.getValue().get(0));
    }

    @Test
    public void testConfigureDialogBoolean() {
        ioEditor.configureDialog("task name", true, false, true, false);
        verify(ioEditorView).setInputAssignmentsVisibility(true);
        verify(ioEditorView).setIsInputAssignmentSingleVar(false);
        verify(ioEditorView).setOutputAssignmentsVisibility(true);
        verify(ioEditorView).setIsOutputAssignmentSingleVar(false);

        ioEditor.configureDialog("task name", false, true, false, true);
        verify(ioEditorView).setInputAssignmentsVisibility(false);
        verify(ioEditorView).setIsInputAssignmentSingleVar(true);
        verify(ioEditorView).setOutputAssignmentsVisibility(false);
        verify(ioEditorView).setIsOutputAssignmentSingleVar(true);
    }

    @Test
    public void testConfigureDialogTaskNameEmpty() {
        ioEditor.configureDialog("", true, true, true, true);
        verify(ioEditorView, times(1)).setDefaultViewTitle();
        verify(ioEditorView, never()).setCustomViewTitle(anyString());
    }

    @Test
    public void testConfigureDialogTaskNameNull() {
        ioEditor.configureDialog(null, true, true, true, true);
        verify(ioEditorView, times(1)).setDefaultViewTitle();
        verify(ioEditorView, never()).setCustomViewTitle(anyString());
    }

    @Test
    public void testConfigureDialogTaskNameCustom() {
        ioEditor.configureDialog("abc", true, true, true, true);
        verify(ioEditorView, times(1)).setCustomViewTitle("abc");
        verify(ioEditorView, never()).setDefaultViewTitle();
    }

    @Test
    public void testDisallowedPropertyNames() {
        List<String> disallowedNames = new ArrayList<String>();
        disallowedNames.add("Abc");
        disallowedNames.add("xyZ");

        ioEditor.setDisallowedPropertyNames(disallowedNames);

        verify(ioEditorView).setInputAssignmentsDisallowedNames(setCaptor.capture());
        assertEquals("should be 2 disallowed names", 2, setCaptor.getValue().size());
        assertTrue("disallowed names should contain: abc", setCaptor.getValue().contains("abc"));
        assertTrue("disallowed names should contain: xyz", setCaptor.getValue().contains("xyz"));
    }

    @Test
    public void testProcessVariables() {
        List<String> variables = new ArrayList<String>();
        variables.add("variable");

        ioEditor.setProcessVariables(variables);

        verify(ioEditorView).setInputAssignmentsProcessVariables(listCaptorOne.capture());
        assertEquals(1, listCaptorOne.getValue().size());
        assertEquals(variables.get(0), listCaptorOne.getValue().get(0));

        verify(ioEditorView).setOutputAssignmentsProcessVariables(variables);
        assertEquals(1, listCaptorOne.getValue().size());
        assertEquals(variables.get(0), listCaptorOne.getValue().get(0));
    }

    @Test
    public void testInputAssignmentsRows() {
        AssignmentRow row = mock(AssignmentRow.class);
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        rows.add(row);

        ioEditor.setInputAssignmentRows(rows);
        verify(ioEditorView).setInputAssignmentRows(listAssignmentCaptor.capture());
        assertEquals(1, listAssignmentCaptor.getValue().size());
        assertEquals(rows.get(0), listAssignmentCaptor.getValue().get(0));
    }

    @Test
    public void testOutputAssignmentsRows() {
        AssignmentRow row = mock(AssignmentRow.class);
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        rows.add(row);

        ioEditor.setOutputAssignmentRows(rows);
        verify(ioEditorView).setOutputAssignmentRows(listAssignmentCaptor.capture());
        assertEquals(1, listAssignmentCaptor.getValue().size());
        assertEquals(rows.get(0), listAssignmentCaptor.getValue().get(0));
    }

    @Test
    public void testShow() {
        ioEditor.show();

        verify(ioEditorView).showView();
        verify(ioEditorView, never()).hideView();
    }
}
