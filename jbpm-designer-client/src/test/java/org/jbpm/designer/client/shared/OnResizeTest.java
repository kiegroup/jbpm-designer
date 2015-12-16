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
package org.jbpm.designer.client.shared;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.designer.client.DesignerPopUpPresenter;
import org.jbpm.designer.client.DesignerViewImpl;
import org.jbpm.designer.client.DesignerWidgetPresenter;
import org.jbpm.designer.client.DesignerWidgetView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class OnResizeTest {

    @Spy
    private DesignerWidgetView designerWidgetView;

    @Spy
    private DesignerPopUpPresenter designerPopupPresenter;

    @Spy
    private DesignerViewImpl designerViewImpl;

    @GwtMock
    Widget parentWidget;

    @GwtMock
    FlowPanel parentContaner;

    @GwtMock
    DesignerWidgetPresenter designerWidgetPresenter;

    @Test
    public void testDesignerWidgetViewOnResize() {
        when(parentWidget.getOffsetWidth()).thenReturn(100);
        when(parentWidget.getOffsetHeight()).thenReturn(100);
        when(designerWidgetView.getParent()).thenReturn(parentWidget);

        ArgumentCaptor<Integer> pixelSizeCaptorWidth = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pixelSizeCaptorHeight = ArgumentCaptor.forClass(Integer.class);

        designerWidgetView.onResize();

        Mockito.verify(designerWidgetView, Mockito.times(1)).setPixelSize(pixelSizeCaptorWidth.capture(), pixelSizeCaptorHeight.capture());

        assertEquals(100, (int) pixelSizeCaptorWidth.getValue());
        assertEquals(100, (int) pixelSizeCaptorHeight.getValue());
    }

    @Test
    public void testDesignerPopupPresenterOnResize() {
        when(parentWidget.getOffsetWidth()).thenReturn(100);
        when(parentWidget.getOffsetHeight()).thenReturn(100);

        when(designerPopupPresenter.getContainer()).thenReturn(parentContaner);
        when(parentContaner.getParent()).thenReturn(parentWidget);

        ArgumentCaptor<String> pixelSizeCaptorWidth = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pixelSizeCaptorHeight = ArgumentCaptor.forClass(String.class);

        designerPopupPresenter.onResize();

        Mockito.verify(parentContaner, Mockito.times(1)).setWidth(pixelSizeCaptorWidth.capture());
        Mockito.verify(parentContaner, Mockito.times(1)).setHeight(pixelSizeCaptorHeight.capture());

        assertEquals("100px", pixelSizeCaptorWidth.getValue());
        assertEquals("100px", pixelSizeCaptorHeight.getValue());
    }

    @Test
    public void testDesignerViewImplOnResize() {
        when(parentWidget.getOffsetWidth()).thenReturn(100);
        when(parentWidget.getOffsetHeight()).thenReturn(100);
        when(designerViewImpl.getParent()).thenReturn(parentWidget);

        ArgumentCaptor<Integer> pixelSizeCaptorWidth = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pixelSizeCaptorHeight = ArgumentCaptor.forClass(Integer.class);


        when(designerWidgetPresenter.getView()).thenReturn(designerWidgetView);

        designerViewImpl.setDesignerWidget(designerWidgetPresenter);

        designerViewImpl.onResize();

        Mockito.verify(designerViewImpl, Mockito.times(1)).setPixelSize(pixelSizeCaptorWidth.capture(), pixelSizeCaptorHeight.capture());

        assertEquals(100, (int) pixelSizeCaptorWidth.getValue());
        assertEquals(100, (int) pixelSizeCaptorHeight.getValue());
        
        Mockito.verify(designerWidgetView, Mockito.times(1)).setPixelSize(Mockito.anyInt(), Mockito.anyInt());
    }
}
