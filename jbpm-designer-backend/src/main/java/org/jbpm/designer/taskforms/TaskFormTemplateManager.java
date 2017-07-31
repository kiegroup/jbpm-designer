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

package org.jbpm.designer.taskforms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.UserTask;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.kie.workbench.common.forms.bpmn.BPMNFormBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STRawGroupDir;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

/**
 * Manager for task form templates.
 */
public class TaskFormTemplateManager {

    private static final Logger _logger = LoggerFactory.getLogger(TaskFormTemplateManager.class);
    private static final String[] validStructureRefs = new String[]{"Float", "Integer", "String", "Object", "Boolean", "Undefined"};
    public final static String TASKFORMS_PATH = "stencilsets";

    private IDiagramProfile profile;
    private String packageName;
    private String assetName;
    private Asset processAsset;
    private String templatesPath;
    private Definitions def;
    private String taskId;
    private List<TaskFormInfo> taskFormInformationList = new ArrayList<TaskFormInfo>();
    private Path myPath;
    private BPMNFormBuilderManager formBuilderManager;
    private String formType;

    public TaskFormTemplateManager(Path myPath,
                                   BPMNFormBuilderManager formBuilderManager,
                                   IDiagramProfile profile,
                                   Asset processAsset,
                                   String templatesPath,
                                   Definitions def,
                                   String taskId,
                                   String formType) {
        this.myPath = myPath;
        this.formBuilderManager = formBuilderManager;
        this.profile = profile;
        this.packageName = processAsset.getAssetLocation();
        this.assetName = processAsset.getName();
        this.processAsset = processAsset;
        this.templatesPath = templatesPath;
        this.def = def;
        this.taskId = taskId;
        this.formType = formType;
    }

    public void processTemplates() {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement re : rootElements) {
            if (re instanceof Process) {
                Process process = (Process) re;
                if (process != null && process.getId() != null && process.getId().length() > 0) {

                    // get the list of process properties
                    List<Property> processProperties = process.getProperties();

                    if (taskId == null) {
                        TaskFormInfo tfi = new TaskFormInfo();
                        tfi.setId(process.getId() + "-taskform");
                        if (process.getName() != null && process.getName().length() > 0) {
                            tfi.setProcessName(process.getName());
                        } else {
                            tfi.setProcessName(process.getId());
                        }
                        tfi.setProcessId(process.getId());
                        //String packageName1 = "";
                        //                    FeatureMap attrs = process.getAnyAttribute();
                        //                    for (Object attr : attrs) {
                        //                        EStructuralFeatureImpl.SimpleFeatureMapEntry a = (EStructuralFeatureImpl.SimpleFeatureMapEntry) attr;
                        //                        if("packageName".equals(a.getEStructuralFeature().getName())) {
                        //                            packageName1 = (String)a.getValue();
                        //                        }
                        //                    }
                        tfi.setPkgName(packageName);
                        for (Property prop : processProperties) {
                            if (isValidStructureRef(prop.getItemSubjectRef().getStructureRef())) {
                                TaskFormInput input = new TaskFormInput();
                                input.setName(prop.getId());
                                input.setRefType(prop.getItemSubjectRef().getStructureRef());
                                tfi.getTaskInputs().add(input);
                            }
                        }
                        tfi.setProcessForm(true);
                        tfi.setUserTaskForm(false);
                        taskFormInformationList.add(tfi);
                    }

                    for (FlowElement fe : process.getFlowElements()) {
                        if (fe instanceof UserTask) {
                            getTaskInfoForUserTask((UserTask) fe,
                                                   process,
                                                   processProperties,
                                                   taskId);
                        } else if (fe instanceof FlowElementsContainer) {
                            getTaskInfoForContainers((FlowElementsContainer) fe,
                                                     process,
                                                     processProperties,
                                                     taskId);
                        }
                    }
                    generateTemplates();
                } else {
                    _logger.info("Invalid process. Not templates are generated");
                }
            }
        }
    }

    private void getTaskInfoForContainers(FlowElementsContainer container,
                                          Process process,
                                          List<Property> processProperties,
                                          String taskId) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof UserTask) {
                getTaskInfoForUserTask((UserTask) fe,
                                       process,
                                       processProperties,
                                       taskId);
            } else if (fe instanceof FlowElementsContainer) {
                getTaskInfoForContainers((FlowElementsContainer) fe,
                                         process,
                                         processProperties,
                                         taskId);
            }
        }
    }

    public void generateUserTaskForm(UserTask utask,
                                     Process process,
                                     List<Property> processProperties) {
        TaskFormInfo usertfi = new TaskFormInfo();
        if (process.getName() != null && process.getName().length() > 0) {
            usertfi.setProcessName(process.getName());
        } else {
            usertfi.setProcessName(process.getId());
        }
        usertfi.setProcessId(process.getId());
        usertfi.setTaskId(utask.getId());
        usertfi.setPkgName(packageName);
        // make sure we have a valid task name
        boolean validTaskName = false;
        List<DataInput> dataInputs = Collections.emptyList();
        List<DataOutput> dataOutputs = Collections.emptyList();
        List<DataInputAssociation> dataInputAssociations = Collections.emptyList();
        List<DataOutputAssociation> dataOutputAssociations = Collections.emptyList();
        if (utask.getIoSpecification() != null) {
            dataInputs = utask.getIoSpecification().getDataInputs();
            dataOutputs = utask.getIoSpecification().getDataOutputs();
            dataInputAssociations = utask.getDataInputAssociations();
            dataOutputAssociations = utask.getDataOutputAssociations();
            for (DataInput din : dataInputs) {
                if (din.getName().equals("TaskName")) {
                    // make sure we have a data input association to the task name value
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String taskName = ((FormalExpression) assignment.getFrom()).getBody();
                                if (taskName != null && taskName.length() > 0) {
                                    String unwrappedTaskName = taskName.replace("<![CDATA[",
                                                                                "").replace("]]>",
                                                                                            "");
                                    usertfi.setId(unwrappedTaskName + "-taskform");
                                    usertfi.setTaskName(unwrappedTaskName);
                                    validTaskName = true;
                                }
                            }
                        }
                    }
                }
                if (din.getName().equals("ActorId")) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String actorid = ((FormalExpression) assignment.getFrom()).getBody();
                                if (actorid != null && actorid.length() > 0) {
                                    usertfi.setActorId(replaceInterpolations(actorid));
                                }
                            }
                        }
                    }
                }
                if (din.getName().equals("GroupId")) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String groupid = ((FormalExpression) assignment.getFrom()).getBody();
                                if (groupid != null && groupid.length() > 0) {
                                    usertfi.setGroupId(replaceInterpolations(groupid));
                                }
                            }
                        }
                    }
                }
                if (din.getName().equals("Skippable")) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String skippable = ((FormalExpression) assignment.getFrom()).getBody();
                                if (skippable != null && skippable.length() > 0) {
                                    usertfi.setSkippable(replaceInterpolations(skippable));
                                }
                            }
                        }
                    }
                }
                if (din.getName().equals("Priority")) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String priority = ((FormalExpression) assignment.getFrom()).getBody();
                                if (priority != null && priority.length() > 0) {
                                    usertfi.setPriority(replaceInterpolations(priority));
                                }
                            }
                        }
                    }
                }
                if (din.getName().equals("Comment")) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        for (Assignment assignment : assignments) {
                            if (((FormalExpression) assignment.getTo()).getBody().equals(din.getId())) {
                                String comment = ((FormalExpression) assignment.getFrom()).getBody();
                                if (comment != null && comment.length() > 0) {
                                    usertfi.setComment(replaceInterpolations(comment));
                                }
                            }
                        }
                    }
                }
            }
        }
        if (validTaskName) {
            // get list of potential owners
            List<ResourceRole> utaskroles = utask.getResources();
            for (ResourceRole role : utaskroles) {
                if (role instanceof PotentialOwner) {
                    FormalExpression ownerexp = (FormalExpression) ((PotentialOwner) role).getResourceAssignmentExpression().getExpression();
                    if (ownerexp.getBody() != null && ownerexp.getBody().length() > 0) {
                        usertfi.getTaskOwners().add(replaceInterpolations(ownerexp.getBody()));
                    }
                }
            }
            // get all inputs and outputs of the user task
            for (DataInput dinput : dataInputs) {
                // we already handled TaskName, ActorId , GroupId, Skippable, Priority, Comment
                if (!(dinput.getName().equals("TaskName") || dinput.getName().equals("ActorId") || dinput.getName().equals("GroupId")
                        || dinput.getName().equals("Skippable") || dinput.getName().equals("Priority") || dinput.getName().equals("Comment"))) {
                    TaskFormInput input = new TaskFormInput();
                    input.setName(dinput.getName());
                    // we need to see if the value of the input references a process var
                    // or we have an assignment defined
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {
                        List<Assignment> assignments = inputAssociation.getAssignment();
                        if (assignments != null && assignments.size() > 0) {
                            // get the assignment value
                            for (Assignment assignment : assignments) {
                                if (((FormalExpression) assignment.getTo()).getBody().equals(dinput.getId())) {
                                    input.setValue(((FormalExpression) assignment.getFrom()).getBody());
                                }
                            }
                        } else {
                            // mapping to process var
                            if (inputAssociation.getTargetRef().getId().equals(dinput.getId())) {
                                for (Property prop : processProperties) {
                                    if (prop.getId().equals(inputAssociation.getSourceRef().get(0).getId())) {
                                        input.setRefType(prop.getItemSubjectRef().getStructureRef());
                                    }
                                }
                                if (input.getRefType() != null && input.getRefType().equals("Date")) {
                                    //input.setValue("${"+ inputAssociation.getSourceRef().get(0).getId() + "?date} ${"+ inputAssociation.getSourceRef().get(0).getId() + "?time}");
                                    input.setValue("${" + ((DataInput) inputAssociation.getTargetRef()).getName() + "?date} ${" + ((DataInput) inputAssociation.getTargetRef()).getName() + "?time}");
                                } else {
                                    //input.setValue("${"+ inputAssociation.getSourceRef().get(0).getId() + "}");
                                    input.setValue("${" + ((DataInput) inputAssociation.getTargetRef()).getName() + "}");
                                }
                            }
                        }
                    }

                    usertfi.getTaskInputs().add(input);
                }
            }
            for (DataOutput dout : dataOutputs) {
                TaskFormOutput out = new TaskFormOutput();
                out.setName(dout.getName());
                out.setValue("${" + dout.getName() + "}");
                for (DataOutputAssociation outputAssociation : dataOutputAssociations) {
                    List<ItemAwareElement> sources = outputAssociation.getSourceRef();
                    for (ItemAwareElement iae : sources) {
                        if (iae.getId().equals(dout.getId())) {
                            for (Property prop : processProperties) {
                                if (prop.getId().equals(outputAssociation.getTargetRef().getId())) {
                                    out.setRefType(prop.getItemSubjectRef().getStructureRef());
                                }
                            }
                        }
                    }
                }
                usertfi.getTaskOutputs().add(out);
            }
            usertfi.setUserTaskForm(true);
            usertfi.setProcessForm(false);
            // check if this usertfi already exists..if so, merge their inputs/outputs to create a single form
            boolean merged = false;
            for (TaskFormInfo existingForm : taskFormInformationList) {
                if (existingForm.getId().equals(usertfi.getId())) {
                    mergeUserTaskForms(usertfi,
                                       existingForm);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                taskFormInformationList.add(usertfi);
            }
        } else {
            _logger.info("Could not generate task form for usertask id: " + utask.getId() + ". No task name specified.");
        }
    }

    private void getTaskInfoForUserTask(UserTask utask,
                                        Process process,
                                        List<Property> processProperties,
                                        String taskId) {
        if (taskId != null && taskId.length() > 0) {
            if (utask.getId().equals(taskId)) {
                generateUserTaskForm(utask,
                                     process,
                                     processProperties);
            } else {
                _logger.info("Generating for specific task id: " + taskId + ". Omitting task id: " + utask.getId());
            }
        } else {
            generateUserTaskForm(utask,
                                 process,
                                 processProperties);
        }
    }

    private void mergeUserTaskForms(TaskFormInfo sourceForm,
                                    TaskFormInfo targetForm) {
        List<TaskFormInput> toMergeTaskInputs = new ArrayList<TaskFormInput>();
        List<TaskFormOutput> toMergeTaskOutputs = new ArrayList<TaskFormOutput>();

        for (String sourceOwner : sourceForm.getTaskOwners()) {
            if (!targetForm.getTaskOwners().contains(sourceOwner)) {
                targetForm.getTaskOwners().add(sourceOwner);
            }
        }

        for (TaskFormInput sourceInput : sourceForm.getTaskInputs()) {
            boolean foundInput = false;
            for (TaskFormInput targetInput : targetForm.getTaskInputs()) {
                if (targetInput.getName().equals(sourceInput.getName())) {
                    foundInput = true;
                }
            }
            if (!foundInput) {
                toMergeTaskInputs.add(sourceInput);
            }
        }

        for (TaskFormOutput sourceOutput : sourceForm.getTaskOutputs()) {
            boolean foundOutput = false;
            for (TaskFormOutput targetOutput : targetForm.getTaskOutputs()) {
                if (targetOutput.getName().equals(sourceOutput.getName())) {
                    foundOutput = true;
                }
            }
            if (!foundOutput) {
                toMergeTaskOutputs.add(sourceOutput);
            }
        }

        for (TaskFormInput input : toMergeTaskInputs) {
            targetForm.getTaskInputs().add(input);
        }
        for (TaskFormOutput output : toMergeTaskOutputs) {
            targetForm.getTaskOutputs().add(output);
        }
    }

    private boolean isValidStructureRef(String structureRef) {
        // supported types are Float Integer String Object Boolean and Underfined
        if (structureRef != null && structureRef.length() > 0) {
            return Arrays.asList(validStructureRefs).contains(structureRef);
        } else {
            // null or empty is default Object type
            return true;
        }
    }

    public void generateTemplates() {
        for (TaskFormInfo tfi : taskFormInformationList) {
            if (tfi.isProcessForm()) {
                generateProcessTemplate(tfi);
            } else {
                generateUserTaskTemplate(tfi);
            }
            generatePlatformForms(tfi);
        }
    }

    private void generateProcessTemplate(TaskFormInfo tfi) {
        STRawGroupDir templates = new STRawGroupDir(templatesPath,
                                                    '$',
                                                    '$');
        ST processFormTemplate = templates.getInstanceOf("processtaskform");
        processFormTemplate.add("tfi",
                                tfi);
        processFormTemplate.add("bopen",
                                "{");
        processFormTemplate.add("bclose",
                                "}");
        processFormTemplate.add("dollar",
                                "$");
        tfi.setMetaOutput(processFormTemplate.render());
    }

    protected void generatePlatformForms(TaskFormInfo tfi) {

        BPMNFormBuilderService<Definitions> formBuilder = formBuilderManager.getBuilderByFormType(formType);
        if (formBuilder != null && formBuilder.getFormExtension() != null) {
            String formName = tfi.getId() + "." + formBuilder.getFormExtension();
            String formURI = myPath.toURI();
            formURI = formURI.substring(0,
                                        formURI.lastIndexOf("/"));
            formURI = formURI + "/" + formName;

            Path formPath = PathFactory.newPathBasedOn(formName,
                                                       formURI,
                                                       myPath);

            try {
                tfi.getModelerOutputs().put(formBuilder.getFormExtension(),
                                            formBuilder.buildFormContent(formPath,
                                                                         def,
                                                                         tfi.getTaskId()));
            } catch (Exception e) {
                _logger.error(e.getMessage());
                e.printStackTrace();
            }
        } else {
            _logger.error("Unable to find form builder service for type: " + formType);
        }
    }

    private void generateUserTaskTemplate(TaskFormInfo tfi) {
        STRawGroupDir templates = new STRawGroupDir(templatesPath,
                                                    '$',
                                                    '$');
        ST usertaskFormTemplate = templates.getInstanceOf("usertaskform");
        usertaskFormTemplate.add("tfi",
                                 tfi);
        usertaskFormTemplate.add("bopen",
                                 "{");
        usertaskFormTemplate.add("bclose",
                                 "}");
        usertaskFormTemplate.add("dollar",
                                 "$");
        tfi.setMetaOutput(usertaskFormTemplate.render());
    }

    public String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname));
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    private String replaceInterpolations(String base) {
        return base.replaceAll("\\#\\{",
                               "\\$\\{");
    }

    public List<TaskFormInfo> getTaskFormInformationList() {
        return taskFormInformationList;
    }

    public String getTemplatesPath() {
        return templatesPath;
    }

    public void setTemplatesPath(String templatesPath) {
        this.templatesPath = templatesPath;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setTaskFormInformationList(
            List<TaskFormInfo> taskFormInformationList) {
        this.taskFormInformationList = taskFormInformationList;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
