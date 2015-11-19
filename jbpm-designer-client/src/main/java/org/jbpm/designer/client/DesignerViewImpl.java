/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gwt.user.client.ui.Widget;
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
        designerWidget.raiseEventUpdateLock( designerWidget.getEditorID() );
    }

    @Override
    public void raiseEventCheckSave(String pathURI) {
        designerWidget.raiseEventCheckSave(designerWidget.getEditorID(), pathURI);
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
    public void setup(String editorID, Map<String, String> editorParameters) {
        designerWidget.setup(editorID, editorParameters);
    }

    @Override
    public void setProcessUnSaved() {
        designerWidget.setProcessUnSaved(designerWidget.getEditorID());
    }

    @Override
    public boolean canClose() {
        if (!designerWidget.canSaveDesignerModel(designerWidget.getEditorID())) {
            boolean canClose = designerWidget.confirmClose();
            if (canClose) {
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
        //Layouts.setToFillParent(this);
        final Widget w = getParent();
        final int width = w.getOffsetWidth();
        final int height = w.getOffsetHeight();
        this.setWidth( width + "px" );
        this.setHeight( height + "px" );
        ( (RequiresResize) designerWidget.getView() ).onResize();
    }
}
