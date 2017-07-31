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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

@Dependent
public class DesignerWidgetPresenter {

    public interface View
            extends
            HasBusyIndicator,
            IsWidget {

        void setEditorID(final String id);

        void setEditorParamters(final Map<String, String> editorParameters);

        String getEditorID();

        boolean confirmClose();
    }

    private DesignerWidgetView view;

    public DesignerWidgetPresenter() {
    }

    @Inject
    public DesignerWidgetPresenter(DesignerWidgetView view) {
        this.view = view;
    }

    public void setup(final String editorID,
                      final Map<String, String> editorParameters) {
        view.setEditorID(editorID);
        view.setEditorParamters(editorParameters);
    }

    public String getEditorID() {
        return view.getEditorID();
    }

    public void askOpenInXMLEditor(String editorID) {
        view.raiseAskOpenInXMLEditor(editorID);
    }

    public boolean confirmClose() {
        return view.confirmClose();
    }

    public void raiseEventReload(final String editorID) {
        view.raiseEventReload(editorID);
    }

    public void raiseEventSave(final String editorID) {
        view.raiseEventSave(editorID);
    }

    public void raiseEventCheckSave(final String editorID,
                                    String pathURI) {
        view.raiseEventCheckSave(editorID,
                                 pathURI);
    }

    public void raiseEventSaveCancel(String editorID) {
        view.raiseEventSaveCancel(editorID);
    }

    public void setProcessUnSaved(String editorID) {
        view.setProcessUnSaved(editorID);
    }

    public boolean getIsReadOnly(String editorID) {
        return view.getIsReadOnly(editorID);
    }

    public boolean getIsViewLocked(String editorID) {
        return view.getIsViewLocked(editorID);
    }

    public void setProcessSaved(String editorID) {
        view.setProcessSaved(editorID);
    }

    public void turnOffValidation(String editorID) {
        view.turnOffValidation(editorID);
    }

    public void raiseEventUpdateLock(String editorID) {
        view.raiseEventUpdateLock(editorID);
    }


    public boolean canSaveDesignerModel(String editorID) {
        return view.canSaveDesignerModel(editorID);
    }

    public boolean isProcessValidating(String editorID) {
        return view.isProcessValidating(editorID);
    }

    public void setSize(final int width,
                        final int height) {
        view.setSize(width,
                     height);
    }

    public IsWidget getView() {
        return view;
    }
}
