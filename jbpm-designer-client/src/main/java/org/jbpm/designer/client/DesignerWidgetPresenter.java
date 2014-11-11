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

        void setEditorID( final String id );

        void setEditorParamters( final Map<String, String> editorParameters );

        String getEditorID();

        boolean confirmClose();
    }

    @Inject
    private DesignerView view;

    public void setup( final String editorID,
                       final Map<String, String> editorParameters ) {
        view.setEditorID( editorID );
        view.setEditorParamters( editorParameters );
    }

    public String getEditorID() {
        return view.getEditorID();
    }

    public boolean confirmClose() {
        return view.confirmClose();
    }

    public void raiseEventReload( final String editorID ) {
        view.raiseEventReload( editorID );
    }

    public void raiseEventSave( final String editorID ) {
        view.raiseEventSave( editorID );
    }

    public void raiseEventSaveCancel( String editorID ) {
        view.raiseEventSaveCancel( editorID );
    }

    public void setProcessUnSaved( String editorID ) {
        view.setProcessUnSaved( editorID );
    }

    public boolean getIsReadOnly( String editorID ) {
        return view.getIsReadOnly( editorID );
    }

    public void setProcessSaved( String editorID ) {
        view.setProcessSaved( editorID );

    }

    public boolean canSaveDesignerModel( String editorID ) {
        return view.canSaveDesignerModel( editorID );
    }

    public void setSize( final int width,
                         final int height ) {
        view.setSize( width, height );
    }

    public IsWidget getView() {
        return view;
    }

}
