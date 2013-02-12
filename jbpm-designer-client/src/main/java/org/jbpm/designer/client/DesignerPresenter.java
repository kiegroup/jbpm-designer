package org.jbpm.designer.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

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
    private PlaceRequest place;

    @OnStart
    public void onStart( final Path path, final PlaceRequest place) {
        this.path = path;
        this.place = place;
        if(path != null) {

            assetService.call( new RemoteCallback<String>() {
                @Override
                public void callback( String editorID ) {
                    view.setEditorID(editorID);
                    String url =   GWT.getHostPageBaseURL().replaceFirst("/"+GWT.getModuleName()+"/", "");
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

                    } ).loadEditorBody(path, editorID, url, place);
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
