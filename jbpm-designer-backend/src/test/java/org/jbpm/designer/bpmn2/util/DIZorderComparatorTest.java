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

package org.jbpm.designer.bpmn2.util;

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DIZorderComparatorTest {

    private DIZorderComparator comparator;

    @Mock
    private Lane lane;

    @Mock
    private BPMNShape laneShape;

    @Mock
    private Task task;

    @Mock
    private BPMNShape taskShape;

    @Before
    public void setUp() throws Exception {
        comparator = new DIZorderComparator();
        when(laneShape.getBpmnElement()).thenReturn(lane);
        when(taskShape.getBpmnElement()).thenReturn(task);
    }

    @Test
    public void testShapeBeforeEdge() throws Exception {
        assertTrue(comparator.compare(mock(BPMNShape.class),
                                      mock(BPMNEdge.class)) < 0);
    }

    @Test
    public void testEdgeAfterShape() throws Exception {
        assertTrue(comparator.compare(mock(BPMNEdge.class),
                                      mock(BPMNShape.class)) > 0);
    }

    @Test
    public void testLaneBeforeTask() throws Exception {
        assertTrue(comparator.compare(laneShape,
                                      taskShape) < 0);
    }

    @Test
    public void testTaskBeforeLane() throws Exception {
        assertTrue(comparator.compare(taskShape,
                                      laneShape) > 0);
    }
}
