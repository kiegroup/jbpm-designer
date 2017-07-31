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

package org.jbpm.designer.client;

import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class DesignerViewImpl
        extends KieEditorViewImpl
        implements DesignerView {

    private DesignerWidgetPresenter designerWidget;

    public DesignerViewImpl() {
    }

    @Inject
    public DesignerViewImpl(DesignerWidgetView view) {
        this.designerWidget = new DesignerWidgetPresenter(view);
        initWidget(designerWidget.getView().asWidget());
        this.publishCallOnReisize(this);
    }

    @Override
    public boolean getIsReadOnly() {
        return designerWidget.getIsReadOnly(designerWidget.getEditorID());
    }

    @Override
    public void askOpenInXMLEditor() {
        designerWidget.askOpenInXMLEditor(designerWidget.getEditorID());
    }

    @Override
    public boolean getIsViewLocked() {
        return designerWidget.getIsViewLocked(designerWidget.getEditorID());
    }

    @Override
    public void raiseEventSave() {
        designerWidget.raiseEventSave(designerWidget.getEditorID());
    }

    @Override
    public void raiseEventUpdateLock() {
        designerWidget.raiseEventUpdateLock(designerWidget.getEditorID());
    }

    @Override
    public void raiseEventCheckSave(String pathURI) {
        designerWidget.raiseEventCheckSave(designerWidget.getEditorID(),
                                           pathURI);
    }

    @Override
    public void raiseEventSaveCancel() {
        designerWidget.raiseEventSaveCancel(designerWidget.getEditorID());
    }

    @Override
    public void raiseEventReload() {
        designerWidget.raiseEventReload(designerWidget.getEditorID());
    }

    @Override
    public void setup(String editorID,
                      Map<String, String> editorParameters) {
        designerWidget.setup(editorID,
                             editorParameters);
    }

    @Override
    public void setProcessUnSaved() {
        designerWidget.setProcessUnSaved(designerWidget.getEditorID());
    }

    private native void publishCallOnReisize(DesignerViewImpl dvi)/*-{
        $wnd.designercallonresize = function () {
            dvi.@org.jbpm.designer.client.DesignerViewImpl::onResize()();
        }
    }-*/;

    @Override
    public boolean canClose() {
        if (!designerWidget.canSaveDesignerModel(designerWidget.getEditorID()) ||
                designerWidget.isProcessValidating(designerWidget.getEditorID())) {
            boolean canClose = designerWidget.confirmClose();
            if (canClose) {
                designerWidget.turnOffValidation(designerWidget.getEditorID());
                designerWidget.setProcessSaved(designerWidget.getEditorID());
                return canClose;
            } else {
                designerWidget.setProcessUnSaved(designerWidget.getEditorID());
                return canClose;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();

        setPixelSize(width,
                     height);

        this.setWidth(width + "px");
        this.setHeight(Math.max(0,
                                height - 5) + "px");

        ((RequiresResize) designerWidget.getView()).onResize();
    }

    public void setDesignerWidget(DesignerWidgetPresenter widgetPresenter) {
        this.designerWidget = widgetPresenter;
    }
}
