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
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
    protected ProjectContext workbenchContext;

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
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class));
    }
}
