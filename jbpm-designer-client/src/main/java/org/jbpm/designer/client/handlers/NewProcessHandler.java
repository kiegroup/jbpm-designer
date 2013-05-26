package org.jbpm.designer.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class NewProcessHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<DesignerAssetService> designerAssetService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Bpmn2Type resourceType;

    @Override
    public String getDescription() {
        return "BPMN2 Process";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public void create( final Path contextPath,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        designerAssetService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                presenter.complete();
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path,
                                                                 "jbpm.designer" );
                placeManager.goTo( place );
            }
        } ).createProcess( contextPath, buildFileName( resourceType, baseFileName ) );
    }

    @Override
    public void acceptPath( Path path,
                            Callback<Boolean, Void> callback ) {
        callback.onSuccess( true );
    }
}
