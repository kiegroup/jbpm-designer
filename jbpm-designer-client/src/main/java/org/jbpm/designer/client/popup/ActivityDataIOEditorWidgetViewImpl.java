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
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Dependent
@Templated("ActivityDataIOEditorWidget.html#widget" )
public class ActivityDataIOEditorWidgetViewImpl extends Composite implements ActivityDataIOEditorWidgetView {

    private Presenter presenter;

    @Inject
    @DataField
    protected Button addVarButton;

    @DataField
    private final Element table = DOM.createTable();

    @DataField
    private HeadingElement tabletitle = Document.get().createHElement(3);

    @DataField
    private final Element processvarorconstantth = DOM.createTH();

    /**
     * The list of assignments that currently exist.
     */
    @Inject
    @DataField
    @Table(root="tbody")
    protected ListWidget<AssignmentRow, AssignmentListItemWidgetViewImpl> assignments;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
        addVarButton.setText(DesignerEditorConstants.INSTANCE.Add());
        addVarButton.setIcon(IconType.PLUS);
    }

    @Override
    public void showOnlySingleEntryAllowed() {
        notification.fire(new NotificationEvent(DesignerEditorConstants.INSTANCE.Only_single_entry_allowed(), NotificationEvent.NotificationType.ERROR));
    }

    @Override
    public int getAssignmentsCount() {
        return assignments.getValue().size();
    }

    @Override
    public void setTableTitleInputSingle() {
        tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Input_and_Assignment());
    }

    @Override
    public void setTableTitleInputMultiple() {
        tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Inputs_and_Assignments());
    }

    @Override
    public void setTableTitleOutputSingle() {
        tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Output_and_Assignment());
    }

    @Override
    public void setTableTitleOutputMultiple() {
        tabletitle.setInnerText(DesignerEditorConstants.INSTANCE.Data_Outputs_and_Assignments());
    }

    @Override
    public void setProcessVarAsSource() {
        processvarorconstantth.setInnerText(DesignerEditorConstants.INSTANCE.Source());
    }

    @Override
    public void setProcessVarAsTarget() {
        processvarorconstantth.setInnerText(DesignerEditorConstants.INSTANCE.Target());
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.TABLE);
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void setAssignmentRows(List<AssignmentRow> rows) {
        assignments.setValue(rows);
    }

    @Override
    public List<AssignmentRow> getAssignmentRows() {
        return assignments.getValue();
    }

    @Override
    public AssignmentListItemWidgetView getAssignmentWidget(int index) {
        return assignments.getWidget(index);
    }

    @EventHandler("addVarButton")
    public void handleAddVarButton(ClickEvent e) {
        presenter.handleAddClick();
    }
}
