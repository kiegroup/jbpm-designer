/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.popup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.util.ListBoxValues;

@Dependent
public class ActivityDataIOEditor implements ActivityDataIOEditorView.Presenter {

    private boolean hasInputVars;
    private boolean isSingleInputVar;
    private boolean hasOutputVars;
    private boolean isSingleOutputVar;

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Assignments data.
     */
    public interface GetDataCallback {

        void getData(String assignmentData);
    }

    GetDataCallback callback = null;

    @Inject
    ActivityDataIOEditorView view;

    private List<String> dataTypes = new ArrayList<String>();

    private List<String> dataTypeDisplayNames = new ArrayList<String>();

    private AssignmentData assignmentData;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setCallback(GetDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void handleSaveClick() {

        if (callback != null) {
            AssignmentData data = new AssignmentData(view.getInputAssignmentData(),
                                                     view.getOutputAssignmentData(),
                                                     dataTypes,
                                                     dataTypeDisplayNames);
            data.setVariableCountsString(hasInputVars,
                                         isSingleInputVar,
                                         hasOutputVars,
                                         isSingleOutputVar);
            String sData = marshallToJson(data);
            callback.getData(sData);
        }
        view.hideView();
    }

    protected String marshallToJson(AssignmentData data) {
        return Marshalling.toJSON(data);
    }

    @Override
    public void handleCancelClick() {
        view.hideView();
    }

    public void setDataTypes(List<String> dataTypes,
                             List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        view.setPossibleInputAssignmentsDataTypes(dataTypeDisplayNames);
        view.setPossibleOutputAssignmentsDataTypes(dataTypeDisplayNames);
    }

    public void setAssignmentData(AssignmentData assignmentData) {
        this.assignmentData = assignmentData;
    }

    public void configureDialog(String taskName,
                                boolean hasInputVars,
                                boolean isSingleInputVar,
                                boolean hasOutputVars,
                                boolean isSingleOutputVar) {
        this.hasInputVars = hasInputVars;
        this.isSingleInputVar = isSingleInputVar;
        this.hasOutputVars = hasOutputVars;
        this.isSingleOutputVar = isSingleOutputVar;

        if (taskName != null && !taskName.isEmpty()) {
            view.setCustomViewTitle(taskName);
        } else {
            view.setDefaultViewTitle();
        }

        view.setInputAssignmentsVisibility(hasInputVars);
        view.setOutputAssignmentsVisibility(hasOutputVars);
        view.setIsInputAssignmentSingleVar(isSingleInputVar);
        view.setIsOutputAssignmentSingleVar(isSingleOutputVar);
    }

    public void setDisallowedPropertyNames(List<String> disallowedPropertyNames) {
        Set<String> propertyNames = new HashSet<String>();
        if (disallowedPropertyNames != null) {
            for (String name : disallowedPropertyNames) {
                propertyNames.add(name.toLowerCase());
            }
        }
        view.setInputAssignmentsDisallowedNames(propertyNames);
    }

    public void setProcessVariables(List<String> processVariables) {
        view.setInputAssignmentsProcessVariables(processVariables);
        view.setOutputAssignmentsProcessVariables(processVariables);
    }

    public void setCustomAssignmentsProperties(final Map<String, List<String>> customAssignmentsProperties) {
        view.setCustomAssignmentsProperties(customAssignmentsProperties);
    }

    public void setInputAssignmentRows(List<AssignmentRow> inputAssignmentRows) {
        view.setInputAssignmentRows(inputAssignmentRows);
    }

    public void setOutputAssignmentRows(List<AssignmentRow> outputAssignmentRows) {
        view.setOutputAssignmentRows(outputAssignmentRows);
    }

    public void show() {
        view.showView();
    }

    @Override
    public ListBoxValues.ValueTester dataTypesTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                if (assignmentData != null) {
                    return assignmentData.getDataTypeDisplayNameForUserString(userValue);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public ListBoxValues.ValueTester processVarTester() {
        return new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                return null;
            }
        };
    }
}
