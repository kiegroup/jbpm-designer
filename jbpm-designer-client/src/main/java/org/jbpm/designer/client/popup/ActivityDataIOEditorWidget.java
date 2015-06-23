/*
 * Copyright 2015 JBoss Inc
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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.popup.ActivityDataIOEditor.ListBoxValues;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget" )
public class ActivityDataIOEditorWidget extends Composite {


    private List<String> dataTypes;
    private List<String> processVariables;
    ListBoxValues dataTypeListBoxValues;
    ListBoxValues processVarListBoxValues;

    private VariableType variableType = VariableType.INPUT;

    boolean isSingleVar = false;

    @Inject
    @DataField
    private Button addVarButton;

    @DataField
    private final Element tabletitle = DOM.createLabel();

    @DataField
    private final Element processvarorconstantth = DOM.createTH();

    /**
     * The list of assignments that currently exist.
     */
    @Inject
    @DataField
    @Table(root="tbody")
    private ListWidget<AssignmentRow, AssignmentListItemWidget> assignments;

    @Inject
    private Event<NotificationEvent> notification;


    @PostConstruct
    public void init() {
    }

    public void setIsSingleVar(boolean isSingleVar) {
        this.isSingleVar = isSingleVar;
        if (variableType.equals(VariableType.INPUT)) {
            processvarorconstantth.setInnerText("Source");
            if (isSingleVar) {
                tabletitle.setInnerText("Data Input and Assignment");
            }
            else {
                tabletitle.setInnerText("Data Inputs and Assignments");
            }
        }
        else {
            processvarorconstantth.setInnerText("Target");
            if (isSingleVar) {
                tabletitle.setInnerText("Data Output and Assignment");
            }
            else {
                tabletitle.setInnerText("Data Outputs and Assignments");
            }
        }
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    @EventHandler("addVarButton")
    public void handleAddvarButton(ClickEvent e) {
        if (isSingleVar && assignments.getValue().size() > 0) {
            notification.fire(new NotificationEvent("Only single entry allowed", NotificationEvent.NotificationType.ERROR));
        }
        else {
            addAssignment();
        }
    }

    public void addAssignment() {
        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        List<AssignmentRow> as = assignments.getValue();
        as.add(newAssignment);

        AssignmentListItemWidget widget = assignments.getWidget(assignments.getValue().size() - 1);
        widget.setDataTypes(dataTypes, dataTypeListBoxValues);
        widget.setProcessVariables(processVariables, processVarListBoxValues);
        widget.setAssignments(assignments.getValue());
    }

    public void setData(List<AssignmentRow> assignmentRows) {
        assignments.setValue(assignmentRows);

        for (int i = 0; i < assignmentRows.size(); i++) {
            assignments.getWidget(i).setAssignments(assignments.getValue());
        }
    }

    public List<AssignmentRow> getData() {
        return assignments.getValue();
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setDataTypes(List<String> dataTypes, ListBoxValues dataTypeListBoxValues) {
        this.dataTypes = dataTypes;
        this.dataTypeListBoxValues = dataTypeListBoxValues;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setDataTypes(dataTypes, dataTypeListBoxValues);
        }
    }

    public void setProcessVariables(List<String> processVariables, ListBoxValues processVarListBoxValues) {
        this.processVariables = processVariables;
        this.processVarListBoxValues = processVarListBoxValues;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setProcessVariables(processVariables, processVarListBoxValues);
        }
    }

}
