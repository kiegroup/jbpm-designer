package org.jbpm.designer.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchEditor(identifier = "jbpm.designer", fileTypes = "*.bpmn?")
public class DesignerPresenter {

    public interface View
            extends
            UberView<DesignerPresenter> {
        void setEditorID( final String id );
        void startDesigneInstancer();
    }

    @Inject
    private DesignerView view;

    @Inject
    private Caller<DesignerAssetService> assetService;

    private Path path;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        if(path != null) {
            assetService.call( new RemoteCallback<String>() {
                @Override
                public void callback( String editorID ) {
                    view.setEditorID(editorID);
                    assetService.call( new RemoteCallback<String>() {
                        @Override
                        public void callback( String editorBody ) {
                            // remove the open+close script tags before injecting
                            editorBody = editorBody.replaceAll("<script type=\"text/javascript\">", "");
                            editorBody = editorBody.replaceAll("</script>", "");

                            final Document doc = Document.get();
                            final NodeList<Element> nodes = doc.getElementsByTagName( HeadElement.TAG );
                            final HeadElement head = nodes.getItem( 0 ).cast();
                            appendScriptSource(head, editorBody);

                            view.startDesigneInstancer();
                            //ScriptInjector.fromString(editorBody).setRemoveTag(false).inject();
                        }
                    } ).loadEditorBody(path, editorID);
                }
            } ).getEditorID();
        }
    }

    @WorkbenchPartTitle
    public String getName() {
        return "jBPM Designer - " + this.path.getFileName();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    private void appendScriptSource( final Element element,
                                     final String source ) {
        final ScriptElement scriptElement = Document.get().createScriptElement( source );
        scriptElement.setType( "text/javascript" );
        element.appendChild( scriptElement );
    }
}
