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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

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
public class ActivityDataIOEditorViewImplTest {

    @Mock
    private ActivityDataIOEditorWidget inputAssignmentsWidget;

    @Mock
    private ActivityDataIOEditorWidget outputAssignmentsWidget;

    @Captor
    private ArgumentCaptor<List<AssignmentRow>> listAssignmentCaptor;

    @GwtMock
    private ActivityDataIOEditorViewImpl view;

    private List<AssignmentRow> rows;

    @Before
    public void setUp() {
        view.inputAssignmentsWidget = inputAssignmentsWidget;
        view.outputAssignmentsWidget = outputAssignmentsWidget;

        doCallRealMethod().when(view).setInputAssignmentRows(any(List.class));
        doCallRealMethod().when(view).setOutputAssignmentRows(any(List.class));

        rows = new ArrayList<AssignmentRow>();
        rows.add(new AssignmentRow("varName", null, null, null, "varName", null));
        rows.add(new AssignmentRow("varName2", null, null, null, "varName2", null));
    }

    @Test
    public void testInputAssignmentsRowsSameSourceAndTargetName() {
        view.setInputAssignmentRows(rows);
        verify(inputAssignmentsWidget, times(1)).setData(listAssignmentCaptor.capture());
        verify(outputAssignmentsWidget, never()).setData(any(List.class));
        verifyForSameSourceAndTargetName();
    }

    @Test
    public void testOutputAssignmentsRowsSameSourceAndTargetName() {
        view.setOutputAssignmentRows(rows);
        verify(outputAssignmentsWidget, times(1)).setData(listAssignmentCaptor.capture());
        verify(inputAssignmentsWidget, never()).setData(any(List.class));
        verifyForSameSourceAndTargetName();
    }

    private void verifyForSameSourceAndTargetName() {
        assertEquals(2, listAssignmentCaptor.getValue().size());
        assertEquals(rows.get(0), listAssignmentCaptor.getValue().get(0));
        assertEquals("varName", listAssignmentCaptor.getValue().get(0).getName());
        assertEquals("varName", listAssignmentCaptor.getValue().get(0).getProcessVar());
        assertEquals(rows.get(1), listAssignmentCaptor.getValue().get(1));
        assertEquals("varName2", listAssignmentCaptor.getValue().get(1).getName());
        assertEquals("varName2", listAssignmentCaptor.getValue().get(1).getProcessVar());
    }

}
