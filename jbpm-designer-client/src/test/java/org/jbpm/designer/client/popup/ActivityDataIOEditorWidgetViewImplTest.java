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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActivityDataIOEditorWidgetViewImplTest {

    @Mock
    ActivityDataIOEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @GwtMock
    private ListWidget<AssignmentRow, AssignmentListItemWidgetViewImpl> assignments;

    @GwtMock
    private ActivityDataIOEditorWidgetViewImpl view;

    @Captor
    private ArgumentCaptor<List<AssignmentRow>> captor;

    private List<AssignmentRow> rows;

    @Before
    public void setUp() {
        view.assignments = assignments;
        view.addVarButton = button;

        doCallRealMethod().when(view).setAssignmentRows(any(List.class));
        doCallRealMethod().when(view).init(any(ActivityDataIOEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).handleAddVarButton(any(ClickEvent.class));

        rows = new ArrayList<AssignmentRow>();
        rows.add(new AssignmentRow("varName", null, null, null, "varName", null));
        rows.add(new AssignmentRow("varName2", null, null, null, "varName2", null));
    }

    @Test
    public void testInit() {
        view.init(presenter);

        verify(button, timeout(1)).setText(DesignerEditorConstants.INSTANCE.Add());
        verify(button, timeout(1)).setIcon(IconType.PLUS);
    }

    @Test
    public void testHandleAddVarButton() {
        view.init(presenter);
        view.handleAddVarButton(mock(ClickEvent.class));
        verify(presenter, times(1)).handleAddClick();
    }

    @Test
    public void testAssignmentsRowsSameSourceAndTarget() {
        view.setAssignmentRows(rows);
        verify(assignments, times(1)).setValue(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals("varName", captor.getValue().get(0).getName());
        assertEquals("varName", captor.getValue().get(0).getProcessVar());
        assertEquals("varName2", captor.getValue().get(1).getName());
        assertEquals("varName2", captor.getValue().get(1).getProcessVar());
    }
}
