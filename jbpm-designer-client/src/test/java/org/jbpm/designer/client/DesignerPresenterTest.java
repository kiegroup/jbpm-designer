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

package org.jbpm.designer.client;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DesignerPresenterTest {

    @Mock
    private DesignerView view;

    @Mock
    private Overview overview;

    @Mock
    private DesignerEditorParametersPublisher designerEditorParametersPublisher;

    @Spy
    private Map<String, String> parameters = new HashMap<String, String>();

    @InjectMocks
    @Spy
    private DesignerPresenter presenter =  new DesignerPresenter(view)
    {
        @Override
        protected void resetEditorPages(final Overview overview)
        {
        }
    };

    @Test
    public void testSetup() {
        String id = "testId";

        presenter.setup(parameters, id, overview);

        verify(parameters, times(1)).put("readonly", "false");
        verify(designerEditorParametersPublisher, times(1)).publish(parameters);
        verify(view, times(1)).setup(id, parameters);

        assertEquals(1, parameters.size());
        assertTrue(parameters.containsKey("readonly"));
    }
}
