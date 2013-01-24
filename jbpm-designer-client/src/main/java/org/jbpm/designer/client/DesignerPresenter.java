package org.jbpm.designer.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

@Dependent
@WorkbenchEditor(identifier = "jbpm.designer", fileTypes = "bpmn2")
public class DesignerPresenter {

    public interface View
            extends
            UberView<DesignerPresenter> {

        void setJsonModel( final String jsonModel );

    }

    @Inject
    private View view;

    @Inject
    private Bootstrap bootstrap;

    @Inject
    private Caller<DesignerAssetService> assetService;

    private Path path;

    @PostConstruct
    private void bootstrapOryxScripts() {
        bootstrap.init();
        bridgeOnSaveMethod();
    }

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;

        assetService.call( new RemoteCallback<String>() {
            @Override
            public void callback( final String json ) {
                if ( json != null ) {
                    view.setJsonModel( json );
                }
            }
        } ).loadJsonModel( path );
    }

    public native void bridgeOnSaveMethod() /*-{
        var that = this;
        $wnd.designerPresenterOnSave = $entry(function (jsonModel) {
            return that.@org.jbpm.designer.client.DesignerPresenter::onSave(Ljava/lang/String;)(jsonModel);
        });
    }-*/;

    public void onSave( final String jsonModel ) {
        assetService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                //Nothing to do at the moment... error handling would be nice
            }
        } ).saveJsonModel(this.path,
                jsonModel);
    }

    @WorkbenchPartTitle
    public String getName() {
        return "jBPM Designer";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }
}
