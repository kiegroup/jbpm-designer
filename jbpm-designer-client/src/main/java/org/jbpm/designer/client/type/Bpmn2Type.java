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

package org.jbpm.designer.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.designer.client.resources.DesignerEditorResources;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.type.Bpmn2TypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class Bpmn2Type
        extends Bpmn2TypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image(DesignerEditorResources.INSTANCE.images().typeForm());

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = DesignerEditorConstants.INSTANCE.businessProcessResourceTypeDescription();
        if (desc == null || desc.isEmpty()) {
            return super.getDescription();
        }
        return desc;
    }
}