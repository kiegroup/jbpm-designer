package org.jbpm.designer.client;

import java.util.Map;

public interface DesignerView
        /*extends KieEditorView, RequiresResize, ProvidesResize*/ {

    boolean canClose();

    boolean getIsReadOnly();

    void raiseEventSave();

    void raiseEventSaveCancel();

    void raiseEventReload();

    void setup(String editorID, Map<String, String> editorParameters);

    void setProcessUnSaved();

}
