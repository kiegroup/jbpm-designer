package com.intalio.taskforms;

import java.util.ArrayList;
import java.util.List;

/** 
 * Holds info for one task form.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormInfo {
    private String id;
    private String processName;
    private String pkgName;
    private String taskName;
    private String output;
    private boolean processForm;
    private boolean userTaskForm;
    private List<String> taskOwners = new ArrayList<String>();
    private List<TaskFormInput> taskInputs = new ArrayList<TaskFormInput>();
    private List<TaskFormOutput> taskOutputs = new ArrayList<TaskFormOutput>();
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<TaskFormInput> getTaskInputs() {
        return taskInputs;
    }
    public void setTaskInputs(List<TaskFormInput> taskInputs) {
        this.taskInputs = taskInputs;
    }
    public List<TaskFormOutput> getTaskOutputs() {
        return taskOutputs;
    }
    public void setTaskOutputs(List<TaskFormOutput> taskOutputs) {
        this.taskOutputs = taskOutputs;
    }
    public String getProcessName() {
        return processName;
    }
    public void setProcessName(String processName) {
        this.processName = processName;
    }
    public String getPkgName() {
        return pkgName;
    }
    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
    public List<String> getTaskOwners() {
        return taskOwners;
    }
    public void setTaskOwners(List<String> taskOwners) {
        this.taskOwners = taskOwners;
    }
    public boolean isProcessForm() {
        return processForm;
    }
    public void setProcessForm(boolean processForm) {
        this.processForm = processForm;
    }
    public boolean isUserTaskForm() {
        return userTaskForm;
    }
    public void setUserTaskForm(boolean userTaskForm) {
        this.userTaskForm = userTaskForm;
    }
}
