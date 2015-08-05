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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.popup.ActivityDataIOEditor.ListBoxValues;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget" )
public class ActivityDataIOEditorWidget extends Composite {

    private static Set<String> hiddenPropertyNames = new HashSet<String>();
    static {
        hiddenPropertyNames.add("GroupId");
        hiddenPropertyNames.add("Skippable");
        hiddenPropertyNames.add("Comment");
        hiddenPropertyNames.add("Description");
        hiddenPropertyNames.add("Priority");
        hiddenPropertyNames.add("Content");
        hiddenPropertyNames.add("TaskName");
        hiddenPropertyNames.add("Locale");
        hiddenPropertyNames.add("CreatedBy");
        hiddenPropertyNames.add("NotCompletedReassign");
        hiddenPropertyNames.add("NotStartedReassign");
        hiddenPropertyNames.add("NotCompletedNotify");
        hiddenPropertyNames.add("NotStartedNotify");
    }

    ListBoxValues dataTypeListBoxValues;
    ListBoxValues processVarListBoxValues;

    private VariableType variableType = VariableType.INPUT;

    boolean isSingleVar = false;

    @Inject
    @DataField
    private Button addVarButton;

    @DataField
    private final Element table = DOM.createTable();

    @DataField
    private HeadingElement tabletitle = Document.get().createHElement( 3 );

    @DataField
    private final Element processvarorconstantth = DOM.createTH();

    /**
     * The list of assignments that currently exist.
     */
    @Inject
    @DataField
    @Table(root="tbody")
    private ListWidget<AssignmentRow, AssignmentListItemWidget> assignments;

    // List of rows that won't be shown in the UI
    List<AssignmentRow> hiddenPropertyRows = new ArrayList<AssignmentRow>();

    @Inject
    private Event<NotificationEvent> notification;


    @PostConstruct
    public void init() {
        addVarButton.setText(DesignerEditorConstants.INSTANCE.Add());
        addVarButton.setIcon( IconType.PLUS );
    }

    public void setIsSingleVar(boolean isSingleVar) {
        this.isSingleVar = isSingleVar;
        if (variableType.equals(VariableType.INPUT)) {
            processvarorconstantth.setInnerText(DesignerEditorConstants.INSTANCE.Source());
            if (isSingleVar) {
                tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Input_and_Assignment());
            }
            else {
                tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Inputs_and_Assignments());
            }
        }
        else {
            processvarorconstantth.setInnerText(DesignerEditorConstants.INSTANCE.Target());
            if (isSingleVar) {
                tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Output_and_Assignment());
            }
            else {
                tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Outputs_and_Assignments());
            }
        }
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    @EventHandler("addVarButton")
    public void handleAddVarButton(ClickEvent e) {
        if (isSingleVar && assignments.getValue().size() > 0) {
            notification.fire(new NotificationEvent(DesignerEditorConstants.INSTANCE.Only_single_entry_allowed(), NotificationEvent.NotificationType.ERROR));
        }
        else {
            addAssignment();
        }
    }

    public void addAssignment() {
        List<AssignmentRow> as = assignments.getValue();
        if (as.isEmpty()) {
            table.getStyle().setDisplay(Style.Display.TABLE);
        }

        AssignmentRow newAssignment = new AssignmentRow();
        newAssignment.setVariableType(variableType);
        as.add(newAssignment);

        AssignmentListItemWidget widget = assignments.getWidget(assignments.getValue().size() - 1);
        widget.setDataTypes(dataTypeListBoxValues);
        widget.setProcessVariables(processVarListBoxValues);
        widget.setParentWidget(this);
    }

    public void removeAssignment(AssignmentRow assignmentRow) {
        assignments.getValue().remove(assignmentRow);

        if (assignments.getValue().isEmpty()) {
            table.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public void setData(List<AssignmentRow> assignmentRows) {
        // Hide the properties which shouldn't be shown
        hiddenPropertyRows.clear();
        for (int i = assignmentRows.size() - 1; i >= 0; i--) {
            AssignmentRow row = assignmentRows.get(i);
            if (row.getName() != null && !row.getName().isEmpty() && hiddenPropertyNames.contains(row.getName())) {
                assignmentRows.remove(i);
                hiddenPropertyRows.add(0, row);
            }
        }

        if (assignmentRows.isEmpty()) {
            table.getStyle().setDisplay(Style.Display.NONE);
        }
        else {
            table.getStyle().setDisplay(Style.Display.TABLE);
        }

        assignments.setValue(assignmentRows);

        for (int i = 0; i < assignmentRows.size(); i++) {
            assignments.getWidget(i).setParentWidget(this);
        }
    }

    public List<AssignmentRow> getData() {
        List<AssignmentRow> rows = new ArrayList<AssignmentRow>();
        if (!assignments.getValue().isEmpty()) {
            rows.addAll(assignments.getValue());
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
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setDataTypes(dataTypeListBoxValues);
        }
    }

    public void setProcessVariables(ListBoxValues processVarListBoxValues) {
        this.processVarListBoxValues = processVarListBoxValues;
        for (int i = 0; i < assignments.getValue().size(); i++) {
            assignments.getWidget(i).setProcessVariables(processVarListBoxValues);
        }
    }

}
