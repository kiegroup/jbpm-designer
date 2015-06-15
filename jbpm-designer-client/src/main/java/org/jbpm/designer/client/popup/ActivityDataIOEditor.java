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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import org.jboss.errai.marshalling.client.Marshalling;
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

    private Button btnOK;

    private Button btnCancel;


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

        void register(ValueListBox<String> listBox, List<String> acceptableValues,
                boolean showCustomValues) {
            if (acceptableValues != null) {
                for (int i = 0; i < acceptableValues.size(); i++) {
                    String value = acceptableValues.get(i);
                    if (! acceptableValuesWithCustomValues.contains(value)) {
                        acceptableValuesWithCustomValues.add(value);
                    }
                    else {
                        // all new entries to be added are at the start
                        break;
                    }
                }
                for (int i = 0; i < acceptableValues.size(); i++) {
                    String value = acceptableValues.get(i);
                    if (! acceptableValuesWithoutCustomValues.contains(value)
                            && ! value.endsWith("...")) {
                        acceptableValuesWithoutCustomValues.add(value);
                    }
                    else {
                        // all new entries to be added are at the start
                        break;
                    }
                }
            }
            update(listBox, showCustomValues);
        }

        void addValue(ValueListBox<String> listBox, String newValue, String newValuePrompt, String customValue,
                boolean showCustomValues) {
            if (newValuePrompt != null && !acceptableValuesWithCustomValues.contains(newValuePrompt)) {
                acceptableValuesWithCustomValues.add(0, newValuePrompt);
            }
            if (newValue != null && !acceptableValuesWithCustomValues.contains(newValue)) {
                acceptableValuesWithCustomValues.add(0, newValue);
            }
            if (customValue != null && !customValues.contains(customValue)) {
                customValues.add(customValue);
            }
            update(listBox, showCustomValues);
        }

        boolean isCustomValue(String value) {
            if (value == null || value.isEmpty()) {
                return false;
            }
            else {
                return customValues.contains(value);
            }
        }

        void update(ValueListBox<String> observer, boolean showCustomValues) {
            if (showCustomValues) {
                observer.setAcceptableValues(acceptableValuesWithCustomValues);
            }
            else {
                observer.setAcceptableValues(acceptableValuesWithoutCustomValues);
            }
        }
    }

    ListBoxValues dataTypeListBoxValues = new ListBoxValues();
    ListBoxValues processVarListBoxValues = new ListBoxValues();

    @PostConstruct
    public void init() {
        setTitle("Data I/O Editor");

        inputAssignmentsWidget.setVariableType(VariableType.INPUT);
        this.add(inputAssignmentsWidget);

        outputAssignmentsWidget.setVariableType(VariableType.OUTPUT);
        this.add(outputAssignmentsWidget);

        btnOK = new Button( "OK" );
        btnOK.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (callback != null) {
                    AssignmentData data = new AssignmentData(inputAssignmentsWidget.getData(),
                            outputAssignmentsWidget.getData(), dataTypes, dataTypeDisplayNames);
                    String sData = Marshalling.toJSON(data);
                    callback.getData(sData);
                }
                hide();
            }
        });
        this.add(btnOK);

        btnCancel = new Button( "Cancel" );
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                hide();
            }
        });
        this.add(btnCancel);
    }

    public void configureWidgets(boolean hasInputVars, boolean isSingleInputVar, boolean hasOutputVars, boolean isSingleOutputVar) {
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
    public void onShow(Event e) {
        double width = (double) Window.getClientWidth() * 0.6D;
        this.setWidth(width + "px");
        double maxHeight = (double) Window.getClientHeight() * 0.6D;
        this.setMaxHeigth(maxHeight + "px");
        double widgetWidth = width - 25;
        if (inputAssignmentsWidget.isVisible()) {
            inputAssignmentsWidget.setWidth(widgetWidth + "px");
        }
        if (outputAssignmentsWidget.isVisible()) {
            outputAssignmentsWidget.setWidth(widgetWidth + "px");
        }
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

        inputAssignmentsWidget.setDataTypes(dataTypeDisplayNames, dataTypeListBoxValues);
        outputAssignmentsWidget.setDataTypes(dataTypeDisplayNames, dataTypeListBoxValues);
    }

    public void setProcessVariables(List<String> processVariables) {
        List<String> inProcessVariables = new ArrayList<String>();
        inProcessVariables.add("Constant ...");
        inProcessVariables.addAll(processVariables);
        inputAssignmentsWidget.setProcessVariables(inProcessVariables, processVarListBoxValues);

        outputAssignmentsWidget.setProcessVariables(processVariables, processVarListBoxValues);
    }
}
