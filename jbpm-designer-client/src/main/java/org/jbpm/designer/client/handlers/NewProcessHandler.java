package org.jbpm.designer.client.handlers;

//@ApplicationScoped
public class NewProcessHandler /*extends DefaultNewResourceHandler*/ {

//    @Inject
//    private Caller<DesignerAssetService> designerAssetService;
//
//    @Inject
//    private PlaceManager placeManager;
//
//    @Inject

//    @Override
//    public String getDescription() {
//    }
//
//    public IsWidget getIcon() {
//        return null;
//    }
//
//    @Override
//    public ResourceTypeDefinition getResourceType() {
//        return resourceType;
//    }
//
//    @Override
//    public void create( final Package pkg,
//                        final String baseFileName,
//                        final NewResourcePresenter presenter ) {
//        designerAssetService.call( new RemoteCallback<Path>() {
//            @Override
//            public void callback( final Path path ) {
//                presenter.complete();
//                notifySuccess();
//                final PlaceRequest place = new PathPlaceRequest( path );
//                placeManager.goTo( place );
//            }
//        }, new DefaultErrorCallback() ).createProcess( pkg.getPackageMainResourcesPath(), buildFileName( baseFileName,
//                                                                                                         resourceType ) );
//    }
//
//    @Override
//    public void validate( final String baseFileName,
//                          final ValidatorWithReasonCallback callback ) {
//        if ( pathLabel.getPath() == null ) {
//            ErrorPopup.showMessage( CommonConstants.INSTANCE.MissingPath() );
//            callback.onFailure();
//            return;
//        }
//
//        final String fileName = buildFileName( baseFileName,
//                                               getResourceType() );
//
//        validationService.call( new RemoteCallback<Boolean>() {
//            @Override
//            public void callback( final Boolean response ) {
//                if ( Boolean.TRUE.equals( response ) ) {
//                    callback.onSuccess();
//                } else {
//                    callback.onFailure( CommonConstants.INSTANCE.InvalidFileName0( baseFileName ) );
//                }

}
