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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.ValueListBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.popup.ActivityDataIOEditor.ListBoxValues;
import org.jbpm.designer.client.shared.AssignmentData;
import org.jbpm.designer.client.shared.AssignmentRow;
import org.jbpm.designer.client.shared.Variable.VariableType;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link AssignmentRow}s.
 *
 * The Name field of AssignmentRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated("ActivityDataIOEditorWidget.html#assignment")
public class AssignmentListItemWidget extends Composite implements HasModel<AssignmentRow> {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(AssignmentRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. assignment.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    private DataBinder<AssignmentRow> assignment;

    @Inject
    @Bound
    @DataField
    private TextBox name;

    @DataField
    private ValueListBox<String> dataType = new ValueListBox<String>(new Renderer<String>() {
        public String render(String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }
        public void render(String object, Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    private TextBox customDataType;

    @DataField
    private final Element direction = DOM.createTD();

    @DataField
    private ValueListBox<String> processVar = new ValueListBox<String>(new Renderer<String>() {
        public String render(String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }
        public void render(String object, Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });


    Map<ValueListBox<String>, ListBoxValues> mapListBoxToValues = new HashMap<ValueListBox<String>, ListBoxValues>();
    Map<ValueListBox<String>, Boolean> mapListBoxToShowCustomValues = new HashMap<ValueListBox<String>, Boolean>();

    public static final String EDIT_PROMPT = "Edit ";
    public static final String CUSTOM_PROMPT = "Custom ...";
    public static final String ENTER_TYPE_PROMPT = "Enter type...";
    public static final String CONSTANT_PROMPT = "Constant ...";
    public static final String ENTER_CONSTANT_PROMPT = "Enter constant...";

    @Inject
    @DataField
    private TextBox constant;

    @Inject
    @DataField
    private Button deleteButton;

    /**
     * List of Assignments the current assignment is in.
     * Required for implementation of Delete button.
     */
    private List<AssignmentRow> assignments;

    public void setAssignments(List<AssignmentRow> assignments) {
        this.assignments = assignments;
    }

    /**
     * Initializes an 'EditableListBox' control, which is a combination of
     * a ValueListBox<String> and a TextBox used to set values via a drop-down
     * and also allow new custom values to be added and edited.
     *
     * @param listBox
     * @param textBox
     * @param bQuoteStringValues
     * @param customPrompt
     * @param placeholder
     * @param editPrompt
     */
    private void initEditableListBox(final ValueListBox<String> listBox, final TextBox textBox, final boolean bQuoteStringValues,
            final String customPrompt, final String placeholder, final String editPrompt) {
        textBox.setVisible(false);
        textBox.setPlaceholder(placeholder);
        listBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                String newValue = valueChangeEvent.getValue();
                if (customPrompt.equals(newValue)) {
                    // "Custom..." selected, show textBox with empty value
                    setModelValue(listBox, "");
                    setModelValue(textBox, "");
                    listBox.setVisible(false);
                    textBox.setVisible(true);
                    textBox.setFocus(true);
                } else if (newValue.startsWith(editPrompt)) {
                    // "Edit..." selected, show textBox with appropriate value
                    setModelValue(listBox, "");
                    String quotedValue = newValue.substring(editPrompt.length(), newValue.length() - 3);
                    setModelValue(textBox, AssignmentData.createUnquotedConstant(quotedValue));
                    listBox.setVisible(false);
                    textBox.setVisible(true);
                    textBox.setFocus(true);
                } else if (getListBoxValues(listBox).isCustomValue(newValue)) {
                    // A Custom value has been selected
                    String textValue = newValue;
                    if (bQuoteStringValues) {
                        textValue = AssignmentData.createUnquotedConstant(newValue);
                    }
                    setModelValue(listBox, newValue);
                    setModelValue(textBox, textValue);
                } else if (newValue != null) {
                    // A non-custom value has been selected
                    setModelValue(listBox, newValue);
                    setModelValue(textBox, "");
                }
            }
        });

        listBox.addDomHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent focusEvent) {
                // Update drop-down list values on listBox FocusEvent
                boolean showCustomValues = mapListBoxToShowCustomValues.get(listBox);
                getListBoxValues(listBox).update(listBox, showCustomValues);
            }
        }, FocusEvent.getType());

        textBox.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                // Update ListBoxValues and set model values when textBox loses focus
                String value = textBox.getValue();
                if (value != null) {
                    if (!value.isEmpty()) {
                        addValueToListBoxValues(listBox, value, editPrompt, bQuoteStringValues);
                    }
                    if (bQuoteStringValues) {
                        value = AssignmentData.createQuotedConstant(value);
                    }
                    // Set the value even if it's ""
                    setModelValue(textBox, value);
                    setModelValue(listBox, value);
                }
                textBox.setVisible(false);
                listBox.setVisible(true);
            }
        });
    }

    protected ListBoxValues getListBoxValues(final ValueListBox<String> listBox) {
        return mapListBoxToValues.get(listBox);
    }

    protected void addValueToListBoxValues(final ValueListBox<String> listBox, String value, String editPrompt,
            boolean bQuoteStringValues) {
        if (bQuoteStringValues) {
            value = AssignmentData.createQuotedConstant(value);
        }
        String promptWithValue = editPrompt + value + "...";
        boolean showCustomValues = mapListBoxToShowCustomValues.get(listBox);
        getListBoxValues(listBox).addValue(listBox, value, promptWithValue, value, showCustomValues);
    }

    protected void setModelValue(final TextBox textBox, String value) {
        textBox.setValue(value);
        if (textBox == customDataType) {
            assignment.getModel().setCustomDataType(value);
        }
        else if (textBox == constant) {
            assignment.getModel().setConstant(value);
        }
    }

    protected void setModelValue(final ValueListBox<String> listBox, String value) {
        listBox.setValue(value);
        if (listBox == dataType) {
            assignment.getModel().setDataType(value);
        }
        else if (listBox == processVar) {
            assignment.getModel().setProcessVar(value);
        }
    }

    @PostConstruct
    private void init() {
        // Configure dataType and customDataType controls
        initEditableListBox(dataType, customDataType, false, CUSTOM_PROMPT, ENTER_TYPE_PROMPT, EDIT_PROMPT);

        // Configure processVar and constant controls
        initEditableListBox(processVar, constant, true, CONSTANT_PROMPT, ENTER_CONSTANT_PROMPT, EDIT_PROMPT);
    }

    @PreDestroy
    private void cleanUp() {

    }

    @Override
    public AssignmentRow getModel() {
        return assignment.getModel();
    }

    @Override
    public void setModel(AssignmentRow model) {
        assignment.setModel(model);
        initAssignmentControls();
    }

    public void setDataTypes(List<String> dataTypes, ListBoxValues listBoxValues) {
        mapListBoxToValues.put(dataType, listBoxValues);
        mapListBoxToShowCustomValues.put(dataType, true);
        listBoxValues.register(dataType, dataTypes, true);
        String cdt = assignment.getModel().getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            addValueToListBoxValues(dataType, cdt, EDIT_PROMPT, false);
        }
    }

    public void setProcessVariables(List<String> processVariables, ListBoxValues listBoxValues) {
        mapListBoxToValues.put(processVar, listBoxValues);
        boolean showCustomValues = false;
        if (assignment.getModel().getVariableType() == VariableType.INPUT) {
            showCustomValues = true;
        }
        mapListBoxToShowCustomValues.put(processVar, showCustomValues);
        listBoxValues.register(processVar, processVariables, showCustomValues);
        String con = assignment.getModel().getConstant();
        if (con != null && !con.isEmpty()) {
            addValueToListBoxValues(processVar, con, EDIT_PROMPT, true);
        }
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(ClickEvent e) {
        assignments.remove(assignment.getModel());
    }


    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link AssignmentRow}.
     */
    private void initAssignmentControls() {
        deleteButton.setIcon(IconType.REMOVE);

        if (assignment.getModel().getVariableType() == VariableType.INPUT) {
            direction.appendChild(new Icon(IconType.ARROW_LEFT).getElement());
        }
        else {
            direction.appendChild(new Icon(IconType.ARROW_RIGHT).getElement());
            constant.setVisible(false);
        }

        String cdt = assignment.getModel().getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            customDataType.setValue(cdt);
            dataType.setValue(cdt);
        }
        else if (assignment.getModel().getDataType() != null){
            dataType.setValue(assignment.getModel().getDataType());
        }

        String con = assignment.getModel().getConstant();
        if (con != null && !con.isEmpty()) {
            con = AssignmentData.createQuotedConstant(con);
            constant.setValue(con);
            processVar.setValue(con);
        }
        else if (assignment.getModel().getProcessVar() != null){
            processVar.setValue(assignment.getModel().getProcessVar());
        }
    }

}
