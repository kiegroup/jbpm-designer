/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client;

import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.jbpm.designer.client.popup.ActivityDataIOEditor;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.Variable;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.notification.DesignerNotificationEvent;
import org.jbpm.designer.service.DesignerAssetService;
import org.jbpm.designer.service.DesignerContent;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandler;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandlerProvider;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
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
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.util.URIUtil;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;

@Dependent
@WorkbenchEditor(identifier = DesignerPresenter.EDITOR_ID, supportedTypes = {Bpmn2Type.class})
public class DesignerPresenter
        extends KieEditor<Path> {

    public static final String EDITOR_ID = "jbpm.designer";

    @Inject
    DesignerEditorParametersPublisher designerEditorParametersPublisher;

    @Inject
    private Caller<DesignerAssetService> assetService;

    @Inject
    private PlaceManager placeManager;

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
    private User user;

    @Inject
    private ActivityDataIOEditor activityDataIOEditor;

    @Inject
    IntegrationHandlerProvider integrationHandlerProvider;

    private DesignerView view;

    private Overview overview;

    @Inject
    public DesignerPresenter(final DesignerView view) {
        super(view);
        this.view = view;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   resourceType);
    }

    @OnMayClose
    public boolean canClose() {
        return view.canClose();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    protected Promise<Void> makeMenuBar() {
        if (workbenchContext.getActiveWorkspaceProject().isPresent()) {
            final WorkspaceProject activeProject = workbenchContext.getActiveWorkspaceProject().get();
            return projectController.canUpdateProject(activeProject).then(canUpdateProject -> {
                if (canUpdateProject) {
                    final ParameterizedCommand<Boolean> onSave = withComments -> {
                        saveWithComments = withComments;
                        saveAction();
                    };
                    fileMenuBuilder
                            .addSave(versionRecordManager.newSaveMenuItem(onSave))
                            .addCopy(versionRecordManager.getCurrentPath(),
                                     assetUpdateValidator)
                            .addRename(getSaveAndRename())
                            .addDelete(versionRecordManager.getPathToLatest(),
                                       assetUpdateValidator);
                    integrationHandlerProvider.getIntegrationHandler().ifPresent(integrationHandler -> fileMenuBuilder.addNewTopLevelMenu(buildMigrateMenuItem(integrationHandler)));
                }

                fileMenuBuilder
                        .addNewTopLevelMenu(versionRecordManager.buildMenu())
                        .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());

                return promises.resolve();
            });
        }

        return promises.resolve();
    }

    @Override
    protected Command getSaveAndRename() {
        return () -> {
            if (isDirty()) {
                view.showYesNoCancelPopup(getPopupTitle(), getMessage(), doSaveAndRename(), doRename());
            } else {
                doRename().execute();
            }
        };
    }

    boolean isDirty() {
        return !view.canSaveDesignerModel();
    }

    String getMessage() {
        return DesignerEditorConstants.INSTANCE.ModelEditorConfirmSaveBeforeRename();
    }

    String getPopupTitle() {
        return DesignerEditorConstants.INSTANCE.Information();
    }

    Command doSaveAndRename() {
        return () -> save(doRename());
    }

    Command doRename() {
        return () -> openRenamePopUp(versionRecordManager.getPathToLatest());
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @OnClose
    @Override
    public void onClose() {
        view.onClose();
        super.onClose();
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

    public void notifyOpenInXMLEditor(@Observes final DesignerNotificationEvent event) {
        if (user.getIdentifier().equals(event.getUserId())) {
            if (event.getNotification() != null && event.getNotification().equals("openinxmleditor")) {
                view.askOpenInXMLEditor();
            }
        }
    }

    public void onLockChange(@Observes UpdatedLockStatusEvent updateLockEvent) {
        if (updateLockEvent.getFile() != null &&
                updateLockEvent.getFile().equals(versionRecordManager.getCurrentPath())) {
            view.raiseEventUpdateLock();
        }
    }

    private MenuItem buildMigrateMenuItem(IntegrationHandler integrationHandler) {
        final Button widget = newButton();
        widget.setSize(ButtonSize.SMALL);
        widget.setText(DesignerEditorConstants.INSTANCE.Migrate());
        widget.addClickHandler(clickEvent -> integrationHandler.migrateFromJBPMDesignerToStunner(versionRecordManager.getCurrentPath(),
                                                                                                 place,
                                                                                                 isDirty(),
                                                                                                 saveCallback -> save(() -> saveCallback.accept(true))));
        return new MenuFactory.CustomMenuBuilder() {
            @Override
            public void push(MenuFactory.CustomMenuBuilder element) {
            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<Widget>() {
                    @Override
                    public Widget build() {
                        return widget.asWidget();
                    }

                    @Override
                    public boolean isEnabled() {
                        return widget.isEnabled();
                    }

                    @Override
                    public void setEnabled(boolean enabled) {
                        widget.setEnabled(enabled);
                    }
                };
            }
        }.build();
    }

    /**
     * keep this method for testing purposes.
     */
    protected Button newButton() {
        return new Button();
    }

    private native void publishOpenInXMLEditorTab(DesignerPresenter dp)/*-{
        $wnd.designeropeninxmleditortab = function (uri) {
            dp.@org.jbpm.designer.client.DesignerPresenter::openInXMLEditorTab(Ljava/lang/String;)(uri);
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

    private native void publishClosePlace(DesignerPresenter dp)/*-{
        $wnd.designersignalcloseplace = function () {
            dp.@org.jbpm.designer.client.DesignerPresenter::closePlace()();
        }
    }-*/;

    private native void publishIsLatest(DesignerPresenter dp)/*-{
        $wnd.designerIsLatest = function () {
            return dp.@org.jbpm.designer.client.DesignerPresenter::isLatest()();
        }
    }-*/;

    private native void publishShowDataIOEditor(DesignerPresenter dp)/*-{
        $wnd.designersignalshowdataioeditor = function (taskname, datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, datatypes, disallowedpropertynames, customassignmentproperties, jscallback) {
            dp.@org.jbpm.designer.client.DesignerPresenter::showDataIOEditor(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(taskname, datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, datatypes, disallowedpropertynames, customassignmentproperties, jscallback);
        }
    }-*/;

    private native void publishGetAssignmentsViewProperty(DesignerPresenter dp)/*-{
        $wnd.designersignalgetassignmentsviewproperty = function (datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, disallowedpropertynames) {
            return dp.@org.jbpm.designer.client.DesignerPresenter::GetAssignmentsViewProperty(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(datainput, datainputset, dataoutput, dataoutputset, processvars, assignments, disallowedpropertynames);
        }
    }-*/;

    public boolean isLatest() {
        return versionRecordManager.isCurrentLatest();
    }

    public void closePlace() {
        if (view.getIsReadOnly()) {
            placeManager.forceClosePlace(this.place);
        }
    }

    public void assetCopyEvent(String uri) {
        vfsServices.call(new RemoteCallback<ObservablePath>() {
            @Override
            public void callback(final ObservablePath mypath) {
                copyPopUpPresenter.show(mypath,
                                        fileNameValidator,
                                        new CommandWithFileNameAndCommitMessage() {
                                            @Override
                                            public void execute(final FileNameAndCommitMessage details) {
                                                baseView.showLoading();
                                                copyService.call(getCopySuccessCallback(copyPopUpPresenter.getView()),
                                                                 getCopyErrorCallback(copyPopUpPresenter.getView())).copy(mypath,
                                                                                                                          details.getNewFileName(),
                                                                                                                          details.getCommitMessage());
                                            }
                                        });
            }
        }).get(URIUtil.encode(uri));
    }

    private HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(baseView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                copyPopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

//    AssignmentData _assignmentData = new AssignmentData("inStr:String,inInt1:Integer,inCustom1:org.jdl.Custom,inStrConst:String,Skippable",
//            "outStr1:String,outInt1:Integer,outCustom1:org.jdl.Custom",
//            "str1:String,int1:Integer,custom1:org.jdl.Custom",
//            "[din]str1->inStr,[din]int1->inInt1,[din]custom1->inCustom1,[din]inStrConst=TheString,[dout]outStr1->str1,[dout]outInt1->int1,[dout]outCustom1->custom1",
//            "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object");

    public void showDataIOEditor(final String taskName,
                                 final String datainput,
                                 final String datainputset,
                                 final String dataoutput,
                                 final String dataoutputset,
                                 final String processvars,
                                 final String assignments,
                                 final String datatypes,
                                 final String disallowedpropertynames,
                                 final String customassignmentproperties,
                                 final JavaScriptObject jscallback) {

        //getDataIOEditorData("{ \"a\":\"hello\" }", jscallback);
        final DesignerPresenter dp = this;
        activityDataIOEditor.setCallback(
                new ActivityDataIOEditor.GetDataCallback() {
                    @Override
                    public void getData(String data) {
                        dp.getDataIOEditorData(data,
                                               jscallback);
                    }
                }
        );

        String inputvars = null;
        boolean hasInputVars = false;
        boolean isSingleInputVar = false;
        if (datainput != null) {
            inputvars = datainput;
            hasInputVars = true;
            isSingleInputVar = true;
        }
        if (datainputset != null) {
            inputvars = datainputset;
            hasInputVars = true;
            isSingleInputVar = false;
        }

        String outputvars = null;
        boolean hasOutputVars = false;
        boolean isSingleOutputVar = false;
        if (dataoutput != null) {
            outputvars = dataoutput;
            hasOutputVars = true;
            isSingleOutputVar = true;
        }
        if (dataoutputset != null) {
            outputvars = dataoutputset;
            hasOutputVars = true;
            isSingleOutputVar = false;
        }

        AssignmentData assignmentData = new AssignmentData(inputvars,
                                                           outputvars,
                                                           processvars,
                                                           assignments,
                                                           datatypes,
                                                           disallowedpropertynames,
                                                           customassignmentproperties);
        assignmentData.setVariableCountsString(hasInputVars,
                                               isSingleInputVar,
                                               hasOutputVars,
                                               isSingleOutputVar);
        activityDataIOEditor.setAssignmentData(assignmentData);
        activityDataIOEditor.setDisallowedPropertyNames(assignmentData.getDisallowedPropertyNames());
        activityDataIOEditor.setInputAssignmentRows(assignmentData.getAssignmentRows(Variable.VariableType.INPUT));
        activityDataIOEditor.setOutputAssignmentRows(assignmentData.getAssignmentRows(Variable.VariableType.OUTPUT));
        activityDataIOEditor.setDataTypes(assignmentData.getDataTypes(),
                                          assignmentData.getDataTypeDisplayNames());
        activityDataIOEditor.setProcessVariables(assignmentData.getProcessVariableNames());
        activityDataIOEditor.setCustomAssignmentsProperties(assignmentData.getCustomAssignmentProperties());

        activityDataIOEditor.configureDialog(taskName,
                                             hasInputVars,
                                             isSingleInputVar,
                                             hasOutputVars,
                                             isSingleOutputVar);
        activityDataIOEditor.show();
    }

    private native void getDataIOEditorData(String assignmentData,
                                            final JavaScriptObject jscallback)/*-{
        try {
            jscallback(assignmentData);
        } catch(e) {
            // continue
        }
        //$wnd.alert("DesignerPresenter.getDataIOEditorData assignmentdata = " + assignmentData);
    }-*/;

    public String GetAssignmentsViewProperty(String datainput,
                                             String datainputset,
                                             String dataoutput,
                                             String dataoutputset,
                                             String processvars,
                                             String assignments,
                                             String disallowedpropertynames) {
        String inputvars = null;
        boolean hasInputVars = false;
        boolean isSingleInputVar = false;
        if (datainput != null) {
            inputvars = datainput;
            hasInputVars = true;
            isSingleInputVar = true;
        }
        if (datainputset != null) {
            inputvars = datainputset;
            hasInputVars = true;
            isSingleInputVar = false;
        }

        String outputvars = null;
        boolean hasOutputVars = false;
        boolean isSingleOutputVar = false;
        if (dataoutput != null) {
            outputvars = dataoutput;
            hasOutputVars = true;
            isSingleOutputVar = true;
        }
        if (dataoutputset != null) {
            outputvars = dataoutputset;
            hasOutputVars = true;
            isSingleOutputVar = false;
        }

        AssignmentData assignmentData = new AssignmentData(inputvars,
                                                           outputvars,
                                                           processvars,
                                                           assignments,
                                                           disallowedpropertynames);
        return assignmentData.getVariableCountsString(hasInputVars,
                                                      isSingleInputVar,
                                                      hasOutputVars,
                                                      isSingleOutputVar);
    }

    public void assetRenameEvent(String uri) {
        vfsServices.call(new RemoteCallback<ObservablePath>() {
            @Override
            public void callback(final ObservablePath mypath) {
                openRenamePopUp(mypath);
            }
        }).get(URIUtil.encode(uri));
    }

    void openRenamePopUp(final ObservablePath observablePath) {

        final CommandWithFileNameAndCommitMessage renameCommand = makeRenameCommand();

        renamePopUpPresenter.show(observablePath,
                                  fileNameValidator,
                                  renameCommand);
    }

    CommandWithFileNameAndCommitMessage makeRenameCommand() {

        return details -> {

            final RenamePopUpPresenter.View view = renamePopUpPresenter.getView();
            final ObservablePath pathToLatest = versionRecordManager.getPathToLatest();
            final RemoteCallback<Path> onSuccess = getRenameSuccessCallback(view);
            final HasBusyIndicatorDefaultErrorCallback onError = getRenameErrorCallback(view);

            baseView.showLoading();
            getRenameService().call(onSuccess, onError).rename(pathToLatest,
                                                               details.getNewFileName(),
                                                               details.getCommitMessage());
        };
    }

    Caller<RenameService> getRenameService() {
        return renameService;
    }

    HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(baseView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                renamePopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    private void refreshTitle() {
        baseView.refreshTitle(getTitleText());
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

    private RemoteCallback<Path> getCopySuccessCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new RemoteCallback<Path>() {
            @Override
            public void callback(final Path path) {
                copyPopupView.hide();
                baseView.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
            }
        };
    }

    RemoteCallback<Path> getRenameSuccessCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                renamePopupView.hide();
                baseView.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemRenamedSuccessfully()));
                placeManager.forceClosePlace(place);
                placeManager.goTo(path);
            }
        };
    }

    public void openInTab(String filename,
                          String uri) {
        PlaceRequest placeRequestImpl = new PathPlaceRequest(
                PathFactory.newPathBasedOn(filename,
                                           uri,
                                           versionRecordManager.getCurrentPath())
        );

        placeRequestImpl.addParameter("uuid",
                                      uri);
        placeRequestImpl.addParameter("profile",
                                      "jbpm");
        this.placeManager.goTo(placeRequestImpl);
    }

    public void openInXMLEditorTab(String uri) {
        vfsServices.call(new RemoteCallback<Path>() {
            @Override
            public void callback(final Path mypath) {
                PlaceRequest placeRequestImpl = new PathPlaceRequest(
                        mypath,
                        "GuvnorTextEditor"
                );
                placeManager.forceClosePlace(place);
                placeManager.goTo(placeRequestImpl);
            }
        }).get(URIUtil.encode(uri));
    }

    private void disableMenus() {
        // TODO no impl for this in designer yet
    }

    @Override
    protected void loadContent() {
        this.publishOpenInTab(this);
        this.publishOpenInXMLEditorTab(this);
        this.publishSignalOnAssetDelete(this);
        this.publishSignalOnAssetCopy(this);
        this.publishSignalOnAssetRename(this);
        this.publishSignalOnAssetUpdate(this);
        this.publishClosePlace(this);
        this.publishShowDataIOEditor(this);
        this.publishGetAssignmentsViewProperty(this);
        this.publishIsLatest(this);

        if (versionRecordManager.getCurrentPath() != null) {
            assetService.call(new RemoteCallback<String>() {
                @Override
                public void callback(final String editorID) {
                    String url = GWT.getHostPageBaseURL().replaceFirst("/" + GWT.getModuleName() + "/",
                                                                       "");
                    assetService.call(new RemoteCallback<Map<String, String>>() {
                                          @Override
                                          public void callback(final Map<String, String> editorParameters) {

                                              assetService.call(new RemoteCallback<DesignerContent>() {
                                                  @Override
                                                  public void callback(DesignerContent content) {
                                                      setup(editorParameters,
                                                            editorID,
                                                            content.getOverview());
                                                  }
                                              }).loadContent(versionRecordManager.getCurrentPath());
                                          }
                                      },
                                      new CommandDrivenErrorCallback(view,
                                                                     new CommandBuilder()
                                                                             .addNoSuchFileException(view,
                                                                                                     menus)
                                                                             .addFileSystemNotFoundException(view,
                                                                                                             menus)
                                                                             .build()) {
                                          @Override
                                          public boolean error(final Message message,
                                                               final Throwable throwable) {
                                              placeManager.forceClosePlace(place);
                                              return super.error(message,
                                                                 throwable);
                                          }
                                      }
                    ).getEditorParameters(versionRecordManager.getCurrentPath(),
                                          editorID,
                                          url,
                                          place);
                }
            }).getEditorID();
        }
    }

    protected void setup(Map<String, String> editorParameters,
                         String editorID,
                         Overview overview) {

        this.overview = overview;

        if (editorParameters != null) {

            resetEditorPages(overview);

            editorParameters.put("readonly",
                                 Boolean.toString(isReadOnly));

            designerEditorParametersPublisher.publish(editorParameters);

            view.setup(editorID,
                       editorParameters);
        }
    }

    protected void save() {
        final ObservablePath latestPath = versionRecordManager.getPathToLatest();

        assetService.call(aVoid -> {
            view.raiseEventCheckSave(saveWithComments, latestPath.toURI());
        }).updateMetadata(latestPath,
                          overview.getMetadata());
    }

    protected void save(final Command onSuccess) {
        final ObservablePath latestPath = versionRecordManager.getPathToLatest();
        assetService.call(aVoid -> {
            view.raiseEventUpdate();
            onSuccess.execute();
        }).updateMetadata(latestPath,
                          overview.getMetadata());
    }

    public void reload() {
        concurrentUpdateSessionInfo = null;
        view.raiseEventReload();
    }

    public void setVersionRecordManager(VersionRecordManager versionRecordManager) {
        this.versionRecordManager = versionRecordManager;
    }
}
