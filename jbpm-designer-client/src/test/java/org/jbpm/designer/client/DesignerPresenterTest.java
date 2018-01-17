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

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Overview;
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
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
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
    private DesignerView view;

    @Mock
    private Overview overview;

    @Mock
    private DesignerEditorParametersPublisher designerEditorParametersPublisher;

    @Spy
    private Map<String, String> parameters = new HashMap<String, String>();

    private DesignerPresenter presenter;

    @Before
    public void setup() {
        presenter = spy(new DesignerPresenter(view) {

            {
                this.fileMenuBuilder = DesignerPresenterTest.this.fileMenuBuilder;
                this.projectController = DesignerPresenterTest.this.projectController;
                this.workbenchContext = DesignerPresenterTest.this.workbenchContext;
                this.versionRecordManager = DesignerPresenterTest.this.versionRecordManager;
                this.designerEditorParametersPublisher = DesignerPresenterTest.this.designerEditorParametersPublisher;
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
        doReturn(mock(WorkspaceProject.class)).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(mock(WorkspaceProject.class)).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
    }
}
