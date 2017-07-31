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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.designer.client.parameters.DesignerEditorParametersPublisher;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "jbpm.designer.popup", size = WorkbenchPopup.WorkbenchPopupSize.LARGE)
public class DesignerPopUpPresenter extends Composite implements RequiresResize {

    @Inject
    private DesignerWidgetPresenter designerWidget;

    @Inject
    private Caller<DesignerAssetService> assetService;

    @Inject
    private DesignerEditorParametersPublisher designerEditorParametersPublisher;

    protected boolean isReadOnly;

    private FlowPanel container = new FlowPanel();

    @PostConstruct
    public void init() {
        container.clear();
        container.add(designerWidget.getView());
    }

    @OnOpen
    public void onOpen() {
        designerWidget.setSize(1000,
                               600);
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        // read only set to true for now
        this.isReadOnly = true;

        if (place instanceof PathPlaceRequest) {
            assetService.call(new RemoteCallback<String>() {
                @Override
                public void callback(final String editorID) {
                    String url = GWT.getHostPageBaseURL().replaceFirst("/" + GWT.getModuleName() + "/",
                                                                       "");
                    assetService.call(new RemoteCallback<Map<String, String>>() {
                        @Override
                        public void callback(Map<String, String> editorParameters) {
                            if (editorParameters != null) {
                                if (editorParameters.containsKey("readonly")) {
                                    isReadOnly = Boolean.valueOf(editorParameters.get("readonly"));
                                }
                                designerEditorParametersPublisher.publish(editorParameters);
                                designerWidget.setup(editorID,
                                                     editorParameters);
                            }
                        }
                    }).getEditorParameters(((PathPlaceRequest) place).getPath(),
                                           editorID,
                                           url,
                                           place);
                }
            }).getEditorID();
        }
    }

    @WorkbenchPartTitle
    public String getName() {
        return DesignerEditorConstants.INSTANCE.ProcessModel();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return container;
    }

    @Override
    public void onResize() {

        int height = getContainer().getParent().getOffsetHeight();
        int width = getContainer().getParent().getOffsetWidth();

        getContainer().setWidth(width + "px");
        getContainer().setHeight(height + "px");
    }

    public FlowPanel getContainer() {
        return this.container;
    }
}
