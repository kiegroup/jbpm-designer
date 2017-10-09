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

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;
import org.jbpm.designer.client.util.ListBoxValues;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class ActivityDataIOEditorViewImpl extends BaseModal implements ActivityDataIOEditorView {

    protected Presenter presenter;

    @Inject
    protected ActivityDataIOEditorWidget inputAssignmentsWidget;

    @Inject
    protected ActivityDataIOEditorWidget outputAssignmentsWidget;

    private Button btnSave;

    private Button btnCancel;

    private Container container = new Container();

    private Row row = new Row();

    private Column column = new Column(ColumnSize.MD_12);

    public static final int CONSTANT_MAX_DISPLAY_LENGTH = 10;

    public ActivityDataIOEditorViewImpl() {
        super();
    }

    public void init(final Presenter presenter) {
        this.presenter = presenter;
        container.setFluid(true);
        container.add(row);
        row.add(column);

        setTitle(DesignerEditorConstants.INSTANCE.Data_IO());

        inputAssignmentsWidget.setVariableType(VariableType.INPUT);
        inputAssignmentsWidget.setAllowDuplicateNames(false,
                                                      DesignerEditorConstants.INSTANCE.A_Data_Input_with_this_name_already_exists());
        column.add(inputAssignmentsWidget.getWidget());

        outputAssignmentsWidget.setVariableType(VariableType.OUTPUT);
        outputAssignmentsWidget.setAllowDuplicateNames(true,
                                                       "");
        column.add(outputAssignmentsWidget.getWidget());

        final Row btnRow = new Row();
        btnRow.getElement().getStyle().setMarginTop(10,
                                                    Style.Unit.PX);
        final Column btnColumn = new Column(ColumnSize.MD_12);
        btnRow.add(btnColumn);
        btnSave = new Button(DesignerEditorConstants.INSTANCE.Save());
        btnSave.setType(ButtonType.PRIMARY);
        btnSave.setIcon(IconType.SAVE);
        btnSave.setPull(Pull.RIGHT);
        btnSave.addClickHandler(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.handleSaveClick();
                    }
                }
        );
        btnColumn.add(btnSave);

        btnCancel = new Button(DesignerEditorConstants.INSTANCE.Cancel());
        btnCancel.setPull(Pull.RIGHT);
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                presenter.handleCancelClick();
            }
        });
        btnColumn.add(btnCancel);
        container.add(btnRow);

        setBody(container);
    }

    @Override
    public void onHide(Event e) {
    }

    @Override
    public void setCustomViewTitle(String name) {
        setTitle(name + " " + DesignerEditorConstants.INSTANCE.Data_IO());
    }

    @Override
    public void setDefaultViewTitle() {
        setTitle(DesignerEditorConstants.INSTANCE.Data_IO());
    }

    @Override
    public void setInputAssignmentRows(List<AssignmentRow> inputAssignmentRows) {
        inputAssignmentsWidget.setData(inputAssignmentRows);
    }

    @Override
    public void setOutputAssignmentRows(List<AssignmentRow> outputAssignmentRows) {
        outputAssignmentsWidget.setData(outputAssignmentRows);
    }

    @Override
    public void setInputAssignmentsVisibility(boolean visible) {
        inputAssignmentsWidget.setIsVisible(visible);
    }

    @Override
    public void setOutputAssignmentsVisibility(boolean visible) {
        outputAssignmentsWidget.setIsVisible(visible);
    }

    @Override
    public void setIsInputAssignmentSingleVar(boolean single) {
        inputAssignmentsWidget.setIsSingleVar(single);
    }

    @Override
    public void setIsOutputAssignmentSingleVar(boolean single) {
        outputAssignmentsWidget.setIsSingleVar(single);
    }

    @Override
    public void hideView() {
        super.hide();
    }

    @Override
    public void showView() {
        super.show();
    }

    @Override
    public List<AssignmentRow> getInputAssignmentData() {
        return inputAssignmentsWidget.getData();
    }

    @Override
    public List<AssignmentRow> getOutputAssignmentData() {
        return outputAssignmentsWidget.getData();
    }

    @Override
    public void setPossibleInputAssignmentsDataTypes(List<String> dataTypeDisplayNames) {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CUSTOM_PROMPT,
                                                                DesignerEditorConstants.INSTANCE.Edit() + " ",
                                                                presenter.dataTypesTester());
        dataTypeListBoxValues.addValues(dataTypeDisplayNames);
        inputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    @Override
    public void setPossibleOutputAssignmentsDataTypes(List<String> dataTypeDisplayNames) {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CUSTOM_PROMPT,
                                                                DesignerEditorConstants.INSTANCE.Edit() + " ",
                                                                presenter.dataTypesTester());
        dataTypeListBoxValues.addValues(dataTypeDisplayNames);
        outputAssignmentsWidget.setDataTypes(dataTypeListBoxValues);
    }

    @Override
    public void setInputAssignmentsProcessVariables(List<String> processVariables) {
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CONSTANT_PROMPT,
                                                                  DesignerEditorConstants.INSTANCE.Edit() + " ",
                                                                  presenter.processVarTester(),
                                                                  CONSTANT_MAX_DISPLAY_LENGTH);
        processVarListBoxValues.addValues(processVariables);
        inputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }

    @Override
    public void setOutputAssignmentsProcessVariables(List<String> processVariables) {
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CONSTANT_PROMPT,
                                                                  DesignerEditorConstants.INSTANCE.Edit() + " ",
                                                                  presenter.processVarTester());
        processVarListBoxValues.addValues(processVariables);
        outputAssignmentsWidget.setProcessVariables(processVarListBoxValues);
    }

    @Override
    public void setCustomAssignmentsProperties(final Map<String, List<String>> customAssignmentsProperties) {
        inputAssignmentsWidget.setCustomAssignmentsProperties(customAssignmentsProperties);
    }

    @Override
    public void setInputAssignmentsDisallowedNames(Set<String> names) {
        inputAssignmentsWidget.setDisallowedNames(names,
                                                  DesignerEditorConstants.INSTANCE.This_input_should_be_entered_as_a_property_for_the_task());
    }
}
