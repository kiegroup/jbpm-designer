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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds info for one task form.
 */

public class TaskFormInfo {

    private String id;
    private String processName;
    private String processId;
    private String pkgName;
    private String taskName;
    private String taskId;
    private String groupId;
    private String skippable;
    private String actorId;
    private String comment;
    private String priority;
    private String metaOutput;
    private Map<String, String> modelerOutputs = new HashMap<>();
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

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMetaOutput() {
        return metaOutput;
    }

    public void setMetaOutput(String metaOutput) {
        this.metaOutput = metaOutput;
    }

    public Map<String, String> getModelerOutputs() {
        return modelerOutputs;
    }

    public void setModelerOutputs(Map<String, String> modelerOutputs) {
        this.modelerOutputs = modelerOutputs;
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSkippable() {
        return skippable;
    }

    public void setSkippable(String skippable) {
        this.skippable = skippable;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
