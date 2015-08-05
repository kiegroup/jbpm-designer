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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.designer.client.popup.ActivityDataIOEditor.ListBoxValues;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
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


    Map<ValueListBox<String>, ListBoxValues> mapListBoxToListBoxValues = new HashMap<ValueListBox<String>, ListBoxValues>();
    Map<ValueListBox<String>, Boolean> mapListBoxToShowCustomValues = new HashMap<ValueListBox<String>, Boolean>();
    Map<TextBox, String> mapTextBoxToCurrentValue = new HashMap<TextBox, String>();

    public static final String EDIT_PREFIX = DesignerEditorConstants.INSTANCE.Edit() + " ";
    public static final String EDIT_SUFFIX = " ...";

    public static final String CUSTOM_PROMPT = DesignerEditorConstants.INSTANCE.Custom() + EDIT_SUFFIX;
    public static final String ENTER_TYPE_PROMPT = DesignerEditorConstants.INSTANCE.Enter_type() + EDIT_SUFFIX;
    public static final String CONSTANT_PROMPT = DesignerEditorConstants.INSTANCE.Constant() + EDIT_SUFFIX;
    public static final String ENTER_CONSTANT_PROMPT = DesignerEditorConstants.INSTANCE.Enter_constant() + EDIT_SUFFIX;

    @Inject
    @DataField
    private TextBox constant;

    @Inject
    @DataField
    private Button deleteButton;

    /**
     * Widget the current assignment is in.
     * Required for implementation of Delete button.
     */
    private ActivityDataIOEditorWidget parentWidget;

    public void setParentWidget(ActivityDataIOEditorWidget parentWidget) {
        this.parentWidget = parentWidget;
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
     */
    private void initEditableListBox(final ValueListBox<String> listBox, final TextBox textBox, final boolean bQuoteStringValues,
            final String customPrompt, final String placeholder) {
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
                } else if (newValue.startsWith("*")) {
                    // Not a valid value
                    setModelValue(listBox, "");
                    setModelValue(textBox, "");
                 } else if (newValue.startsWith(EDIT_PREFIX)) {
                    // "Edit <value> ..." selected, show textBox with appropriate value
                    String quotedValue = getModelValue(listBox);
                    String unquotedValue = AssignmentData.createUnquotedConstant(quotedValue);
                    setModelValue(textBox, unquotedValue);
                    listBox.setVisible(false);
                    textBox.setVisible(true);
                    textBox.setFocus(true);
                } else if (getListBoxValues(listBox).isCustomValue(newValue)) {
                    // A Custom value has been selected
                    String textValue = newValue;
                    if (bQuoteStringValues) {
                        String unquotedValue = AssignmentData.createUnquotedConstant(newValue);
                        textValue = unquotedValue;
                    }
                    setModelValue(listBox, newValue);
                    setModelValue(textBox, textValue);
                } else if (newValue != null) {
                    // A non-custom value has been selected
                    setModelValue(listBox, newValue);
                    setModelValue(textBox, "");
                }
                updateListBoxValues(listBox);
            }
        });

        listBox.addDomHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent focusEvent) {
                updateListBoxValues(listBox);
            }
        }, FocusEvent.getType());


        textBox.addFocusHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent focusEvent) {
                setCurrentTextValue(textBox, textBox.getValue());
            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                // Update ListBoxValues and set model values when textBox loses focus
                String value = textBox.getValue();
                if (value != null) {
                    if (!bQuoteStringValues) {
                        value = value.trim();
                    }
                    if (!value.isEmpty()) {
                        String oldValue = getCurrentTextValue(textBox);
                        addValueToListBoxValues(listBox, value, oldValue, bQuoteStringValues);
                    }
                    if (bQuoteStringValues) {
                        value = AssignmentData.createQuotedConstant(value);
                    }
                    // Set the value even if it's ""
                    setModelValue(textBox, value);
                    setModelValue(listBox, value);
                    setCurrentTextValue(textBox, value);
                }
                textBox.setVisible(false);
                listBox.setVisible(true);
            }
        });
    }

    protected void updateListBoxValues(ValueListBox<String> listBox) {
        boolean showCustomValues = mapListBoxToShowCustomValues.get(listBox);
        getListBoxValues(listBox).update(listBox, showCustomValues);

    }

    protected String getCurrentTextValue(TextBox textBox) {
        String value = mapTextBoxToCurrentValue.get(textBox);
        if (value == null) {
            value = "";
        }
        return value;
    }

    protected void setCurrentTextValue(TextBox textBox, String value) {
        if (value == null) {
            value = "";
        }
        mapTextBoxToCurrentValue.put(textBox, value);
    }

    protected ListBoxValues getListBoxValues(final ValueListBox<String> listBox) {
        return mapListBoxToListBoxValues.get(listBox);
    }

    protected void addValueToListBoxValues(final ValueListBox<String> listBox, String newValue, String oldValue,
            boolean bQuoteStringValues) {
        if (bQuoteStringValues) {
            newValue = AssignmentData.createQuotedConstant(newValue);
            oldValue = AssignmentData.createQuotedConstant(oldValue);
        }
        getListBoxValues(listBox).addValue(newValue, oldValue);
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

    protected String getModelValue(final ValueListBox<String> listBox) {
        if (listBox == dataType) {
            String value = assignment.getModel().getCustomDataType();
            if (value == null || value.isEmpty()) {
                value = assignment.getModel().getDataType();
            }
            return value;
        }
        else if (listBox == processVar) {
            String value = assignment.getModel().getConstant();
            if (value == null || value.isEmpty()) {
                value = assignment.getModel().getProcessVar();
            }
            return value;
        }
        else {
            return "";
        }
    }

    @PostConstruct
    private void init() {
        // Configure dataType and customDataType controls
        initEditableListBox(dataType, customDataType, false, CUSTOM_PROMPT, ENTER_TYPE_PROMPT);

        // Configure processVar and constant controls
        initEditableListBox(processVar, constant, true, CONSTANT_PROMPT, ENTER_CONSTANT_PROMPT);

        // Configure name control
        name.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                String value = name.getValue();
                if (value != null) {
                    name.setValue(value.trim());
                }
            }
        });

        name.addKeyDownHandler(new KeyDownHandler() {
            @Override public void onKeyDown(KeyDownEvent event) {
                int iChar = event.getNativeKeyCode();
                if (iChar == ' ') {
                    event.preventDefault();
                }
            }
        });

        customDataType.addKeyDownHandler(new KeyDownHandler() {
            @Override public void onKeyDown(KeyDownEvent event) {
                int iChar = event.getNativeKeyCode();
                if (iChar == ' ') {
                    event.preventDefault();
                }
            }
        });
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

    public void setDataTypes(ListBoxValues dataTypeListBoxValues) {
        mapTextBoxToCurrentValue.put(customDataType, "");
        mapListBoxToListBoxValues.put(dataType, dataTypeListBoxValues);
        mapListBoxToShowCustomValues.put(dataType, true);
        String cdt = assignment.getModel().getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            addValueToListBoxValues(dataType, cdt, "", false);
        }
    }

    public void setProcessVariables(ListBoxValues processVarListBoxValues) {
        mapTextBoxToCurrentValue.put(constant, "");
        mapListBoxToListBoxValues.put(processVar, processVarListBoxValues);
        boolean showCustomValues = false;
        if (assignment.getModel().getVariableType() == VariableType.INPUT) {
            showCustomValues = true;
        }
        mapListBoxToShowCustomValues.put(processVar, showCustomValues);
        String con = assignment.getModel().getConstant();
        if (con != null && !con.isEmpty()) {
            addValueToListBoxValues(processVar, con, "", true);
        }
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(ClickEvent e) {
        parentWidget.removeAssignment(assignment.getModel());
    }


    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link AssignmentRow}.
     */
    private void initAssignmentControls() {
        deleteButton.setIcon( IconType.TRASH );

        if (assignment.getModel().getVariableType() == VariableType.OUTPUT) {
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
