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

package org.jbpm.designer.taskforms.builder;

import java.util.UUID;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.forms.bpmn.BPMNFormBuilderService;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring.Authoring;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Dependent
public class BPMNKieWorkbenchFormBuilderService implements BPMNFormBuilderService<Definitions> {

    private IOService ioService;

    protected FormModelHandlerManager formModelHandlerManager;

    protected BPMNFormModelGenerator generator;

    protected FormDefinitionSerializer serializer;

    protected FormLayoutTemplateGenerator layoutTemplateGenerator;

    protected BPMNFormGeneratorService<Path> bpmnFormGeneratorService;

    @Inject
    public BPMNKieWorkbenchFormBuilderService(
            @Named("ioStrategy") IOService ioService,
            FormModelHandlerManager formModelHandlerManager,
            BPMNFormModelGenerator generator,
            FormDefinitionSerializer serializer,
            FormLayoutTemplateGenerator layoutTemplateGenerator,
            @Authoring BPMNFormGeneratorService<Path> bpmnFormGeneratorService) {
        this.ioService = ioService;
        this.formModelHandlerManager = formModelHandlerManager;
        this.generator = generator;
        this.serializer = serializer;
        this.layoutTemplateGenerator = layoutTemplateGenerator;
        this.bpmnFormGeneratorService = bpmnFormGeneratorService;
    }

    @Override
    public String buildFormContent(Path formPath,
                                   Definitions definition,
                                   String taskId) throws Exception {

        JBPMFormModel model;

        if (StringUtils.isEmpty(taskId)) {
            model = generator.generateProcessFormModel(definition, formPath);
        } else {
            model = generator.generateTaskFormModel(definition,
                                                    taskId, formPath);
        }

        if (model == null) {
            throw new IllegalArgumentException("Unable to generate form '" + formPath.getFileName() + "'");
        }

        FormDefinition form = bpmnFormGeneratorService.generateForms(model,
                                                                     formPath).getRootForm();

        return serializer.serialize(form);
    }

    @Override
    public String buildEmptyFormContent(String fileName) throws Exception {

        FormDefinition form = getNewFormInstance();

        form.setName(fileName);

        return serializer.serialize(form);
    }

    protected FormDefinition getNewFormInstance() {
        FormDefinition form = new FormDefinition();

        form.setId(UUID.randomUUID().toString());

        layoutTemplateGenerator.generateLayoutTemplate(form);

        return form;
    }

    @Override
    public String getFormExtension() {
        return "frm";
    }
}
