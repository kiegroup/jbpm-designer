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

package org.jbpm.designer.client.shared;

import org.jbpm.designer.client.DesignerPresenter;
import org.jbpm.designer.client.DesignerView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;

import static org.mockito.Mockito.*;

public class ViewLockTest {

    private VersionRecordManager versionRecordManager;
    private UpdatedLockStatusEvent updatedLockStatusEvent;
    private DesignerView designerView;
    private ObservablePath path;
    private ObservablePath otherPath;

    @Before
    public void setUp() {
        designerView = Mockito.mock(DesignerView.class);
        updatedLockStatusEvent = Mockito.mock(UpdatedLockStatusEvent.class);
        versionRecordManager = Mockito.mock(VersionRecordManager.class);
        path = Mockito.mock(ObservablePath.class);
        otherPath = Mockito.mock(ObservablePath.class);
    }

    @Test
    public void testOnLockChangeOnSamePath() {
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(updatedLockStatusEvent.getFile()).thenReturn(path);

        DesignerPresenter designerPresenter = new DesignerPresenter(designerView);
        designerPresenter.setVersionRecordManager(versionRecordManager);

        designerPresenter.onLockChange(updatedLockStatusEvent);

        Mockito.verify(designerView,
                       Mockito.times(1)).raiseEventUpdateLock();
    }

    @Test
    public void testOnLockChangeOnDifferentPath() {
        when(versionRecordManager.getCurrentPath()).thenReturn(path);
        when(updatedLockStatusEvent.getFile()).thenReturn(otherPath);

        DesignerPresenter designerPresenter = new DesignerPresenter(designerView);
        designerPresenter.setVersionRecordManager(versionRecordManager);

        designerPresenter.onLockChange(updatedLockStatusEvent);

        Mockito.verify(designerView,
                       Mockito.times(0)).raiseEventUpdateLock();
    }
}
 