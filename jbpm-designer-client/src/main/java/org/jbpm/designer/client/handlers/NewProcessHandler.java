package org.jbpm.designer.client.handlers;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.guvnor.commons.ui.client.handlers.DefaultNewResourceHandler;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class NewProcessHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DesignerAssetService> designerAssetService;

    @Inject
    private PlaceManager placeManager;

    @Override
    public String getFileType() {
        return "bpmn";
    }

    @Override
    public String getDescription() {
        return "BPMN2 Process";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public void create(String name) {
        final Path path = buildFullPathName( name );

                designerAssetService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( Void aVoid ) {
                        notifySuccess();
                        notifyResourceAdded( path );
                        final PlaceRequest place = new PathPlaceRequest( path,
                                "EnumEditor" );
                        placeManager.goTo( place );
                    }
                } ).createProcess( path );
    }


    @Override
    public void acceptPath(Path path, Callback<Boolean, Void> callback) {
        callback.onSuccess(true);
    }
}
