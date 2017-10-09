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

package org.jbpm.designer.client.util;

import java.util.List;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jbpm.designer.client.shared.util.StringUtils;

public class ComboBox implements ComboBoxView.ComboBoxPresenter {

    protected ListBoxValues listBoxValues;

    protected boolean showCustomValues = false;

    protected String currentTextValue = "";

    protected boolean quoteStringValues;

    protected String customPrompt;

    @Inject
    ComboBoxView view;

    @Override
    public void init(final ComboBoxView.ModelPresenter modelPresenter,
                     final ValueListBox<String> listBox,
                     final TextBox textBox,
                     final boolean quoteStringValues,
                     final String customPrompt,
                     final String placeholder) {
        this.quoteStringValues = quoteStringValues;
        this.customPrompt = customPrompt;

        view.init(this,
                  modelPresenter,
                  listBox,
                  textBox,
                  placeholder);
    }

    @Override
    public String getValue() {
        return view.getValue();
    }

    @Override
    public void setListBoxValues(final ListBoxValues listBoxValues) {
        this.listBoxValues = listBoxValues;
    }

    @Override
    public void setShowCustomValues(final boolean showCustomValues) {
        this.showCustomValues = showCustomValues;
    }

    @Override
    public void setCurrentTextValue(String currentTextValue) {
        this.currentTextValue = currentTextValue;
    }

    @Override
    public ListBoxValues getListBoxValues() {
        return listBoxValues;
    }

    @Override
    public void updateListBoxValues(String listBoxValue) {
        if (showCustomValues) {
            List<String> updatedValues = listBoxValues.update(listBoxValue);
            view.setAcceptableValues(updatedValues);
        } else {
            List<String> values = listBoxValues.getAcceptableValuesWithoutCustomValues();
            view.setAcceptableValues(values);
        }
    }

    @Override
    public void listBoxValueChanged(String newValue) {
        if (customPrompt.equals(newValue)) {
            // "Custom..." selected, show textBox with empty value
            setListBoxValue("");
            setTextBoxValue("");
            view.setListBoxVisible(false);
            view.setTextBoxVisible(true);
            view.setTextBoxFocus(true);
        } else if (newValue.startsWith("*")) {
            // Not a valid value
            setListBoxValue("");
            setTextBoxValue("");
        } else if (newValue.startsWith(listBoxValues.getEditPrefix())) {
            // "Edit <value> ..." selected, show textBox with appropriate value
            String value = view.getModelValue();
            setTextBoxValue(value);
            view.setListBoxVisible(false);
            view.setTextBoxVisible(true);
            view.setTextBoxFocus(true);
        } else if (listBoxValues.isCustomValue(newValue)) {
            // A Custom value has been selected
            String textValue = listBoxValues.getValueForDisplayValue(newValue);
            if (quoteStringValues) {
                textValue = StringUtils.createUnquotedConstant(textValue);
            }
            setListBoxValue(newValue);
            setTextBoxValue(textValue);
        } else if (newValue != null) {
            // A non-custom value has been selected
            setListBoxValue(newValue);
            setTextBoxValue("");
        }
        updateListBoxValues(view.getListBoxValue());
    }

    @Override
    public void textBoxValueChanged(String newValue) {
        if (newValue != null) {
            if (!quoteStringValues) {
                newValue = newValue.trim();
            }
            if (!newValue.isEmpty()) {
                String nonCustomValue = listBoxValues.getNonCustomValueForUserString(newValue);
                if (nonCustomValue != null) {
                    setListBoxValue(nonCustomValue);
                    setTextBoxValue("");
                    currentTextValue = "";
                } else {
                    String oldValue = currentTextValue;
                    String displayValue = addCustomValueToListBoxValues(newValue,
                                                                        oldValue);
                    setTextBoxValue(newValue);
                    currentTextValue = newValue;
                    setListBoxValue(displayValue);
                }
            } else {
                // Set the value even if it's ""
                setTextBoxValue(newValue);
                setListBoxValue(newValue);
                currentTextValue = newValue;
            }
        }
        view.setTextBoxVisible(false);
        view.setListBoxVisible(true);
    }

    @Override
    public String addCustomValueToListBoxValues(String newValue,
                                                String oldValue) {
        if (quoteStringValues) {
            newValue = StringUtils.createQuotedConstant(newValue);
            oldValue = StringUtils.createQuotedConstant(oldValue);
        }
        return listBoxValues.addCustomValue(newValue,
                                            oldValue);
    }

    public void setTextBoxValue(String value) {
        view.setTextBoxValue(value);
        view.setTextBoxModelValue(value);
    }

    public void setListBoxValue(String value) {
        view.setListBoxValue(value);
        view.setListBoxModelValue(value);
    }
}
