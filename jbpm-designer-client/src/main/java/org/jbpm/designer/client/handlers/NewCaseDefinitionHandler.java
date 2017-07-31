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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.HTMLPanel;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewCaseDefinitionHandler extends DefaultNewResourceHandler {

    private Caller<DesignerAssetService> designerAssetService;

    private PlaceManager placeManager;

    private Bpmn2Type resourceType;

    private TextBox caseIdPrefixTextBox = new TextBox();

    public NewCaseDefinitionHandler() {
    }

    @Inject
    public NewCaseDefinitionHandler(final Caller<DesignerAssetService> designerAssetService,
                                    final PlaceManager placeManager,
                                    final Bpmn2Type resourceType) {
        this.designerAssetService = designerAssetService;
        this.placeManager = placeManager;
        this.resourceType = resourceType;
    }

    @Override
    public String getDescription() {
        return DesignerEditorConstants.INSTANCE.CaseDefinition();
    }

    @Override
    public IsWidget getIcon() {
        return resourceType.getIcon();
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @PostConstruct
    private void setupExtensions() {
        extensions.add(Pair.newPair("CaseIdPrefixLabel",
                                    new HTMLPanel(DesignerEditorConstants.INSTANCE.CaseIdPrefix())));
        extensions.add(Pair.newPair("CaseIdPrefixTextBox",
                                    caseIdPrefixTextBox));
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        String caseIdPrefix = caseIdPrefixTextBox.getValue();
        caseIdPrefixTextBox.clear();

        designerAssetService.call(new RemoteCallback<Path>() {
                                      @Override
                                      public void callback(final Path path) {
                                          presenter.complete();

                                          notifySuccess();
                                          newResourceSuccessEvent.fire(new NewResourceSuccessEvent(path));
                                          placeManager.goTo(path);
                                      }
                                  },
                                  new DefaultErrorCallback()).createCaseDefinition(pkg.getPackageMainResourcesPath(),
                                                                                   buildFileName(baseFileName,
                                                                                                 resourceType),
                                                                                   caseIdPrefix);
    }

    @Override
    public void acceptContext(final Callback<Boolean, Void> callback) {
        if (context == null) {
            callback.onSuccess(false);
        } else {
            if (context.getActiveProject() != null) {
                designerAssetService.call(new RemoteCallback<Boolean>() {
                                              @Override
                                              public void callback(final Boolean isCaseProject) {
                                                  callback.onSuccess(isCaseProject);
                                              }
                                          },
                                          new DefaultErrorCallback()).isCaseProject(context.getActiveProject().getRootPath());
            } else {
                callback.onSuccess(false);
            }
        }
    }
}
