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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;

/**
 * ComboBox based on a ValueListBox<String> and a TextBox
 */
public class ComboBoxViewImpl implements ComboBoxView {

    protected ComboBoxView.ComboBoxPresenter presenter;
    protected ComboBoxView.ModelPresenter modelPresenter;
    protected ValueListBox<String> listBox;
    protected TextBox textBox;

    @Override
    public void init(final ComboBoxView.ComboBoxPresenter presenter,
            final ComboBoxView.ModelPresenter modelPresenter,
            final ValueListBox<String> listBox, final TextBox textBox,
            final String placeholder) {
        this.presenter = presenter;
        this.modelPresenter = modelPresenter;
        this.listBox = listBox;
        this.textBox = textBox;
        this.textBox.setPlaceholder(placeholder);

        textBox.setVisible(false);
        listBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> valueChangeEvent) {
                presenter.listBoxValueChanged(valueChangeEvent.getValue());
            }
        });

        listBox.addDomHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent focusEvent) {
                listBoxGotFocus();
            }
        }, FocusEvent.getType());

        textBox.addFocusHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent focusEvent) {
                textBoxGotFocus();
            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            @Override public void onBlur(BlurEvent blurEvent) {
                // Update ListBoxValues and set model values when textBox loses focus
                textBoxLostFocus();
            }
        });

    }

    @Override
    public String getModelValue() {
        return modelPresenter.getModelValue(listBox);
    }

    @Override
    public void setTextBoxModelValue(String value) {
        modelPresenter.setTextBoxModelValue(textBox, value);
    }

    @Override
    public void setListBoxModelValue(String value) {
        modelPresenter.setListBoxModelValue(listBox, value);
    }

    @Override
    public String getListBoxValue() {
        return listBox.getValue();
    }

    @Override
    public void setListBoxValue(String value) {
        listBox.setValue(value);
    }

    @Override
    public void setTextBoxValue(String value) {
        textBox.setValue(value);
    }

    @Override
    public void setTextBoxVisible(boolean visible) {
        textBox.setVisible(visible);
    }

    @Override
    public void setListBoxVisible(boolean visible) {
        listBox.setVisible(visible);
    }

    @Override
    public void setTextBoxFocus(boolean focus) {
        textBox.setFocus(focus);
    }

    @Override
    public void textBoxGotFocus() {
        presenter.setCurrentTextValue(textBox.getValue());
    }

    @Override
    public void textBoxLostFocus() {
        presenter.textBoxValueChanged(textBox.getValue());
    }

    @Override
    public void listBoxGotFocus() {
        presenter.updateListBoxValues(listBox.getValue());
    }

    @Override
    public void setAcceptableValues(List<String> acceptableValues) {
        listBox.setAcceptableValues(acceptableValues);
    }

    @Override
    public String getValue() {
        if (textBox.isVisible()) {
            return textBox.getValue();
        }
        else {
            return listBox.getValue();
        }
    }

}
