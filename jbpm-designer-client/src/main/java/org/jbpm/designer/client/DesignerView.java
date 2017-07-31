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

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DesignerView
        extends KieEditorView,
                RequiresResize,
                ProvidesResize {

    boolean canClose();

    boolean getIsReadOnly();

    boolean getIsViewLocked();

    void raiseEventSave();

    void raiseEventCheckSave(String pathURI);

    void raiseEventSaveCancel();

    void raiseEventReload();

    void setup(String editorID,
               Map<String, String> editorParameters);

    void setProcessUnSaved();

    void askOpenInXMLEditor();

    void raiseEventUpdateLock();
}
