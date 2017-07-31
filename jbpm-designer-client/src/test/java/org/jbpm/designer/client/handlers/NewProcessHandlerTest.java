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

package org.jbpm.designer.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.designer.service.DesignerAssetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewProcessHandlerTest {

    @Mock
    private DesignerAssetService designerAssetService;
    private Caller<DesignerAssetService> designerAssetServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    private NewProcessHandler newProcessHandler;

    @Before
    public void setup() {
        designerAssetServiceCaller = new CallerMock<>(designerAssetService);
        newProcessHandler = new NewProcessHandler(designerAssetServiceCaller,
                                                  placeManager,
                                                  null) {
            {
                newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            protected String buildFileName(final String baseFileName,
                                           final ResourceTypeDefinition resourceType) {
                return "fileName";
            }

            @Override
            protected void notifySuccess() {
            }
        };
    }

    @Test
    public void createTest() {
        final NewResourcePresenter presenter = mock(NewResourcePresenter.class);
        final Path path = mock(Path.class);
        doReturn(path).when(designerAssetService).createProcess(any(Path.class),
                                                                anyString());

        newProcessHandler.create(mock(Package.class),
                                 "",
                                 presenter);

        verify(presenter).complete();
        verify(newResourceSuccessEventMock,
               times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(placeManager,
               times(1)).goTo(path);
    }
}
