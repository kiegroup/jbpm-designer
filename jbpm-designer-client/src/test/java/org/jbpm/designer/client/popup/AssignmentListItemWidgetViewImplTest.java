/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jbpm.designer.client.shared.AssignmentRow;

import org.jbpm.designer.client.shared.Variable;
import org.jbpm.designer.client.util.ComboBox;
import org.jbpm.designer.client.util.DataIOEditorNameTextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.mocks.EventSourceMock;

import javax.enterprise.event.Event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@WithClassesToStub(ValueListBox.class)
@RunWith(GwtMockitoTestRunner.class)
public class AssignmentListItemWidgetViewImplTest {

    private static final String VARIABLE_NAME = "variableName";
    private static final String CONSTANT_NAME = "constantName";
    private static final String CUST_DATA_TYPE_NAME = "custDataTypeName";
    private static final String DATA_TYPE_NAME = "dataTypeName";

    @Mock
    private DataBinder<AssignmentRow> assignment;

    @Mock
    private DataIOEditorNameTextBox name;

    @Mock
    private Button deleteButton;

    @Mock
    private TextBox customDataType;

    @Mock
    private org.gwtbootstrap3.client.ui.ValueListBox<String> dataType;

    @Mock
    private TextBox constant;

    @Mock
    private org.gwtbootstrap3.client.ui.ValueListBox<String> processVar;

    @Mock
    private ComboBox dataTypeComboBox;

    @Mock
    private ComboBox processVarComboBox;

    @Mock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<BlurHandler> blurHandlerCaptor;

    private AssignmentListItemWidgetViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = new AssignmentListItemWidgetViewImpl();
        view.assignment = assignment;
        view.name = name;
        view.deleteButton = deleteButton;
        view.customDataType = customDataType;
        view.dataType = dataType;
        view.constant = constant;
        view.processVar = processVar;
        view.dataTypeComboBox = dataTypeComboBox;
        view.processVarComboBox = processVarComboBox;
        view.notification = notification;

        AssignmentRow row = new AssignmentRow();
        doReturn(row).when(assignment).getModel();
    }

    @Test
    public void testSetModelInputCustomProcessVar() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(VARIABLE_NAME);
        row.setConstant(null);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(CUST_DATA_TYPE_NAME);
        row.setDataType(null);
        row.setVariableType(Variable.VariableType.INPUT);

        doReturn(row).when(assignment).getModel();

        view.setModel(row);

        verify(assignment, times(1)).setModel(row);
        verify(deleteButton, times(1)).setIcon(IconType.TRASH);
        verify(constant, never()).setVisible(false);
        verify(customDataType, times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(dataType, times(1)).setValue(CUST_DATA_TYPE_NAME);
        verify(processVar, times(1)).setValue(VARIABLE_NAME);
        verify(constant, never()).setValue(anyString());
    }

    @Test
    public void testSetModelOutputNormalConstant() {
        AssignmentRow row = new AssignmentRow();
        row.setProcessVar(null);
        row.setConstant(CONSTANT_NAME);
        row.setName(VARIABLE_NAME);
        row.setCustomDataType(null);
        row.setDataType(DATA_TYPE_NAME);
        row.setVariableType(Variable.VariableType.OUTPUT);

        doReturn(row).when(assignment).getModel();

        view.setModel(row);

        verify(assignment, times(1)).setModel(row);
        verify(deleteButton, times(1)).setIcon(IconType.TRASH);
        verify(constant, times(1)).setVisible(false);
        verify(customDataType, never()).setValue(DATA_TYPE_NAME);
        verify(dataType, times(1)).setValue(DATA_TYPE_NAME);
        verify(constant, times(1)).setValue("\"" + CONSTANT_NAME + "\"");
        verify(processVar, times(1)).setValue("\"" + CONSTANT_NAME + "\"");
    }

    @Test
    public void testSetTextBoxModelValueCustomDataType() {
        assertNull(view.getModel().getCustomDataType());
        view.setTextBoxModelValue(customDataType, "abc");
        assertEquals("abc", view.getModel().getCustomDataType());
        assertNull(view.getModel().getConstant());
        assertEquals("abc", view.getModelValue(dataType));
    }

    @Test
    public void testSetTextBoxModelValueConstant() {
        assertNull(view.getModel().getConstant());
        view.setTextBoxModelValue(constant, "abc");
        assertEquals("abc", view.getModel().getConstant());
        assertNull(view.getModel().getCustomDataType());
        assertEquals("abc", view.getModelValue(processVar));
    }

    @Test
    public void testSetListBoxModelValueDataType() {
        assertNull(view.getModel().getDataType());
        view.setListBoxModelValue(dataType, "abc");
        assertEquals("abc", view.getModel().getDataType());
        assertNull(view.getModel().getCustomDataType());
        assertNull(view.getModel().getProcessVar());
        assertEquals("abc", view.getModelValue(dataType));
    }

    @Test
    public void testSetListBoxModelValueProcessVar() {
        assertNull(view.getModel().getProcessVar());
        view.setListBoxModelValue(processVar, "abc");
        assertEquals("abc", view.getModel().getProcessVar());
        assertNull(view.getModel().getConstant());
        assertNull(view.getModel().getDataType());
        assertEquals("abc", view.getModelValue(processVar));
    }

    @Test
    public void testDataTypeHandlerSpace() {
        view.init();

        verify(customDataType, times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();

        doReturn(Integer.valueOf(' ')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent, times(1)).preventDefault();
    }

    @Test
    public void testDataTypeHandlerAlphabetical() {
        view.init();

        verify(customDataType, times(1)).addKeyDownHandler(keyDownHandlerCaptor.capture());
        KeyDownHandler handler = keyDownHandlerCaptor.getValue();

        doReturn(Integer.valueOf('a')).when(keyDownEvent).getNativeKeyCode();
        handler.onKeyDown(keyDownEvent);
        verify(keyDownEvent, never()).preventDefault();
    }

    @Test
    public void testNameBlurHandler() {
        ActivityDataIOEditorWidget parent = mock(ActivityDataIOEditorWidget.class);
        when(parent.isDuplicateName(anyString())).thenReturn(true);
        doReturn("anyName").when(name).getText();

        view.setAllowDuplicateNames(false, "ErrorMessage");
        view.setParentWidget(parent);
        view.init();

        verify(name, times(1)).addBlurHandler(blurHandlerCaptor.capture());
        BlurHandler handler = blurHandlerCaptor.getValue();

        handler.onBlur(mock(BlurEvent.class));

        verify(parent, times(1)).isDuplicateName("anyName");
        verify(notification, times(1)).fire(new NotificationEvent("ErrorMessage", NotificationEvent.NotificationType.ERROR));
        verify(name, times(1)).setValue("");
    }

}
