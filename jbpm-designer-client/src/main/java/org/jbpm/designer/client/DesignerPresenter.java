package org.jbpm.designer.client;

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithFileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CopyPopup;
import org.kie.workbench.common.widgets.client.popups.file.FileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.RenamePopup;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

import static org.uberfire.client.common.ConcurrentChangePopup.*;

@Dependent
@WorkbenchEditor(identifier = "jbpm.designer", supportedTypes = { Bpmn2Type.class })
public class DesignerPresenter {

    public interface View
            extends
            HasBusyIndicator,
            UberView<DesignerPresenter> {

        void setEditorID( final String id );

        void setEditorParamters( final Map<String, String> editorParameters );

        String getEditorID();

        boolean confirmClose();
    }

    @Inject
    private DesignerView view;

    @Inject
    private Caller<DesignerAssetService> assetService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Caller<VFSService> vfsServices;

    @Inject
    private Caller<DeleteService> deleteService;

    @Inject
    private Caller<CopyService> copyService;

    @Inject
    private Caller<RenameService> renameService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Bpmn2Type resourceType;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private MetadataWidget metadataWidget;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    private ObservablePath path;
    private PlaceRequest place;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;
    private Menus menus;

    protected boolean isReadOnly;
    private String version;

    private boolean passedProcessSources;

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        this.path = path;
        this.place = place;

        // read only set to true for now
        this.isReadOnly = true;

        this.version = place.getParameter( "version", null );

        this.path.onRename( new Command() {
            @Override
            public void execute() {
                changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getName(), null ) );
            }
        } );

        this.path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        } );

        this.path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                newConcurrentRename( info.getSource(),
                                     info.getTarget(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             reload();
                                         }
                                     }
                                   ).show();
            }
        } );

        this.path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentDelete info ) {
                newConcurrentDelete( info.getPath(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             placeManager.closePlace( place );
                                         }
                                     }
                                   ).show();
            }
        } );

        this.publishOpenInTab( this );
        this.publishSignalOnAssetDelete( this );
        this.publishSignalOnAssetCopy( this );
        this.publishSignalOnAssetRename( this );
        this.publishSignalOnAssetUpdate( this );
        this.publishClosePlace( this );

        multiPage.addWidget( view,
                             DesignerEditorConstants.INSTANCE.businessProcess() );

        if ( path != null ) {
            assetService.call( new RemoteCallback<String>() {
                @Override
                public void callback( final String editorID ) {
                    view.setEditorID( editorID );
                    String url = GWT.getHostPageBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" );
                    assetService.call( new RemoteCallback<Map<String, String>>() {
                        @Override
                        public void callback( Map<String, String> editorParameters ) {
                            if ( editorParameters != null ) {
                                if ( editorParameters.containsKey( "readonly" ) ) {
                                    isReadOnly = Boolean.valueOf(editorParameters.get( "readonly" ));
                                    if (isReadOnly) {
                                        passedProcessSources = true;
                                    }
                                }
                                if ( editorParameters.containsKey( "processsource" ) ) {
                                    String processSources = editorParameters.get( "processsource" );
                                    if ( processSources != null && processSources.length() > 0 ) {
                                        publishProcessSourcesInfo( editorParameters.get( "processsource" ) );
                                    }
                                    editorParameters.remove( "processsource" );
                                }

                                if ( editorParameters.containsKey( "activenodes" ) ) {
                                    String activeNodes = editorParameters.get( "activenodes" );
                                    if ( activeNodes != null && activeNodes.length() > 0 ) {
                                        publishActiveNodesInfo( editorParameters.get( "activenodes" ) );
                                    }
                                    editorParameters.remove( "activenodes" );
                                }

                                if ( editorParameters.containsKey( "completednodes" ) ) {
                                    String activeNodes = editorParameters.get( "completednodes" );
                                    if ( activeNodes != null && activeNodes.length() > 0 ) {
                                        publishCompletedNodesInfo( editorParameters.get( "completednodes" ) );
                                    }
                                    editorParameters.remove( "completednodes" );
                                }
                                editorParameters.put( "ts", Long.toString( System.currentTimeMillis() ) );
                                view.setEditorParamters( editorParameters );

                                // dont add in instance details view
                                if ( !passedProcessSources ) {
                                    multiPage.addPage( new Page( metadataWidget,
                                                                 CommonConstants.INSTANCE.MetadataTabTitle() ) {
                                        @Override
                                        public void onFocus() {
                                            if ( !metadataWidget.isAlreadyLoaded() ) {
                                                metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                                                metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                                                   isReadOnly ),
                                                                      new HasBusyIndicatorDefaultErrorCallback( metadataWidget ) ).getMetadata( path );
                                            }
                                        }

                                        @Override
                                        public void onLostFocus() {
                                            //Nothing to do
                                        }
                                    } );
                                }

                            }
                        }

                    } ).getEditorParameters( path, editorID, url, place );
                }
            } ).getEditorID();
        }
    }

    @OnMayClose
    public boolean canClose() {
        if ( !view.canSaveDesignerModel( view.getEditorID() ) ) {
            boolean canClose = view.confirmClose();
            if ( canClose ) {
                view.setProcessSaved( view.getEditorID() );
                return canClose;
            } else {
                view.setProcessUnSaved( view.getEditorID() );
                return canClose;
            }
        } else {
            return true;
        }
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    private void makeMenuBar() {
        if ( isReadOnly && version != null) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder.build();
        }
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getName() {
        String fileName = FileNameUtil.removeExtension(this.path, resourceType);

        if ( version != null ) {
            fileName = fileName + " v" + version;
        }

        return DesignerEditorConstants.INSTANCE.businessProcess() + " [" + fileName + "]";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return multiPage;
    }

    private native void publishProcessSourcesInfo( String ps )/*-{
        $wnd.designerprocesssources = function () {
            return ps;
        }
    }-*/;

    private native void publishActiveNodesInfo( String an )/*-{
        $wnd.designeractivenodes = function () {
            return an;
        }
    }-*/;

    private native void publishCompletedNodesInfo( String cn )/*-{
        $wnd.designercompletednodes = function () {
            return cn;
        }
    }-*/;

    private native void publishOpenInTab( DesignerPresenter dp )/*-{
        $wnd.designeropenintab = function (filename, uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::openInTab(Ljava/lang/String;Ljava/lang/String;)(filename, uri);
        }
    }-*/;

    private native void publishSignalOnAssetDelete( DesignerPresenter dp )/*-{
        $wnd.designersignalassetdelete = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetDeleteEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetCopy( DesignerPresenter dp )/*-{
        $wnd.designersignalassetcopy = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetCopyEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetRename( DesignerPresenter dp )/*-{
        $wnd.designersignalassetrename = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetRenameEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetUpdate( DesignerPresenter dp )/*-{
        $wnd.designersignalassetupdate = function () {
            return dp.@org.jbpm.designer.client.DesignerPresenter::assetUpdatedEvent()();
        }
    }-*/;

    private native void publishClosePlace( DesignerPresenter dp )/*-{
        $wnd.designersignalcloseplace = function () {
            dp.@org.jbpm.designer.client.DesignerPresenter::closePlace()();
        }
    }-*/;

    public void closePlace() {
        if ( view.getIsReadOnly( view.getEditorID() ) ) {
            placeManager.forceClosePlace( this.place );
        }
    }

    public void assetCopyEvent( String uri ) {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path mypath ) {
                final CopyPopup popup = new CopyPopup( mypath,
                                                       fileNameValidator,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyService.call( getCopySuccessCallback(),
                                                                                 new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                       details.getNewFileName(),
                                                                                                                                                       details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        } ).get( URIUtil.encode( uri ) );
    }

    public void assetRenameEvent( String uri ) {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path mypath ) {
                final RenamePopup popup = new RenamePopup( mypath,
                                                           fileNameValidator,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameService.call( getRenameSuccessCallback(),
                                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                               details.getNewFileName(),
                                                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        } ).get( URIUtil.encode( uri ) );
    }

    public void assetDeleteEvent( String uri ) {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path mypath ) {
                deleteService.call( getDeleteSuccessCallback( mypath ),
                                    new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( mypath,
                                                                                                            "" );

            }
        } ).get( URIUtil.encode( uri ) );
    }

    public boolean assetUpdatedEvent() {
        if ( concurrentUpdateSessionInfo != null ) {
            newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                 concurrentUpdateSessionInfo.getIdentity(),
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         view.raiseEventSave( view.getEditorID() );
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         view.raiseEventSaveCancel( view.getEditorID() );
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         view.raiseEventReload( view.getEditorID() );
                                     }
                                 }
                               ).show();
            return true;
        } else {
            return false;
        }
    }

    private RemoteCallback<Void> getDeleteSuccessCallback( final Path path ) {
        return new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
                placeManager.forceClosePlace( new PathPlaceRequest( path ) );
            }
        };
    }

    private RemoteCallback<Path> getCopySuccessCallback() {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }

    private RemoteCallback<Path> getRenameSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                placeManager.forceClosePlace( place );
            }
        };
    }

    public void openInTab( String filename,
                           String uri ) {
        PlaceRequest placeRequestImpl = new PathPlaceRequest(
                PathFactory.newPath( this.path.getFileSystem(), filename, uri )
        );
        placeRequestImpl.addParameter( "uuid", uri );
        placeRequestImpl.addParameter( "profile", "jbpm" );
        this.placeManager.goTo( placeRequestImpl );
    }

    private void disableMenus() {
        // TODO no impl for this in designer yet
    }

    private void save() {
        view.setProcessUnSaved( view.getEditorID() );
    }

    private void reload() {
        view.raiseEventReload( view.getEditorID() );
    }
}
