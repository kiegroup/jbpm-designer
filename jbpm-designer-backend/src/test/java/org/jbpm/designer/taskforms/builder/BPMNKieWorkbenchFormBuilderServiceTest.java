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

import org.apache.commons.io.IOUtils;
import org.eclipse.bpmn2.Definitions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jbpm.designer.bpmn2.impl.Bpmn2UnmarshallingTest;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.runtime.RuntimeDMOModelReader;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.ModuleFormFinderService;
import org.kie.workbench.common.forms.editor.service.shared.model.impl.FormModelSynchronizationUtilImpl;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring.BPMNVFSFormDefinitionGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BPMNFormModelGeneratorImpl;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BusinessProcessFormModelHandler;
import org.kie.workbench.common.forms.jbpm.server.service.impl.TaskFormModelHandler;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNKieWorkbenchFormBuilderServiceTest {

    @Mock
    private IOService ioService;

    @Mock
    private Path formPath;

    @Mock
    private FormModelHandlerManager formModelHandlerManager;

    @Mock
    private ModelReaderService<Path> modelReaderService;

    @Mock
    private ModuleFormFinderService moduleFormFinderService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private Bpmn2Loader loader = new Bpmn2Loader(Bpmn2UnmarshallingTest.class);

    private Definitions definitions;

    private BPMNKieWorkbenchFormBuilderService builderService;

    @Mock
    private KieModule module;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    private BPMFinderService bpmFinderService;

    private FieldManager fieldManager = new TestFieldManager();

    private StaticFormLayoutTemplateGenerator formLayoutTemplateGenerator = new StaticFormLayoutTemplateGenerator();

    private FormDefinitionSerializer formSerializer;

    @Before
    public void initTest() throws Exception {
        when(moduleService.resolveModule(any())).thenReturn(module);

        when(moduleClassLoaderHelper.getModuleClassLoader(any())).thenReturn(getClass().getClassLoader());

        when(formPath.toURI()).thenReturn("file://fakepath.frm");
        when(ioService.exists(any())).thenReturn(false);

        definitions = loader.loadProcessFromJson("userTask.json");

        formSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                          new FormModelSerializer(),
                                                          new TestMetaDataEntryManager());

        when(formModelHandlerManager.getFormModelHandler(any())).then(invocationOnMock -> {
            if (BusinessProcessFormModel.class.equals(invocationOnMock.getArguments()[0])) {
                return new BusinessProcessFormModelHandler(moduleService,
                                                           moduleClassLoaderHelper,
                                                           new TestFieldManager(),
                                                           bpmFinderService);
            } else {
                return new TaskFormModelHandler(moduleService,
                                                moduleClassLoaderHelper,
                                                new TestFieldManager(),
                                                bpmFinderService);
            }
        });

        when(modelReaderService.getModelReader(any())).thenReturn(new RuntimeDMOModelReader(getClass().getClassLoader(), new RawMVELEvaluator()));

        builderService = new BPMNKieWorkbenchFormBuilderService(ioService,
                                                                formModelHandlerManager,
                                                                new BPMNFormModelGeneratorImpl(moduleService,
                                                                                               moduleClassLoaderHelper),
                                                                formSerializer,
                                                                new StaticFormLayoutTemplateGenerator(),
                                                                new BPMNVFSFormDefinitionGeneratorService(fieldManager,
                                                                                                          modelReaderService,
                                                                                                          formModelHandlerManager,
                                                                                                          moduleFormFinderService,
                                                                                                          formSerializer,
                                                                                                          ioService,
                                                                                                          commentedOptionFactory,
                                                                                                          new FormModelSynchronizationUtilImpl(fieldManager,
                                                                                                                                               formLayoutTemplateGenerator)));
    }

    @Test
    public void testBasicFuncionallities() throws Exception {
        assertEquals("Form extension must be 'frm'",
                     "frm",
                     builderService.getFormExtension());

        String formContent = builderService.buildEmptyFormContent("fakeName");

        assertNotNull("Form content must not be null",
                      formContent);

        FormDefinition form = formSerializer.deserialize(formContent);

        assertNotNull("Form must not be null",
                      form);
        assertNull("FormModel must be null",
                   form.getModel());
        assertEquals("Form should not have fields",
                     0,
                     form.getFields().size());
        assertNotNull("Form layout must not be null",
                      form.getLayoutTemplate());
    }

    @Test
    public void testCreateNonExistingProcessForm() throws Exception {
        testCreateProcessForm(false);
    }

    @Test
    public void testCreateExistingProcessForm() throws Exception {
        testCreateProcessForm(true);
    }

    protected void testCreateProcessForm(boolean exists) throws Exception {
        when(ioService.exists(any())).thenReturn(exists);
        when(ioService.readAllString(any())).thenReturn(IOUtils.toString(BPMNKieWorkbenchFormBuilderServiceTest.class.getResourceAsStream("processForm.json")));

        String formConent = builderService.buildFormContent(formPath,
                                                            definitions,
                                                            null);

        assertNotNull("Form Content cannot be null",
                      formConent);

        FormDefinition form = formSerializer.deserialize(formConent);

        assertNotNull("Form cannot be null",
                      form);
        assertNotNull("FormModel cannot be null",
                      form.getModel());
        assertTrue("FormModel must be a BusinessProcessFormModel",
                   form.getModel() instanceof BusinessProcessFormModel);
        assertNotNull("Layout must be null",
                      form.getLayoutTemplate());
    }

    @Test
    public void testCreateNonExistingTaskForm() throws Exception {
        testCreateTaskForm(false);
    }

    @Test
    public void testCreateExistingTaskForm() throws Exception {
        testCreateTaskForm(true);
    }

    protected void testCreateTaskForm(boolean exists) throws Exception {
        when(ioService.exists(any())).thenReturn(exists);
        when(ioService.readAllString(any())).thenReturn(IOUtils.toString(BPMNKieWorkbenchFormBuilderServiceTest.class.getResourceAsStream("taskForm.json")));

        String formConent = builderService.buildFormContent(formPath,
                                                            definitions,
                                                            "_E57BCFF4-0787-40FA-83AC-627F06BF8F36");

        assertNotNull("Form Content cannot be null",
                      formConent);

        FormDefinition form = formSerializer.deserialize(formConent);

        assertNotNull("Form cannot be null",
                      form);
        assertNotNull("FormModel cannot be null",
                      form.getModel());
        assertTrue("FormModel must be a TaskFormModel",
                   form.getModel() instanceof TaskFormModel);
        assertNotNull("Layout must be null",
                      form.getLayoutTemplate());
    }
}
