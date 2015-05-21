package org.jbpm.designer.client;

import java.util.Map;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DesignerView
        extends KieEditorView, RequiresResize, ProvidesResize {

    boolean canClose();

    boolean getIsReadOnly();

    boolean getIsViewLocked();

    void raiseEventSave();

    void raiseEventSaveCancel();

    void raiseEventReload();

    void setup(String editorID, Map<String, String> editorParameters);

    void setProcessUnSaved();

}
