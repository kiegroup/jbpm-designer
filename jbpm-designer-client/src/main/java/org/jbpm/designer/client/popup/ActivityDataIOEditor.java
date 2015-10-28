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
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.jboss.errai.marshalling.client.Marshalling;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.jbpm.designer.client.util.ListBoxValues;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class ActivityDataIOEditor extends BaseModal {

    /**
     * Callback interface which should be implemented by callers to retrieve the
     * edited Assignments data.
     */
    public interface GetDataCallback {
        void getData(String assignmentData);
    }
    GetDataCallback callback = null;

    boolean hasInputVars = true;
    boolean isSingleInputVar = false;
    boolean hasOutputVars = true;
    boolean isSingleOutputVar = false;

    @Inject
    private ActivityDataIOEditorWidget inputAssignmentsWidget;

    @Inject
    private ActivityDataIOEditorWidget outputAssignmentsWidget;

    private Button btnSave;

    private Button btnCancel;

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column( ColumnSize.MD_12 );

    public ActivityDataIOEditor() {
        super();
    }

    private List<String> dataTypes = new ArrayList<String>();
    private List<String> dataTypeDisplayNames = new ArrayList<String>();
    private AssignmentData assignmentData;


    ListBoxValues dataTypeListBoxValues = new ListBoxValues(AssignmentListItemWidget.CUSTOM_PROMPT, DesignerEditorConstants.INSTANCE.Edit() + " ",
            new ListBoxValues.ValueTester() {
                public String getNonCustomValueForUserString(String userValue) {
                    if (assignmentData != null) {
                        return assignmentData.getDataTypeDisplayNameForUserString(userValue);
                    }
                    else {
                        return null;
                    }
                }
            });

    ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidget.CONSTANT_PROMPT, DesignerEditorConstants.INSTANCE.Edit() + " ",
            new ListBoxValues.ValueTester() {
                public String getNonCustomValueForUserString(String userValue) {
                    return null;
                }
            });

    @PostConstruct
    public void init() {
        container.setFluid( true );
        container.add( row );
        row.add( column );

        setTitle( DesignerEditorConstants.INSTANCE.Data_IO() );

        inputAssignmentsWidget.setVariableType( VariableType.INPUT );
        inputAssignmentsWidget.setAllowDuplicateNames(false, DesignerEditorConstants.INSTANCE.A_Data_Input_with_this_name_already_exists());
        column.add( inputAssignmentsWidget );

        outputAssignmentsWidget.setVariableType( VariableType.OUTPUT );
        outputAssignmentsWidget.setAllowDuplicateNames(true, "");
        column.add( outputAssignmentsWidget );

        final Row btnRow = new Row();
        btnRow.getElement().getStyle().setMarginTop( 10, Style.Unit.PX );
        final Column btnColumn = new Column( ColumnSize.MD_12 );
        btnRow.add( btnColumn );
        btnSave = new Button( DesignerEditorConstants.INSTANCE.Save() );
        btnSave.setType( ButtonType.PRIMARY );
        btnSave.setIcon( IconType.SAVE );
        btnSave.setPull( Pull.RIGHT );
        btnSave.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( callback != null ) {
                    AssignmentData data = new AssignmentData( inputAssignmentsWidget.getData(),
                            outputAssignmentsWidget.getData(), dataTypes, dataTypeDisplayNames );
                    String sData = Marshalling.toJSON( data );
                    callback.getData( sData );
                }
                hide();
            }
        } );
        btnColumn.add( btnSave );

        btnCancel = new Button( DesignerEditorConstants.INSTANCE.Cancel() );
        btnCancel.setPull( Pull.RIGHT );
        btnCancel.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                hide();
            }
        });
        btnColumn.add( btnCancel );
        container.add( btnRow );

        setBody( container );
    }

    public void configureDialog(String taskName, boolean hasInputVars, boolean isSingleInputVar, boolean hasOutputVars, boolean isSingleOutputVar) {
        if (taskName != null && !taskName.isEmpty()) {
            setTitle(taskName + " " + DesignerEditorConstants.INSTANCE.Data_IO());
        }
        else {
            setTitle(DesignerEditorConstants.INSTANCE.Data_IO());
        }

        this.hasInputVars = hasInputVars;
        this.isSingleInputVar = isSingleInputVar;
        this.hasOutputVars = hasOutputVars;
        this.isSingleOutputVar = isSingleOutputVar;

        if (this.hasInputVars) {
            inputAssignmentsWidget.setVisible(true);
        }
        else {
            inputAssignmentsWidget.setVisible(false);
        }
        if (this.hasOutputVars) {
            outputAssignmentsWidget.setVisible(true);
        }
        else {
            outputAssignmentsWidget.setVisible(false);
        }

        inputAssignmentsWidget.setIsSingleVar(this.isSingleInputVar);
        outputAssignmentsWidget.setIsSingleVar(this.isSingleOutputVar);
    }

    @Override
    public void onHide(Event e) {
    }

    public void setCallback(GetDataCallback callback) {
        this.callback = callback;
    }

    public void setInputAssignmentRows(List<AssignmentRow> inputAssignmentRows) {
        inputAssignmentsWidget.setData(inputAssignmentRows);
    }

    public void setOutputAssignmentRows(List<AssignmentRow> outputAssignmentRows) {
        outputAssignmentsWidget.setData(outputAssignmentRows);
    }

    public void setAssignmentData(AssignmentData assignmentData) {
        this.assignmentData = assignmentData;
    }

    public void setDataTypes(List<String> dataTypes, List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        List<String> displayDataTypes =  new ArrayList<String>();
        displayDataTypes.addAll(dataTypeDisplayNames);

        dataTypeListBoxValues.addValues(displayDataTypes);
        inputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
        outputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    public void setDisallowedPropertyNames(List<String> disallowedPropertyNames) {
        Set<String> propertyNames = new HashSet<String>();
        if (disallowedPropertyNames != null) {
            for (String name : disallowedPropertyNames) {
                propertyNames.add(name.toLowerCase());
            }
        }
        inputAssignmentsWidget.setDisallowedNames(propertyNames, DesignerEditorConstants.INSTANCE.This_input_should_be_entered_as_a_property_for_the_task());
    }

    public void setProcessVariables(List<String> processVariables) {
        List<String> displayProcessVariables = new ArrayList<String>();
        displayProcessVariables.addAll(processVariables);

        processVarListBoxValues.addValues(displayProcessVariables);
        inputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
        outputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }
}
