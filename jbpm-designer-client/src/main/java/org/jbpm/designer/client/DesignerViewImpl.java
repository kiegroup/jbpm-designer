package org.jbpm.designer.client;

import java.util.Map;
import javax.inject.Inject;

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
    public void raiseEventSave() {
        designerWidget.raiseEventSave(designerWidget.getEditorID());
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
}
