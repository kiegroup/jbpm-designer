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

package org.jbpm.designer.client.util;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ComboBoxViewImplTest {

    private static final String LB_VALUE = "LIST_BOX_VALUE";
    private static final String TB_VALUE = "TEXT_BOX_VALUE";

    @Spy
    ComboBoxViewImpl view;

    @Mock
    ComboBoxView.ComboBoxPresenter presenter;

    @Mock
    ComboBoxView.ModelPresenter modelPresenter;

    @Mock
    ValueListBox valueListBox;

    @Mock
    TextBox textBox;

    @Before
    public void init() throws Exception {
        when(valueListBox.getValue()).thenReturn(LB_VALUE);
        when(textBox.getValue()).thenReturn(TB_VALUE);
    }

    @Test
    public void testViewInit() throws Exception {
        ArgumentCaptor<MouseDownHandler> mousDownCaptor = ArgumentCaptor.forClass(MouseDownHandler.class);
        ArgumentCaptor<BlurHandler> blurCaptor = ArgumentCaptor.forClass(BlurHandler.class);
        ArgumentCaptor<FocusHandler> focusCaptor = ArgumentCaptor.forClass(FocusHandler.class);

        view.init(presenter,
                  modelPresenter,
                  valueListBox,
                  textBox,
                  "placeholder");

        verify(valueListBox).addDomHandler(mousDownCaptor.capture(),
                                           eq(MouseDownEvent.getType()));
        mousDownCaptor.getValue().onMouseDown(null);
        assertEquals(true,
                     view.listBoxHasFocus);
        verify(presenter).updateListBoxValues(LB_VALUE);

        verify(valueListBox).addDomHandler(blurCaptor.capture(),
                                           eq(BlurEvent.getType()));
        blurCaptor.getValue().onBlur(null);
        assertEquals(false,
                     view.listBoxHasFocus);

        verify(textBox).addFocusHandler(focusCaptor.capture());
        focusCaptor.getValue().onFocus(null);
        verify(presenter).setCurrentTextValue(TB_VALUE);

        verify(textBox).addBlurHandler(blurCaptor.capture());
        blurCaptor.getValue().onBlur(null);
        verify(presenter).textBoxValueChanged(TB_VALUE);
    }
}
