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

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jbpm.designer.client.shared.util.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The tests in this class run ComboBox, ComboBoxView and ListBoxValues
 * through some realistic user interactions.
 */
@RunWith(GwtMockitoTestRunner.class)
public class ComboBoxAllTest {

    @Mock
    ComboBoxView.ModelPresenter modelPresenter;

    ComboBoxView view = new ComboBoxViewImpl();

    @Mock
    ValueListBox<String> listBox;
    String listBoxValue = "";
    boolean listBoxIsVisible = true;

    @Mock
    TextBox textBox;
    String textBoxValue = "";
    boolean textBoxIsVisible = false;

    @Spy
    ComboBox comboBox = new ComboBox();

    boolean quoteStringValues;

    String editPrefix;

    String editSuffix;

    String customPrompt;

    ListBoxValues getListBoxValues() {
        return comboBox.getListBoxValues();
    }

    private final boolean processVarQuoteStringValues = true;
    private final String processVarConstantPrompt = "Constant ...";
    private final String processVarConstantPlaceholder = "Enter constant ...";
    private final String processVarEditPrefix = "Edit ";
    private final String processVarEditSuffix = " ...";
    List<String> processVarListBoxStartValues = Arrays.asList(
            "** Variable Definitions **",
            "employee",
            "reason",
            "performance"
    );

    private final boolean dataTypeQuoteStringValues = false;
    private final String dataTypeCustomPrompt = "Custom ...";
    private final String dataTypeCustomPlaceholder = "Enter type ...";
    private final String dataTypeEditPrefix = "Edit ";
    private final String dataTypeEditSuffix = " ...";
    List<String> dataTypeListBoxStartValues = Arrays.asList(
            "String",
            "Integer",
            "Boolean",
            "Float",
            "Object",
            "Department [org.test.Department]",
            "Employee [org.test.Employee]",
            "Organization [org.test.Organization]"
    );

    private void initComboBoxTest(boolean quoteStringValues,
                                  String customPrompt,
                                  String placeholder,
                                  String editPrefix,
                                  String editSuffix,
                                  List<String> listBoxStartValues) {
        initPresenter();
        initListBox();
        initTextBox();

        ListBoxValues listBoxValues = new ListBoxValues(customPrompt,
                                                        editPrefix,
                                                        null);
        listBoxValues.addValues(listBoxStartValues);

        comboBox.view = view;
        comboBox.init(modelPresenter,
                      listBox,
                      textBox,
                      quoteStringValues,
                      customPrompt,
                      placeholder);
        comboBox.setListBoxValues(listBoxValues);
        comboBox.setShowCustomValues(true);

        this.quoteStringValues = quoteStringValues;
        this.editPrefix = editPrefix;
        this.editSuffix = editSuffix;
        this.customPrompt = customPrompt;
    }

    private void initPresenter() {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                textBox.setValue(value);
                return null;
            }
        }).when(modelPresenter).setTextBoxModelValue(any(TextBox.class),
                                                     anyString());

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String value = (String) invocation.getArguments()[1];
                listBox.setValue(value);
                return null;
            }
        }).when(modelPresenter).setListBoxModelValue(any(ValueListBox.class),
                                                     anyString());

        when(modelPresenter.getModelValue(any(ValueListBox.class))).thenAnswer(new Answer<Object>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return listBoxValue;
            }
        });
    }

    private void initListBox() {
        when(listBox.getValue()).thenAnswer(new Answer<Object>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return listBoxValue;
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listBoxValue = (String) invocation.getArguments()[0];
                return null;
            }
        }).when(listBox).setValue(anyString());
        when(listBox.isVisible()).thenAnswer(new Answer<Object>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return listBoxIsVisible;
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listBoxIsVisible = (Boolean) invocation.getArguments()[0];
                return null;
            }
        }).when(listBox).setVisible(anyBoolean());
    }

    private void initTextBox() {
        when(textBox.getValue()).thenAnswer(new Answer<Object>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return textBoxValue;
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                textBoxValue = (String) invocation.getArguments()[0];
                return null;
            }
        }).when(textBox).setValue(anyString());
        when(textBox.isVisible()).thenAnswer(new Answer<Object>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return textBoxIsVisible;
            }
        });
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                textBoxIsVisible = (Boolean) invocation.getArguments()[0];
                return null;
            }
        }).when(textBox).setVisible(anyBoolean());
    }

    @Test
    public void testProcessVarComboBox() {
        initComboBoxTest(processVarQuoteStringValues,
                         processVarConstantPrompt,
                         processVarConstantPlaceholder,
                         processVarEditPrefix,
                         processVarEditSuffix,
                         processVarListBoxStartValues);

        setNonCustomValue(processVarListBoxStartValues.get(2),
                          1);

        String customStringValue1 = "first constant";
        setCustomValue(customStringValue1);

        setNonCustomValue(processVarListBoxStartValues.get(2),
                          2);

        String customNumericValue1 = "123";
        setCustomValue(customNumericValue1);

        setNonCustomValue(StringUtils.createQuotedConstant(customStringValue1),
                          2);

        String customNumericValue2 = "123.456";
        setCustomValue(customNumericValue2);

        aboutToEditCustomValue(customNumericValue2,
                               2);

        String customNumericValue3 = "100";
        editCustomValue(customNumericValue3);

        String customStringValue2 = "second constant";
        setCustomValue(customStringValue2);

        setNonCustomValue(StringUtils.createQuotedConstant(customStringValue1),
                          3);

        setNonCustomValue(processVarListBoxStartValues.get(2),
                          3);

        assertTrue(getListBoxValues().getAcceptableValuesWithCustomValues().
                contains(StringUtils.createQuotedConstant(customStringValue1)));
        assertTrue(getListBoxValues().getAcceptableValuesWithCustomValues().
                contains(customNumericValue1));
        assertTrue(!getListBoxValues().getAcceptableValuesWithCustomValues().
                contains(customNumericValue2));
        assertTrue(getListBoxValues().getAcceptableValuesWithCustomValues().
                contains(customNumericValue3));

        assertTrue(getListBoxValues().getAcceptableValuesWithCustomValues().
                contains(StringUtils.createQuotedConstant(customStringValue2)));

//        System.out.println(comboBox.getListBoxValues().toString());
    }

    @Test
    public void testDataTypeComboBox() {
        initComboBoxTest(dataTypeQuoteStringValues,
                         dataTypeCustomPrompt,
                         dataTypeCustomPlaceholder,
                         dataTypeEditPrefix,
                         dataTypeEditSuffix,
                         dataTypeListBoxStartValues);

        setNonCustomValue(dataTypeListBoxStartValues.get(2),
                          1);

        String customDataType1 = "com.acme.parts.Nut";
        setCustomValue(customDataType1);

        setNonCustomValue(dataTypeListBoxStartValues.get(6),
                          1);

        setNonCustomValue(customDataType1,
                          2);

        setNonCustomValue(dataTypeListBoxStartValues.get(5),
                          1);

        String customDataType2 = "com.acme.parts.Bolt";
        setCustomValue(customDataType2);

        setNonCustomValue(customDataType1,
                          3);

//        System.out.println(comboBox.getListBoxValues().toString());
    }

    private void setCustomValue(String value) {
        comboBox.view.listBoxGotFocus();
        comboBox.listBoxValueChanged(this.customPrompt);
        assertEquals(listBox.isVisible(),
                     false);
        assertEquals(textBox.isVisible(),
                     true);

        comboBox.view.textBoxGotFocus();
        textBox.setValue(value);
        comboBox.view.textBoxLostFocus();
        comboBox.view.listBoxGotFocus();
        assertEquals(listBox.isVisible(),
                     true);
        assertEquals(textBox.isVisible(),
                     false);

        String listBoxValue = this.quoteStringValues ? StringUtils.createQuotedConstant(value) : value;
        verify(modelPresenter).setTextBoxModelValue(textBox,
                                                    value);
        assertEquals(comboBox.getValue(),
                     listBoxValue);
    }

    private void setNonCustomValue(String value,
                                   int times) {
        comboBox.view.listBoxGotFocus();
        comboBox.listBoxValueChanged(value);
        assertEquals(listBox.isVisible(),
                     true);
        assertEquals(textBox.isVisible(),
                     false);

        verify(modelPresenter,
               times(times)).setListBoxModelValue(listBox,
                                                  value);
        assertEquals(comboBox.getValue(),
                     value);
    }

    private void aboutToEditCustomValue(String value,
                                        int times) {
        comboBox.view.listBoxGotFocus();
        comboBox.listBoxValueChanged(this.editPrefix + value + this.editSuffix);
        assertEquals(listBox.isVisible(),
                     false);
        assertEquals(textBox.isVisible(),
                     true);

        verify(modelPresenter,
               times(times)).setTextBoxModelValue(textBox,
                                                  value);
        assertEquals(textBox.getValue(),
                     value);
    }

    private void editCustomValue(String value) {

        comboBox.view.textBoxGotFocus();
        textBox.setValue(value);
        comboBox.view.textBoxLostFocus();
        comboBox.view.listBoxGotFocus();
        assertEquals(listBox.isVisible(),
                     true);
        assertEquals(textBox.isVisible(),
                     false);

        String listBoxValue = this.quoteStringValues ? StringUtils.createQuotedConstant(value) : value;
        verify(modelPresenter).setTextBoxModelValue(textBox,
                                                    listBoxValue);
        assertEquals(comboBox.getValue(),
                     listBoxValue);
    }
}
