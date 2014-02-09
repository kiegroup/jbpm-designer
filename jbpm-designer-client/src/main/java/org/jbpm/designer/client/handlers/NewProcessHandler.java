package org.jbpm.designer.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.callbacks.DefaultErrorCallback;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class NewProcessHandler extends DefaultNewResourceHandler {

    private static List<String> EXTRA_INVALID_FILENAME_CHARS = Arrays.asList(new String[]{ "+" });

    @Inject
    private Caller<DesignerAssetService> designerAssetService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Bpmn2Type resourceType;

    @Override
    public String getDescription() {
        return DesignerEditorConstants.INSTANCE.businessProcess();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        designerAssetService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                presenter.complete();
                notifySuccess();
                final PlaceRequest place = new PathPlaceRequest( path );
                placeManager.goTo( place );
            }
        }, new DefaultErrorCallback() ).createProcess( pkg.getPackageMainResourcesPath(), buildFileName( resourceType, baseFileName ) );
    }

    @Override
    public void validate( final String fileName,
                          final ValidatorWithReasonCallback callback ) {
        if ( pathLabel.getPath() == null ) {
            ErrorPopup.showMessage(CommonConstants.INSTANCE.MissingPath());
            callback.onFailure();
            return;
        }

        if( !(processAssetFileNameValid(fileName)) ) {
            ErrorPopup.showMessage(CommonConstants.INSTANCE.InvalidFileName0(fileName));
            callback.onFailure();
            return;
        }

        fileNameValidationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                if ( Boolean.TRUE.equals( response ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure( CommonConstants.INSTANCE.InvalidFileName0(fileName) );
                }
            }
        } ).isFileNameValid( fileName );


    }

    private static boolean processAssetFileNameValid(String str) {
        for(String item : EXTRA_INVALID_FILENAME_CHARS) {
            if(str.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
