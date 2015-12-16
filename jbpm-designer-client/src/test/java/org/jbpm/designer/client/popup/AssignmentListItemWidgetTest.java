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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable;
import org.jbpm.designer.client.util.ComboBox;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.jbpm.designer.client.util.ListBoxValues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the data get/set behaviour of AssignmentListItemWidget
 *
 */
@RunWith(GwtMockitoTestRunner.class)
public class AssignmentListItemWidgetTest {

    @Mock
    ValueListBox<String> dataType;

    @Mock
    TextBox customDataType;

    @Mock
    ValueListBox<String> processVar;

    @Mock
    TextBox constant;

    @Mock ComboBox dataTypeComboBox;

    @Mock ComboBox processVarComboBox;

    @Mock
    DataIOEditorNameTextBox name;

    @Mock
    Button deleteButton;

    @Mock
    DataBinder<AssignmentRow> assignment;

    @Captor
    ArgumentCaptor<String> regExpCaptor;

    //@Spy  // - cannot make Spy because of GWT error
    //@InjectMocks // - cannot InjectMocks because of GWT error
    private AssignmentListItemWidgetViewImpl widget;

    @Before
    public void initTestCase() {
        widget = GWT.create(AssignmentListItemWidgetViewImpl.class);
        AssignmentRow assignmentRow = new AssignmentRow();

        widget.dataType = dataType;
        widget.customDataType = customDataType;
        widget.processVar = processVar;
        widget.constant = constant;
        widget.dataTypeComboBox = dataTypeComboBox;
        widget.name = name;
        widget.deleteButton = deleteButton;
        widget.processVarComboBox = processVarComboBox;
        widget.assignment = assignment;

        Mockito.doCallRealMethod().when(widget).setTextBoxModelValue(any(TextBox.class), anyString());
        Mockito.doCallRealMethod().when(widget).setListBoxModelValue(any(ValueListBox.class), anyString());
        Mockito.doCallRealMethod().when(widget).getModelValue(any(ValueListBox.class));
        Mockito.doCallRealMethod().when(widget).setDataType(anyString());
        Mockito.doCallRealMethod().when(widget).getDataType();
        Mockito.doCallRealMethod().when(widget).setCustomDataType(anyString());
        Mockito.doCallRealMethod().when(widget).getCustomDataType();
        Mockito.doCallRealMethod().when(widget).setConstant(anyString());
        Mockito.doCallRealMethod().when(widget).getConstant();
        Mockito.doCallRealMethod().when(widget).setProcessVar(anyString());
        Mockito.doCallRealMethod().when(widget).getProcessVar();
        Mockito.doCallRealMethod().when(widget).setDataTypes(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).setProcessVariables(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).init();
        Mockito.doCallRealMethod().when(widget).setModel(any(AssignmentRow.class));
        when(widget.getModel()).thenReturn(assignmentRow);

    }

    @Test
    public void testInitWidget() {
        widget.init();
        verify(widget, times(1)).init();

        verify(dataTypeComboBox, times(1)).init(widget, dataType, customDataType, false,
                AssignmentListItemWidgetView.CUSTOM_PROMPT, AssignmentListItemWidgetView.ENTER_TYPE_PROMPT);
        verify(processVarComboBox, times(1)).init(widget, processVar, constant, true,
                AssignmentListItemWidgetView.CONSTANT_PROMPT, AssignmentListItemWidgetView.ENTER_CONSTANT_PROMPT);

        verify(name, times(1)).setRegExp(regExpCaptor.capture(), anyString(), anyString());
        RegExp regExp = RegExp.compile(regExpCaptor.getValue());
        assertEquals(false, regExp.test("a 1"));
        assertEquals(false, regExp.test("a@1"));
        assertEquals(true, regExp.test("a1"));

        verify(customDataType, times(1)).addKeyDownHandler(any(KeyDownHandler.class));
        verify(name, times(1)).addBlurHandler(any(BlurHandler.class));
    }

    @Test
    public void testSetTextBoxModelValue() {
        widget.setTextBoxModelValue(customDataType, "abc");
        verify(widget, times(1)).setCustomDataType("abc");

        widget.setTextBoxModelValue(constant, "abc");
        verify(widget, times(1)).setConstant("abc");
    }

    @Test
    public void testSetListBoxModelValue() {
        widget.setListBoxModelValue(dataType, "abc");
        verify(widget, times(1)).setDataType("abc");

        widget.setListBoxModelValue(processVar, "abc");
        verify(widget, times(1)).setProcessVar("abc");
    }

    @Test
    public void testSetModelInput() {
        when(widget.getVariableType()).thenReturn(Variable.VariableType.INPUT);
        widget.setModel(new AssignmentRow());

        verify(deleteButton).setIcon(IconType.TRASH);
        verify(constant, never()).setVisible(anyBoolean());
        verify(widget).getCustomDataType();
        verify(widget).getDataType();
        verify(widget).getConstant();
        verify(widget).getProcessVar();
    }

    @Test
    public void testSetModelOutput() {
        when(widget.getVariableType()).thenReturn(Variable.VariableType.OUTPUT);
        widget.setModel(new AssignmentRow());

        verify(deleteButton).setIcon(IconType.TRASH);
        verify(constant).setVisible(false);
        verify(widget).getCustomDataType();
        verify(widget).getDataType();
        verify(widget).getConstant();
        verify(widget).getProcessVar();
    }

    @Test
    public void testQuotedConstant() {
        AssignmentRow row = new AssignmentRow();
        row.setConstant("abc");
        when(widget.getModel()).thenReturn(row);
        widget.setModel(row);

        verify(constant).setValue("\"abc\"");
        verify(processVar).setValue("\"abc\"");
    }

    @Test
    public void testSetGetCustomDataType() {
        String customDataType = "com.test.MyType";
        widget.setTextBoxModelValue(widget.customDataType, customDataType);
        String returnedCustomDataType1 = widget.getCustomDataType();
        assertEquals(customDataType, returnedCustomDataType1);

        String returnedCustomDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(customDataType, returnedCustomDataType2);
    }

    @Test
    public void testSetGetDataType() {
        String sDataType = "Boolean";
        widget.setListBoxModelValue(widget.dataType, sDataType);
        String returnedDataType1 = widget.getDataType();
        assertEquals(sDataType, returnedDataType1);

        String returnedDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(sDataType, returnedDataType2);
    }

    @Test
    public void testSetGetConstant() {
        String constant = "any constant";
        widget.setTextBoxModelValue(widget.constant, constant);
        String returnedConstant = widget.getConstant();
        assertEquals(constant, returnedConstant);

        String returnedConstant2 = widget.getModelValue(widget.processVar);
        assertEquals(constant, returnedConstant2);
    }

    @Test
    public void testSetGetProcessVar() {
        String sProcessVar = "username";
        widget.setListBoxModelValue(widget.processVar, sProcessVar);
        String returnedProcessVar1 = widget.getProcessVar();
        assertEquals(sProcessVar, returnedProcessVar1);

        String returnedProcessVar2 = widget.getModelValue(widget.processVar);
        assertEquals(sProcessVar, returnedProcessVar2);
    }

    @Test
    public void testSetDataTypes() {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(null, null, null);
        String sCustomType = "com.test.CustomType";
        widget.setCustomDataType(sCustomType);

        widget.setDataTypes(dataTypeListBoxValues);

        verify(dataTypeComboBox).setListBoxValues(dataTypeListBoxValues);
        verify(dataTypeComboBox).addCustomValueToListBoxValues(sCustomType, "");
    }

    @Test
    public void testSetProcessVariables() {
        ListBoxValues processVarListBoxValues = new ListBoxValues(null, null, null);
        String sConstant = "Mary Wilkins";
        widget.setConstant(sConstant);

        widget.setProcessVariables(processVarListBoxValues);

        verify(processVarComboBox).setListBoxValues(processVarListBoxValues);
        verify(processVarComboBox).addCustomValueToListBoxValues(sConstant, "");
    }
}
