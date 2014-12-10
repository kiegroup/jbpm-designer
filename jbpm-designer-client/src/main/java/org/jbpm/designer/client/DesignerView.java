package org.jbpm.designer.client;

import java.util.Map;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DesignerView
        extends KieEditorView {

    boolean canClose();

    boolean getIsReadOnly();

    void raiseEventSave();

    void raiseEventSaveCancel();

    void raiseEventReload();

    void setup(String editorID, Map<String, String> editorParameters);

    void setProcessUnSaved();

}
