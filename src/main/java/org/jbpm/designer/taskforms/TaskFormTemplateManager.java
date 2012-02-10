package org.jbpm.designer.taskforms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.UserTask;
import org.jbpm.designer.web.profile.IDiagramProfile;


/** 
 * Manager for task form templates.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormTemplateManager {
    private static final Logger _logger = Logger.getLogger(TaskFormTemplateManager.class);
    private static final String[] validStructureRefs = new String[] {"Float", "Integer", "String", "Object", "Boolean", "Undefined"};
    public final static String TASKFORMS_PATH = "stencilsets";
    
    private IDiagramProfile profile;
    private String packageName;
    private String assetName;
    private String templatesPath;
    private Definitions def;
    private List<TaskFormInfo> taskFormInformationList = new ArrayList<TaskFormInfo>();
    
    public TaskFormTemplateManager(IDiagramProfile profile, String packageName, String assetName, String templatesPath, Definitions def) {
        this.profile = profile;
        this.packageName = packageName;
        this.assetName = assetName;
        this.templatesPath = templatesPath;
        this.def = def;
    }
    
    public void processTemplates() {
        List<RootElement> rootElements = def.getRootElements();
        for(RootElement re : rootElements) {
            if(re instanceof Process) {
                Process process = (Process) re;
                if(process != null && process.getId() != null && process.getId().length() > 0) {
                    TaskFormInfo tfi = new TaskFormInfo();
                    tfi.setId(process.getId() + "-taskform");
                    if(process.getName() != null && process.getName().length() > 0 ) {
                        tfi.setProcessName(process.getName());
                    } else {
                        tfi.setProcessName(process.getId());
                    }
                    tfi.setPkgName(packageName);
                    // get the list of process properties
                    List<Property> processProperties = process.getProperties();
                    for(Property prop : processProperties) {
                        if(isValidStructureRef(prop.getItemSubjectRef().getStructureRef())) {
                            TaskFormInput input = new TaskFormInput();
                            input.setName(prop.getId());
                            input.setRefType(prop.getItemSubjectRef().getStructureRef());
                            tfi.getTaskInputs().add(input);
                        }
                    }
                    tfi.setProcessForm(true);
                    tfi.setUserTaskForm(false);
                    taskFormInformationList.add(tfi);
                    
                    for(FlowElement fe : process.getFlowElements()) {
                        if(fe instanceof UserTask) {
                            UserTask utask = (UserTask) fe;
                            TaskFormInfo usertfi = new TaskFormInfo();
                            if(process.getName() != null && process.getName().length() > 0 ) {
                                usertfi.setProcessName(process.getName());
                            } else {
                                usertfi.setProcessName(process.getId());
                            }
                            usertfi.setPkgName(packageName);
                            // make sure we have a valid task name
                            boolean validTaskName = false;
                            List<DataInput> dataInputs = utask.getIoSpecification().getDataInputs();
                            List<DataOutput> dataOutputs = utask.getIoSpecification().getDataOutputs();
                            List<DataInputAssociation> dataInputAssociations = utask.getDataInputAssociations();
                            List<DataOutputAssociation> dataOutputAssociations = utask.getDataOutputAssociations();
                            for(DataInput din : dataInputs) {
                                if(din.getName().equals("TaskName")) {
                                    // make sure we have a data input association to the task name value
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String taskName = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(taskName != null && taskName.length() > 0) {
                                                    usertfi.setId(taskName + "-taskform");
                                                    usertfi.setTaskName(taskName);
                                                    validTaskName = true;
                                                }
                                            }
                                        }
                                    }
                                }
                                if(din.getName().equals("ActorId")) {
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String actorid = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(actorid != null && actorid.length() > 0) {
                                                    usertfi.setActorId(actorid);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(din.getName().equals("GroupId")) {
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String groupid = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(groupid != null && groupid.length() > 0) {
                                                    usertfi.setGroupId(groupid);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(din.getName().equals("Skippable")) {
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String skippable = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(skippable != null && skippable.length() > 0) {
                                                    usertfi.setSkippable(skippable);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(din.getName().equals("Priority")) {
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String priority = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(priority != null && priority.length() > 0) {
                                                    usertfi.setPriority(priority);
                                                }
                                            }
                                        }
                                    }
                                }
                                if(din.getName().equals("Comment")) {
                                    for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                        List<Assignment> assignments = inputAssociation.getAssignment();
                                        for(Assignment assignment : assignments) {
                                            if( ((FormalExpression)assignment.getTo()).getBody().equals(din.getId())) {
                                                String comment = ((FormalExpression)assignment.getFrom()).getBody();
                                                if(comment != null && comment.length() > 0) {
                                                    usertfi.setComment(comment);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(validTaskName) {
                                // get list of potential owners
                                List<ResourceRole> utaskroles = utask.getResources();
                                for(ResourceRole role : utaskroles) {
                                    if(role instanceof PotentialOwner) {
                                        FormalExpression ownerexp = (FormalExpression) ( (PotentialOwner)role).getResourceAssignmentExpression().getExpression();
                                        if(ownerexp.getBody() != null && ownerexp.getBody().length() > 0) {
                                            usertfi.getTaskOwners().add(ownerexp.getBody());
                                        }
                                    }
                                }
                                // get all inputs and outputs of the user task
                                for(DataInput dinput : dataInputs) {
                                    // we already handled TaskName, ActorId , GroupId, Skippable, Priority, Comment 
                                    if(!(dinput.getName().equals("TaskName") || dinput.getName().equals("ActorId") || dinput.getName().equals("GroupId")
                                            || dinput.getName().equals("Skippable") || dinput.getName().equals("Priority") || dinput.getName().equals("Comment"))) {
                                        TaskFormInput input = new TaskFormInput();
                                        input.setName(dinput.getName());
                                        // we need to see if the value of the input references a process var
                                        // or we have an assignment defined
                                        for(DataInputAssociation inputAssociation : dataInputAssociations) {
                                            List<Assignment> assignments = inputAssociation.getAssignment();
                                            if(assignments != null && assignments.size() > 0) {
                                                // get the assignment value
                                                for(Assignment assignment : assignments) {
                                                    if( ((FormalExpression)assignment.getTo()).getBody().equals(dinput.getId())) {
                                                        input.setValue( ((FormalExpression)assignment.getFrom()).getBody() );
                                                    }
                                                }
                                            } else {
                                                // mapping to process var
                                                if(inputAssociation.getTargetRef().getId().equals(dinput.getId())) {
                                                    for(Property prop : processProperties) {
                                                        if(prop.getId().equals(inputAssociation.getSourceRef().get(0).getId())) {
                                                            input.setRefType( prop.getItemSubjectRef().getStructureRef() ); 
                                                        }
                                                    }
                                                    if(input.getRefType() != null && input.getRefType().equals("Date")) {
                                                        //input.setValue("${"+ inputAssociation.getSourceRef().get(0).getId() + "?date} ${"+ inputAssociation.getSourceRef().get(0).getId() + "?time}");
                                                        input.setValue("${"+ ((DataInput)inputAssociation.getTargetRef()).getName() + "?date} ${"+ ((DataInput)inputAssociation.getTargetRef()).getName() + "?time}");
                                                    } else {
                                                        //input.setValue("${"+ inputAssociation.getSourceRef().get(0).getId() + "}");
                                                        input.setValue("${"+ ((DataInput)inputAssociation.getTargetRef()).getName() + "}");
                                                    }
                                                }
                                            }
                                        }
                                        
                                        usertfi.getTaskInputs().add(input);
                                    }
                                }
                                for(DataOutput dout : dataOutputs) {
                                    TaskFormOutput out = new TaskFormOutput();
                                    out.setName(dout.getName());
                                    for(DataOutputAssociation outputAssociation : dataOutputAssociations) {
                                        List<ItemAwareElement> sources = outputAssociation.getSourceRef();
                                        for(ItemAwareElement iae : sources) {
                                            if(iae.getId().equals(dout.getId())) {
                                                for(Property prop : processProperties) {
                                                    if(prop.getId().equals(outputAssociation.getTargetRef().getId())) {
                                                        out.setRefType( prop.getItemSubjectRef().getStructureRef() ); 
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
                                for(TaskFormInfo existingForm : taskFormInformationList) {
                                    if(existingForm.getId().equals(usertfi.getId())) {
                                        mergeUserTaskForms(usertfi, existingForm);
                                        merged = true;
                                        break;
                                    }
                                } 
                                if(!merged) {
                                    taskFormInformationList.add(usertfi);
                                }
                            } else {
                                _logger.info("Could not generate task form for usertask id: " + utask.getId() + ". No task name specified.");
                            }
                        }
                    }
                    generateTemplates();
                } else {
                    _logger.info("Invalid process. Not templates are generated");
                }
            }
        }
        
    }
    
    private void mergeUserTaskForms(TaskFormInfo sourceForm, TaskFormInfo targetForm) {
        List<TaskFormInput> toMergeTaskInputs = new ArrayList<TaskFormInput>();
        List<TaskFormOutput> toMergeTaskOutputs = new ArrayList<TaskFormOutput>();
        
        for(String sourceOwner : sourceForm.getTaskOwners()) {
            if(!targetForm.getTaskOwners().contains(sourceOwner)) {
                targetForm.getTaskOwners().add(sourceOwner);
            }
        }
        
        for(TaskFormInput sourceInput : sourceForm.getTaskInputs()) {
            boolean foundInput = false;
            for(TaskFormInput targetInput : targetForm.getTaskInputs()) {
                if(targetInput.getName().equals(sourceInput.getName())) {
                    foundInput = true;
                }
            }
            if(!foundInput) {
                toMergeTaskInputs.add(sourceInput);
            }
        }
        
        for(TaskFormOutput sourceOutput : sourceForm.getTaskOutputs()) {
            boolean foundOutput = false;
            for(TaskFormOutput targetOutput : targetForm.getTaskOutputs()) {
                if(targetOutput.getName().equals(sourceOutput.getName())) {
                    foundOutput = true;
                }
            }
            if(!foundOutput) {
                toMergeTaskOutputs.add(sourceOutput);
            }
        }
        
        
        for(TaskFormInput input : toMergeTaskInputs) {
            targetForm.getTaskInputs().add(input);
        }
        for(TaskFormOutput output : toMergeTaskOutputs) {
            targetForm.getTaskOutputs().add(output);
        }
    }
    
    private boolean isValidStructureRef(String structureRef) {
        // supported types are Float Integer String Object Boolean and Underfined
        if(structureRef != null && structureRef.length() > 0) {
            return Arrays.asList(validStructureRefs).contains(structureRef);
        } else {
            // null or empty is default Object type
            return true;
        }
    }
    
    public void generateTemplates() {
        for(TaskFormInfo tfi : taskFormInformationList) {
            if(tfi.isProcessForm()) {
                generateProcessTemplate(tfi);
            } else {
                generateUserTaskTemplate(tfi);
            }
        }
    }
    
    private void generateProcessTemplate(TaskFormInfo tfi) {
        StringTemplateGroup templates = new StringTemplateGroup("processtaskgroup", templatesPath);
        StringTemplate processFormTemplate = templates.getInstanceOf("processtaskform");
        processFormTemplate.setAttribute("tfi", tfi);
        tfi.setOutput(processFormTemplate.toString());
    }
    
    private void generateUserTaskTemplate(TaskFormInfo tfi) {
        StringTemplateGroup templates = new StringTemplateGroup("usertaskgroup", templatesPath);
        StringTemplate usertaskFormTemplate = templates.getInstanceOf("usertaskform");
        usertaskFormTemplate.setAttribute("tfi", tfi);
        tfi.setOutput(usertaskFormTemplate.toString());
    }
    
    public String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname));
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
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
