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
import java.util.List;
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

    /**
     * Class containing a list of values for a ValueListBox<String>.
     * This is used by the ListBoxes in the dialog to keep their drop-down lists
     * up to date with updated with new values (CustomDataTypes / Constants) as
     * the user adds them.
     */
    class ListBoxValues {
        List<String> acceptableValuesWithCustomValues = new ArrayList<String>();
        List<String> acceptableValuesWithoutCustomValues = new ArrayList<String>();
        List<String> customValues = new ArrayList<String>();

        void update(ValueListBox<String> listBox, boolean showCustomValues) {
            if (showCustomValues) {
                String currentValue = listBox.getValue();
                String currentEditValuePrompt = getEditValuePrompt();
                String newEditValuePrompt = AssignmentListItemWidget.EDIT_PREFIX + currentValue + AssignmentListItemWidget.EDIT_SUFFIX;
                if (isCustomValue(currentValue)) {
                    if (newEditValuePrompt.equals(currentEditValuePrompt)) {
                        return;
                    }
                    if (currentEditValuePrompt != null) {
                        acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
                    }
                    int editPromptIndex = acceptableValuesWithCustomValues.indexOf(currentValue);
                    if (editPromptIndex > -1) {
                        editPromptIndex++;
                    }
                    else if (acceptableValuesWithCustomValues.size() > 1) {
                        editPromptIndex = 2;
                    }
                    else {
                        editPromptIndex = acceptableValuesWithCustomValues.size();
                    }
                    acceptableValuesWithCustomValues.add(editPromptIndex, newEditValuePrompt);
                }
                else if (currentEditValuePrompt != null) {
                    acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
                }
                listBox.setAcceptableValues(acceptableValuesWithCustomValues);
            }
            else {
                listBox.setAcceptableValues(acceptableValuesWithoutCustomValues);
            }
        }

        void addValues(List<String> acceptableValues) {
            clear();
            if (acceptableValues != null) {
                acceptableValuesWithCustomValues.addAll(acceptableValues);
                for (int i = 0; i < acceptableValues.size(); i++) {
                    String value = acceptableValues.get(i);
                    if (!acceptableValuesWithoutCustomValues.contains(value)
                            && !value.endsWith("...")) {
                        acceptableValuesWithoutCustomValues.add(value);
                    }
                }
            }
        }

        private void clear() {
            customValues.clear();
            acceptableValuesWithCustomValues.clear();
            acceptableValuesWithoutCustomValues.clear();
        }

        private String getEditValuePrompt() {
            if (acceptableValuesWithCustomValues.size() > 0) {
                for (int i = 0; i < acceptableValuesWithCustomValues.size(); i++) {
                    String value = acceptableValuesWithCustomValues.get(i);
                    if (value.startsWith(AssignmentListItemWidget.EDIT_PREFIX)) {
                        return value;
                    }
                }
            }
            return null;
        }

        void addValue(String newValue, String oldValue) {
            if (oldValue != null && !oldValue.isEmpty())
            {
                if (acceptableValuesWithCustomValues.contains(oldValue)) {
                    acceptableValuesWithCustomValues.remove(oldValue);
                }
                if (customValues.contains(oldValue)) {
                    customValues.remove(oldValue);
                }
            }

            if (newValue != null && !newValue.isEmpty()) {
                if (!acceptableValuesWithCustomValues.contains(newValue)) {
                    int index = 1;
                    if (acceptableValuesWithCustomValues.size() < 1)
                    {
                        index = acceptableValuesWithCustomValues.size();
                    }
                    acceptableValuesWithCustomValues.add(index, newValue);
                }
                if (!customValues.contains(newValue)) {
                    customValues.add(newValue);
                }
            }
        }

        boolean isCustomValue(String value) {
            if (value == null || value.isEmpty()) {
                return false;
            }
            else {
                return customValues.contains(value);
            }
        }
    }

    ListBoxValues dataTypeListBoxValues = new ListBoxValues();
    ListBoxValues processVarListBoxValues = new ListBoxValues();

    @PostConstruct
    public void init() {
        container.setFluid( true );
        container.add( row );
        row.add( column );

        setTitle( DesignerEditorConstants.INSTANCE.Data_IO() );

        inputAssignmentsWidget.setVariableType( VariableType.INPUT );
        column.add( inputAssignmentsWidget );

        outputAssignmentsWidget.setVariableType( VariableType.OUTPUT );
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

    public void setDataTypes(List<String> dataTypes, List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;

        List<String> displayDataTypes =  new ArrayList<String>();
        displayDataTypes.add("");
        displayDataTypes.add(AssignmentListItemWidget.CUSTOM_PROMPT);
        displayDataTypes.addAll(dataTypeDisplayNames);

        dataTypeListBoxValues.addValues(displayDataTypes);
        inputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
        outputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    public void setProcessVariables(List<String> processVariables) {
        List<String> displayProcessVariables = new ArrayList<String>();
        displayProcessVariables.add("");
        displayProcessVariables.add(AssignmentListItemWidget.CONSTANT_PROMPT);
        displayProcessVariables.addAll(processVariables);

        processVarListBoxValues.addValues(displayProcessVariables);
        inputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
        outputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }
}
