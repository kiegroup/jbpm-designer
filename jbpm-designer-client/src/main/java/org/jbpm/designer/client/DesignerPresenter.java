package org.jbpm.designer.client;

import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.*;

@Dependent
@WorkbenchEditor(identifier = "jbpm.designer", supportedTypes = {Bpmn2Type.class})
public class DesignerPresenter
        extends KieEditor {

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
    private Bpmn2Type resourceType;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;
    private DesignerView view;

    @Inject
    public DesignerPresenter(final DesignerView view) {
        super(view);
        this.view = view;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path, place, resourceType);

    }

    @OnMayClose
    public boolean canClose() {
        return view.canClose();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    protected void makeMenuBar() {
        menus = menuBuilder.build();
    }

    @OnClose
    public void onClose() {
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return super.getWidget();
    }

    private native void publishProcessSourcesInfo(String ps)/*-{
        $wnd.designerprocesssources = function () {
            return ps;
        }
    }-*/;

    private native void publishActiveNodesInfo(String an)/*-{
        $wnd.designeractivenodes = function () {
            return an;
        }
    }-*/;

    private native void publishCompletedNodesInfo(String cn)/*-{
        $wnd.designercompletednodes = function () {
            return cn;
        }
    }-*/;

    private native void publishOpenInTab(DesignerPresenter dp)/*-{
        $wnd.designeropenintab = function (filename, uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::openInTab(Ljava/lang/String;Ljava/lang/String;)(filename, uri);
        }
    }-*/;

    private native void publishSignalOnAssetDelete(DesignerPresenter dp)/*-{
        $wnd.designersignalassetdelete = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetDeleteEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetCopy(DesignerPresenter dp)/*-{
        $wnd.designersignalassetcopy = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetCopyEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetRename(DesignerPresenter dp)/*-{
        $wnd.designersignalassetrename = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::assetRenameEvent(Ljava/lang/String;)(uri);
        }
    }-*/;

    private native void publishSignalOnAssetUpdate(DesignerPresenter dp)/*-{
        $wnd.designersignalassetupdate = function () {
            return dp.@org.jbpm.designer.client.DesignerPresenter::assetUpdatedEvent()();
        }
    }-*/;

    private native void publishSignalOnAssetExpectConcurrentUpdate(DesignerPresenter dp)/*-{
        $wnd.designersignalexpectconcurrentupdate = function () {
            return true;
        }
    }-*/;

    private native void publishClosePlace(DesignerPresenter dp)/*-{
        $wnd.designersignalcloseplace = function () {
            dp.@org.jbpm.designer.client.DesignerPresenter::closePlace()();
        }
    }-*/;

    public void closePlace() {
        if (view.getIsReadOnly()) {
            placeManager.forceClosePlace(this.place);
        }
    }

    public void assetCopyEvent(String uri) {
        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path mypath) {
                final CopyPopup popup = new CopyPopup(mypath,
                                                      fileNameValidator,
                                                      new CommandWithFileNameAndCommitMessage() {
                                                          @Override
                                                          public void execute(final FileNameAndCommitMessage details) {
                                                              baseView.showLoading();
                                                              copyService.call(getCopySuccessCallback(),
                                                                               new HasBusyIndicatorDefaultErrorCallback(baseView)).copy(versionRecordManager.getCurrentPath(),
                                                                                                                                        details.getNewFileName(),
                                                                                                                                        details.getCommitMessage());
                                                          }
                                                      });
                popup.show();
            }
        }).get(URIUtil.encode(uri));
    }

    public void assetRenameEvent(String uri) {
        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path mypath) {
                final RenamePopup popup = new RenamePopup(mypath,
                                                          fileNameValidator,
                                                          new CommandWithFileNameAndCommitMessage() {
                                                              @Override
                                                              public void execute(final FileNameAndCommitMessage details) {
                                                                  baseView.showLoading();
                                                                  renameService.call(getRenameSuccessCallback(),
                                                                                     new HasBusyIndicatorDefaultErrorCallback(baseView)).rename(versionRecordManager.getCurrentPath(),
                                                                                                                                                details.getNewFileName(),
                                                                                                                                                details.getCommitMessage());
                                                              }
                                                          });

                popup.show();
            }
        }).get(URIUtil.encode(uri));
    }

    public void assetDeleteEvent(String uri) {
        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path mypath) {
                deleteService.call(getDeleteSuccessCallback(mypath),
                                   new HasBusyIndicatorDefaultErrorCallback(baseView)).delete(mypath,
                                                                                              "");

            }
        }).get(URIUtil.encode(uri));
    }

    public boolean assetUpdatedEvent() {
        if (concurrentUpdateSessionInfo != null) {
            newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                                concurrentUpdateSessionInfo.getIdentity(),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        view.raiseEventSave();
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        view.raiseEventSaveCancel();
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        view.raiseEventReload();
                                    }
                                }
                               ).show();

            concurrentUpdateSessionInfo = null;
            return true;
        } else {
            return false;
        }
    }

    private RemoteCallback<Void> getDeleteSuccessCallback(final Path path) {
        return new RemoteCallback<Void>() {

            @Override
            public void callback(final Void response) {
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
                placeManager.forceClosePlace(new PathPlaceRequest(path));
            }
        };
    }

    private RemoteCallback<Path> getCopySuccessCallback() {
        return new RemoteCallback<Path>() {
            @Override
            public void callback(final Path path) {
                baseView.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
            }
        };
    }

    private RemoteCallback<Path> getRenameSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                baseView.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRenamedSuccessfully()));
                placeManager.forceClosePlace(place);
            }
        };
    }

    public void openInTab(String filename,
                          String uri) {
        PlaceRequest placeRequestImpl = new PathPlaceRequest(
                PathFactory.newPathBasedOn(filename, uri, versionRecordManager.getCurrentPath())
        );

        placeRequestImpl.addParameter("uuid", uri);
        placeRequestImpl.addParameter("profile", "jbpm");
        this.placeManager.goTo(placeRequestImpl);
    }

    private void disableMenus() {
        // TODO no impl for this in designer yet
    }

    @Override
    protected void loadContent() {

        this.publishOpenInTab(this);
        this.publishSignalOnAssetDelete(this);
        this.publishSignalOnAssetCopy(this);
        this.publishSignalOnAssetRename(this);
        this.publishSignalOnAssetUpdate(this);
        this.publishSignalOnAssetExpectConcurrentUpdate(this);
        this.publishClosePlace(this);

        if (versionRecordManager.getCurrentPath() != null) {
            assetService.call(new RemoteCallback<String>() {
                @Override
                public void callback(final String editorID) {
                    String url = GWT.getHostPageBaseURL().replaceFirst("/" + GWT.getModuleName() + "/", "");
                    assetService.call(new RemoteCallback<Map<String, String>>() {
                        @Override
                        public void callback(final Map<String, String> editorParameters) {

                            assetService.call(new RemoteCallback<Overview>() {
                                @Override
                                public void callback(Overview overview) {
                                    setup(editorParameters, editorID, overview);
                                }
                            }).loadOverview(versionRecordManager.getCurrentPath());

                        }

                    }).getEditorParameters(versionRecordManager.getCurrentPath(), editorID, url, place);
                }
            }).getEditorID();
        }
    }

    private void setup(Map<String, String> editorParameters, String editorID, Overview overview) {
        if (editorParameters != null) {

            resetEditorPages(overview);

            if (editorParameters.containsKey("readonly")) {
                isReadOnly = Boolean.valueOf(editorParameters.get("readonly"));
            }
            if (editorParameters.containsKey("processsource")) {
                String processSources = editorParameters.get("processsource");
                if (processSources != null && processSources.length() > 0) {
                    publishProcessSourcesInfo(editorParameters.get("processsource"));
                }
                editorParameters.remove("processsource");
            }

            if (editorParameters.containsKey("activenodes")) {
                String activeNodes = editorParameters.get("activenodes");
                if (activeNodes != null && activeNodes.length() > 0) {
                    publishActiveNodesInfo(editorParameters.get("activenodes"));
                }
                editorParameters.remove("activenodes");
            }

            if (editorParameters.containsKey("completednodes")) {
                String activeNodes = editorParameters.get("completednodes");
                if (activeNodes != null && activeNodes.length() > 0) {
                    publishCompletedNodesInfo(editorParameters.get("completednodes"));
                }
                editorParameters.remove("completednodes");
            }
            editorParameters.put("ts", Long.toString(System.currentTimeMillis()));
            view.setup(editorID, editorParameters);
        }
    }

    protected void save() {
        view.setProcessUnSaved();
    }

    public void reload() {
        concurrentUpdateSessionInfo = null;
        view.raiseEventReload();
    }
}
