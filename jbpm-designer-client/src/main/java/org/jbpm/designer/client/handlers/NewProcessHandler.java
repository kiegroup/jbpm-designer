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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.DesignerEditorResources;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewProcessHandler extends DefaultNewResourceHandler {

    private Caller<DesignerAssetService> designerAssetService;

    private PlaceManager placeManager;

    private Bpmn2Type resourceType;

    public NewProcessHandler() {
    }

    @Inject
    public NewProcessHandler(final Caller<DesignerAssetService> designerAssetService,
                             final PlaceManager placeManager,
                             final Bpmn2Type resourceType) {
        this.designerAssetService = designerAssetService;
        this.placeManager = placeManager;
        this.resourceType = resourceType;
    }

    @Override
    public String getDescription() {
        return DesignerEditorConstants.INSTANCE.businessProcess();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(DesignerEditorResources.INSTANCE.images().typeForm());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        designerAssetService.call(new RemoteCallback<Path>() {
                                      @Override
                                      public void callback(final Path path) {
                                          presenter.complete();
                                          notifySuccess();
                                          newResourceSuccessEvent.fire(new NewResourceSuccessEvent(path));
                                          placeManager.goTo(path);
                                      }
                                  },
                                  new DefaultErrorCallback()).createProcess(pkg.getPackageMainResourcesPath(),
                                                                            buildFileName(baseFileName,
                                                                                          resourceType));
    }

    @Override
    public int order() {
        return -30;
    }
}
