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

import com.google.gwt.user.client.ui.Widget;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.jbpm.designer.client.util.ListBoxValues;

@Dependent
public class ActivityDataIOEditorWidget implements ActivityDataIOEditorWidgetView.Presenter {

    @Inject
    private ActivityDataIOEditorWidgetView view;

    ListBoxValues dataTypeListBoxValues;
    ListBoxValues processVarListBoxValues;
    Map<String, List<String>> customAssignmentsProperties;

    private VariableType variableType = VariableType.INPUT;

    boolean isSingleVar = false;

    private boolean allowDuplicateNames = true;
    private String duplicateNameErrorMessage = "";

    private Set<String> disallowedNames = new HashSet<String>();
    private String disallowedNameErrorMessage = "";

    // List of rows that won't be shown in the UI
    List<AssignmentRow> hiddenPropertyRows = new ArrayList<AssignmentRow>();

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void handleAddClick() {
        if (isSingleVar && view.getAssignmentRows().size() > 0) {
            view.showOnlySingleEntryAllowed();
        } else {
            addAssignment();
        }
    }

    public void setIsSingleVar(boolean isSingleVar) {
        this.isSingleVar = isSingleVar;
        if (variableType.equals(VariableType.INPUT)) {
            view.setProcessVarAsSource();
            if (isSingleVar) {
                view.setTableTitleInputSingle();
            } else {
                view.setTableTitleInputMultiple();
            }
        } else {
            view.setProcessVarAsTarget();
            if (isSingleVar) {
                view.setTableTitleOutputSingle();
            } else {
                view.setTableTitleOutputMultiple();
            }
        }
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public void setAllowDuplicateNames(boolean allowDuplicateNames,
                                       String duplicateNameErrorMessage) {
        this.allowDuplicateNames = allowDuplicateNames;
        this.duplicateNameErrorMessage = duplicateNameErrorMessage;
    }

    private boolean getShowConstants() {
        return (this.variableType == VariableType.INPUT) ? true : false;
    }

    private void addAssignment() {
        List<AssignmentRow> as = view.getAssignmentRows();
        if (as.isEmpty()) {
            view.setTableDisplayStyle();
        }

        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        as.add(newAssignment);

        AssignmentListItemWidgetView widget = view.getAssignmentWidget(view.getAssignmentsCount() - 1);
        widget.setDataTypes(dataTypeListBoxValues);
        widget.setProcessVariables(processVarListBoxValues);
        widget.setShowConstants(getShowConstants());

        widget.setDisallowedNames(disallowedNames,
                                  disallowedNameErrorMessage);
        widget.setAllowDuplicateNames(allowDuplicateNames,
                                      duplicateNameErrorMessage);
        widget.setParentWidget(this);
    }

    public void removeAssignment(AssignmentRow assignmentRow) {
        view.getAssignmentRows().remove(assignmentRow);

        if (view.getAssignmentRows().isEmpty()) {
            view.setNoneDisplayStyle();
        }
    }

    public void setData(List<AssignmentRow> assignmentRows) {
        // Hide the properties which shouldn't be shown
        hiddenPropertyRows.clear();
        if (disallowedNames != null && !disallowedNames.isEmpty()) {
            for (int i = assignmentRows.size() - 1; i >= 0; i--) {
                AssignmentRow row = assignmentRows.get(i);
                if (row.getName() != null && !row.getName().isEmpty()) {
                    if (disallowedNames.contains(row.getName().toLowerCase())) {
                        assignmentRows.remove(i);
                        hiddenPropertyRows.add(0,
                                               row);
                    }
                }
            }
        }

        if (assignmentRows.isEmpty()) {
            view.setNoneDisplayStyle();
        } else {
            view.setTableDisplayStyle();
        }

        view.setAssignmentRows(assignmentRows);

        for (int i = 0; i < assignmentRows.size(); i++) {
            view.getAssignmentWidget(i).setParentWidget(this);
            view.getAssignmentWidget(i).setDisallowedNames(disallowedNames,
                                                           disallowedNameErrorMessage);
            view.getAssignmentWidget(i).setAllowDuplicateNames(allowDuplicateNames,
                                                               duplicateNameErrorMessage);
        }
    }

    public List<AssignmentRow> getData() {
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        if (!view.getAssignmentRows().isEmpty()) {
            rows.addAll(view.getAssignmentRows());
        }
        if (!hiddenPropertyRows.isEmpty()) {
            rows.addAll(hiddenPropertyRows);
        }
        return rows;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setDataTypes(ListBoxValues dataTypeListBoxValues) {
        this.dataTypeListBoxValues = dataTypeListBoxValues;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            view.getAssignmentWidget(i).setDataTypes(dataTypeListBoxValues);
        }
    }

    public void setProcessVariables(ListBoxValues processVarListBoxValues) {
        this.processVarListBoxValues = processVarListBoxValues;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            AssignmentListItemWidgetView widget = view.getAssignmentWidget(i);
            widget.setProcessVariables(processVarListBoxValues);
            widget.setShowConstants(getShowConstants());
        }
    }

    public void setDisallowedNames(Set<String> disallowedNames,
                                   String disallowedNameErrorMessage) {
        this.disallowedNames = disallowedNames;
        this.disallowedNameErrorMessage = disallowedNameErrorMessage;

        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            view.getAssignmentWidget(i).setDisallowedNames(disallowedNames,
                                                           disallowedNameErrorMessage);
        }
    }

    public void setCustomAssignmentsProperties(final Map<String, List<String>> customAssignmentsProperties) {
        this.customAssignmentsProperties = customAssignmentsProperties;
        for (int i = 0; i < view.getAssignmentsCount(); i++) {
            view.getAssignmentWidget(i).setCustomAssignmentsProperties(this.customAssignmentsProperties);
        }
    }

    /**
     * Tests whether a Row name occurs more than once in the list of rows
     * @param name
     * @return
     */
    public boolean isDuplicateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        List<AssignmentRow> as = view.getAssignmentRows();
        if (as != null && !as.isEmpty()) {
            int nameCount = 0;
            for (AssignmentRow row : as) {
                if (name.trim().compareTo(row.getName()) == 0) {
                    nameCount++;
                    if (nameCount > 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setIsVisible(boolean visible) {
        view.setVisible(visible);
    }

    public Widget getWidget() {
        return (Widget) view;
    }
}
