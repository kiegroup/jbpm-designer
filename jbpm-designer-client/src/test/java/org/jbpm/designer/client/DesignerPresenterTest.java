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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandler;
import org.kie.workbench.common.stunner.bpmn.integration.client.IntegrationHandlerProvider;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DesignerPresenterTest {

    @Mock
    protected BasicFileMenuBuilder menuBuilder;

    @Mock
    protected VersionRecordManager versionRecordManager;

    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected WorkspaceProjectContext workbenchContext;

    @Mock
    private RenameService renameService;
    private CallerMock<RenameService> renameServiceCaller;

    @Mock
    private DesignerView view;

    @Mock
    private Overview overview;

    @Mock
    private DesignerEditorParametersPublisher designerEditorParametersPublisher;

    @Mock
    private RenamePopUpPresenter renamePopUpPresenter;

    @Mock
    private DefaultFileNameValidator fileNameValidator;

    @Mock
    private BaseEditorView baseView;

    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    private Promises promises;

    @Mock
    private MenuItem alertsButtonMenuItem;

    @Mock
    private IntegrationHandlerProvider integrationHandlerProvider;

    @Mock
    private IntegrationHandler integrationHandler;

    @Captor
    private ArgumentCaptor<ClickHandler> clickHandlerCaptor;

    private Button migrateButton;

    @Mock
    private PlaceRequest place;

    @Spy
    private Map<String, String> parameters = new HashMap<>();

    private DesignerPresenter presenter;

    @Before
    public void setup() {
        migrateButton = mock(Button.class);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(Optional.empty());
        promises = new SyncPromises();
        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        renameServiceCaller = new CallerMock<>(renameService);
        presenter = spy(new DesignerPresenter(view) {

            {
                this.fileMenuBuilder = DesignerPresenterTest.this.fileMenuBuilder;
                this.projectController = DesignerPresenterTest.this.projectController;
                this.workbenchContext = DesignerPresenterTest.this.workbenchContext;
                this.versionRecordManager = DesignerPresenterTest.this.versionRecordManager;
                this.designerEditorParametersPublisher = DesignerPresenterTest.this.designerEditorParametersPublisher;
                this.renamePopUpPresenter = DesignerPresenterTest.this.renamePopUpPresenter;
                this.fileNameValidator = DesignerPresenterTest.this.fileNameValidator;
                this.baseView = DesignerPresenterTest.this.baseView;
                this.alertsButtonMenuItemBuilder = DesignerPresenterTest.this.alertsButtonMenuItemBuilder;
                this.promises = DesignerPresenterTest.this.promises;
                this.integrationHandlerProvider = DesignerPresenterTest.this.integrationHandlerProvider;
                this.place = DesignerPresenterTest.this.place;
            }

            @Override
            protected void resetEditorPages(final Overview overview) {
            }

            @Override
            protected Button newButton() {
                return migrateButton;
            }
        });

        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
    }

    @Test
    public void testSetup() {
        String id = "testId";

        presenter.setup(parameters,
                        id,
                        overview);

        verify(parameters,
               times(1)).put("readonly",
                             "false");
        verify(designerEditorParametersPublisher,
               times(1)).publish(parameters);
        verify(view,
               times(1)).setup(id,
                               parameters);

        assertEquals(1,
                     parameters.size());
        assertTrue(parameters.containsKey("readonly"));
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(Mockito.<MenuItem>any());
        verify(fileMenuBuilder).addCopy(Mockito.<Path>any(),
                                        any());
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(Mockito.<Path>any(),
                                          any());
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Command.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testMigrateActionIsAddedToMenuBarWhenIntegrationIsPresent() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        Optional<IntegrationHandler> optional = Optional.of(integrationHandler);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);

        presenter.makeMenuBar();

        verify(presenter).newButton();
        verify(migrateButton).setSize(ButtonSize.SMALL);
        verify(migrateButton).setText(DesignerEditorConstants.INSTANCE.Migrate());
        verify(migrateButton).addClickHandler(any(ClickHandler.class));
    }

    @Test
    public void testMigrateActionIsNotAddedToMenuBarWhenIntegrationNotPresent() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());

        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(Optional.empty());

        presenter.makeMenuBar();

        verifyMigrateActionWasNotAdded();
    }

    @Test
    public void testMigrateActionIsNotAddedToMenuBarWhenIntegrationIsPresentButProjectUpdateIsNotPermitted() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(false)).when(projectController).canUpdateProject(any());

        Optional<IntegrationHandler> optional = Optional.of(integrationHandler);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);
        presenter.makeMenuBar();

        verifyMigrateActionWasNotAdded();
    }

    private void verifyMigrateActionWasNotAdded() {
        verify(presenter, never()).newButton();
        verify(migrateButton, never()).setSize(any(ButtonSize.class));
        verify(migrateButton, never()).setText(anyString());
        verify(migrateButton, never()).addClickHandler(any(ClickHandler.class));
    }

    @Test
    public void testMigrateWhenDirty() {
        testMigrate(true);
    }

    @Test
    public void testMigrateWhenNotDirty() {
        testMigrate(false);
    }

    @SuppressWarnings("unchecked")
    private void testMigrate(boolean isDirty) {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(promises.resolve(true)).when(projectController).canUpdateProject(any());
        ObservablePath currentPath = mock(ObservablePath.class);
        when(versionRecordManager.getCurrentPath()).thenReturn(currentPath);
        when(view.canSaveDesignerModel()).thenReturn(!isDirty);
        Optional<IntegrationHandler> optional = Optional.of(integrationHandler);
        when(integrationHandlerProvider.getIntegrationHandler()).thenReturn(optional);
        presenter.makeMenuBar();
        verify(migrateButton).addClickHandler(clickHandlerCaptor.capture());
        clickHandlerCaptor.getValue().onClick(mock(ClickEvent.class));
        verify(integrationHandler).migrateFromJBPMDesignerToStunner(eq(currentPath), eq(place), eq(isDirty), any(ParameterizedCommand.class));
    }

    @Test
    public void testGetSaveAndRenameWhenAssetIsDirty() {

        final String title = "title";
        final String message = "message";
        final Command doSaveAndRename = mock(Command.class);
        final Command doRename = mock(Command.class);

        doReturn(true).when(presenter).isDirty();
        doReturn(title).when(presenter).getPopupTitle();
        doReturn(message).when(presenter).getMessage();
        doReturn(doSaveAndRename).when(presenter).doSaveAndRename();
        doReturn(doRename).when(presenter).doRename();

        presenter.getSaveAndRename().execute();

        verify(view).showYesNoCancelPopup(title, message, doSaveAndRename, doRename);
    }

    @Test
    public void testGetSaveAndRenameWhenAssetIsNotDirty() {

        final Command doRename = mock(Command.class);

        doReturn(false).when(presenter).isDirty();
        doReturn(doRename).when(presenter).doRename();

        presenter.getSaveAndRename().execute();

        verify(doRename).execute();
    }

    @Test
    public void testIsDirtyWhenDesignerModelCanBeSaved() {

        doReturn(true).when(view).canSaveDesignerModel();

        assertFalse(presenter.isDirty());
    }

    @Test
    public void testIsDirtyWhenDesignerModelCannotBeSaved() {

        doReturn(false).when(view).canSaveDesignerModel();

        assertTrue(presenter.isDirty());
    }

    @Test
    public void testDoSaveAndRename() {

        final Command command = mock(Command.class);

        doReturn(command).when(presenter).doRename();
        doNothing().when(presenter).save(any());

        presenter.doSaveAndRename().execute();

        verify(presenter).save(command);
    }

    @Test
    public void testDoRename() {

        doNothing().when(presenter).openRenamePopUp(any());

        presenter.doRename().execute();

        verify(presenter).openRenamePopUp(any());
    }

    @Test
    public void testOpenRenamePopUp() {

        final ObservablePath observablePath = mock(ObservablePath.class);
        final CommandWithFileNameAndCommitMessage command = mock(CommandWithFileNameAndCommitMessage.class);

        doReturn(command).when(presenter).makeRenameCommand();

        presenter.openRenamePopUp(observablePath);

        verify(renamePopUpPresenter).show(observablePath, fileNameValidator, command);
    }

    @Test
    public void testMakeRenameCommand() {

        final FileNameAndCommitMessage details = mock(FileNameAndCommitMessage.class);
        final ObservablePath observablePath = mock(ObservablePath.class);
        final RemoteCallback successCallback = mock(RemoteCallback.class);
        final HasBusyIndicatorDefaultErrorCallback errorCallback = mock(HasBusyIndicatorDefaultErrorCallback.class);
        final String newFileName = "newFileName";
        final String message = "message";

        doReturn(newFileName).when(details).getNewFileName();
        doReturn(message).when(details).getCommitMessage();
        doReturn(observablePath).when(versionRecordManager).getPathToLatest();
        doReturn(renameServiceCaller).when(presenter).getRenameService();
        doReturn(successCallback).when(presenter).getRenameSuccessCallback(any());
        doReturn(errorCallback).when(presenter).getRenameErrorCallback(any());

        presenter.makeRenameCommand().execute(details);

        verify(renameService).rename(observablePath, newFileName, message);
    }
}
