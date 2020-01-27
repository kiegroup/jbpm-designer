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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.type.Bpmn2Type;
import org.jbpm.designer.service.DesignerAssetService;
import org.kie.workbench.common.profile.api.preferences.Profile;
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

    protected NewCaseDefinitionHandler(final Caller<DesignerAssetService> designerAssetService,
                                       final PlaceManager placeManager,
                                       final Bpmn2Type resourceType,
                                       final Event<NewResourceSuccessEvent> newResourceSuccessEvent) {
        this(designerAssetService,
             placeManager,
             resourceType);
        this.newResourceSuccessEvent = newResourceSuccessEvent;
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
        extensions.add(Pair.newPair(DesignerEditorConstants.INSTANCE.CaseIdPrefix(),
                                    caseIdPrefixTextBox));
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        String caseIdPrefix = caseIdPrefixTextBox.getValue();
        caseIdPrefixTextBox.clear();

        designerAssetService.call((RemoteCallback<Path>) path -> {
                                      presenter.complete();

                                      notifySuccess();
                                      newResourceSuccessEvent.fire(new NewResourceSuccessEvent(path));
                                      placeManager.goTo(path);
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
            Optional<WorkspaceProject> workspaceProject = context.getActiveWorkspaceProject();
            if (workspaceProject.isPresent()) {
                designerAssetService.call((RemoteCallback<Boolean>) callback::onSuccess,
                                          new DefaultErrorCallback()).isCaseProject(workspaceProject.get().getRootPath());
            } else {
                callback.onSuccess(false);
            }
        }
    }

    @Override
    public List<Profile> getProfiles() {
        return Arrays.asList(Profile.FULL);
    }

    @Override
    public boolean isProjectAsset() {
        return false;
    }
}
