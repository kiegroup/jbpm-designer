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

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
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
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Spy
    private Map<String, String> parameters = new HashMap<>();

    private DesignerPresenter presenter;

    @Before
    public void setup() {
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
            }

            @Override
            protected void resetEditorPages(final Overview overview) {
            }
        });
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
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

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
