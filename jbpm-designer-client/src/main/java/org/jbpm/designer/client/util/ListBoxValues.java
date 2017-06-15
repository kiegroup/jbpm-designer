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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.designer.client.shared.util.StringUtils;

/**
 * Class containing a list of values for a ValueListBox<String>.
 * This is used by the ListBoxes in the DataIOEditor to keep their drop-down lists
 * up to date with updated with new values (CustomDataTypes / Constants) as
 * the user adds them.
 */
public class ListBoxValues {

    protected List<String> acceptableValuesWithCustomValues = new ArrayList<String>();
    protected List<String> acceptableValuesWithoutCustomValues = new ArrayList<String>();
    protected List<String> customValues = new ArrayList<String>();

    protected Map<String, String> mapDisplayValuesToValues = new HashMap<String, String>();

    protected String customPrompt;
    protected String editPrefix;
    public static final String EDIT_SUFFIX = " ...";
    protected int maxDisplayLength;

    protected static final int DEFAULT_MAX_DISPLAY_LENGTH = -1;

    public interface ValueTester {

        String getNonCustomValueForUserString(final String userValue);
    }

    ValueTester valueTester = null;

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester,
                         final int maxDisplayLength) {
        this.customPrompt = customPrompt;
        this.editPrefix = editPrefix;
        this.valueTester = valueTester;
        this.maxDisplayLength = maxDisplayLength;
    }

    public ListBoxValues(final String customPrompt,
                         final String editPrefix,
                         final ValueTester valueTester) {
        this.customPrompt = customPrompt;
        this.editPrefix = editPrefix;
        this.valueTester = valueTester;
        this.maxDisplayLength = DEFAULT_MAX_DISPLAY_LENGTH;
    }

    public ListBoxValues(final ListBoxValues copy,
                         final boolean copyCustomValues) {
        this.customPrompt = copy.customPrompt;
        this.editPrefix = copy.editPrefix;
        this.valueTester = copy.valueTester;
        this.maxDisplayLength = copy.maxDisplayLength;
        this.addValues(copy.acceptableValuesWithoutCustomValues);
        if(copy.customValues != null) {
            for (String copyCustomValue : copy.customValues) {
                this.addCustomValue(copyCustomValue,
                                    null);
            }
        }
    }

    public String getEditPrefix() {
        return editPrefix;
    }

    public void addValues(final List<String> acceptableValues) {
        clear();

        if (acceptableValues != null) {
            List<String> displayValues = createDisplayValues(acceptableValues);

            acceptableValuesWithoutCustomValues.addAll(displayValues);

            acceptableValuesWithCustomValues.add("");
            acceptableValuesWithCustomValues.add(customPrompt);
            acceptableValuesWithCustomValues.addAll(displayValues);
        }
    }

    public String addCustomValue(final String newValue,
                                 final String oldValue) {
        if (oldValue != null && !oldValue.isEmpty()) {
            if (acceptableValuesWithCustomValues.contains(oldValue)) {
                acceptableValuesWithCustomValues.remove(oldValue);
            }
            if (customValues.contains(oldValue)) {
                customValues.remove(oldValue);
            }
            // Do not remove from mapDisplayValuesToValues
        }

        if (newValue != null && !newValue.isEmpty()) {
            String newDisplayValue = addDisplayValue(newValue);
            if (!acceptableValuesWithCustomValues.contains(newDisplayValue)) {
                int index = 1;
                if (acceptableValuesWithCustomValues.size() < 1) {
                    index = acceptableValuesWithCustomValues.size();
                }
                acceptableValuesWithCustomValues.add(index,
                                                     newDisplayValue);
            }
            if (!customValues.contains(newDisplayValue)) {
                customValues.add(newDisplayValue);
            }
            return newDisplayValue;
        } else {
            return newValue;
        }
    }

    public List<String> update(final String currentValue) {
        String currentEditValuePrompt = getEditValuePrompt(editPrefix);
        String newEditValuePrompt = editPrefix + currentValue + EDIT_SUFFIX;
        if (isCustomValue(currentValue)) {
            if (newEditValuePrompt.equals(currentEditValuePrompt)) {
                return acceptableValuesWithCustomValues;
            }
            if (currentEditValuePrompt != null) {
                acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
            }
            int editPromptIndex = acceptableValuesWithCustomValues.indexOf(currentValue);
            if (editPromptIndex > -1) {
                editPromptIndex++;
            } else if (acceptableValuesWithCustomValues.size() > 1) {
                editPromptIndex = 2;
            } else {
                editPromptIndex = acceptableValuesWithCustomValues.size();
            }
            acceptableValuesWithCustomValues.add(editPromptIndex,
                                                 newEditValuePrompt);
        } else if (currentEditValuePrompt != null) {
            acceptableValuesWithCustomValues.remove(currentEditValuePrompt);
        }
        return acceptableValuesWithCustomValues;
    }

    public List<String> getAcceptableValuesWithCustomValues() {
        return acceptableValuesWithCustomValues;
    }

    public List<String> getAcceptableValuesWithoutCustomValues() {
        return acceptableValuesWithoutCustomValues;
    }

    public boolean isCustomValue(final String value) {
        if (value == null || value.isEmpty()) {
            return false;
        } else {
            return customValues.contains(value);
        }
    }

    protected void clear() {
        customValues.clear();
        acceptableValuesWithCustomValues.clear();
        acceptableValuesWithoutCustomValues.clear();
        mapDisplayValuesToValues.clear();
    }

    protected String getEditValuePrompt(final String editPrefix) {
        if (acceptableValuesWithCustomValues.size() > 0) {
            for (int i = 0; i < acceptableValuesWithCustomValues.size(); i++) {
                String value = acceptableValuesWithCustomValues.get(i);
                if (value.startsWith(editPrefix)) {
                    return value;
                }
            }
        }
        return null;
    }

    protected List<String> createDisplayValues(final List<String> acceptableValues) {
        List<String> displayValues = new ArrayList<String>();
        for (String value : acceptableValues) {
            if (value != null) {
                displayValues.add(addDisplayValue(value));
            }
        }
        return displayValues;
    }

    /**
     * Function for handling values which are longer than MAX_DISPLAY_LENGTH such as very long string constants.
     * <p>
     * Creates display value for a value and adds it to the mapDisplayValuesToValues map.
     * If display value already present in mapDisplayValuesToValues, returns it.
     * <p>
     * The first display value for values which are the same is of the form "\"abcdeabcde...\"" and subsequent display values
     * are of the form "\"abcdeabcde...(01)\""
     * @param value the value
     * @return the displayValue for value
     */
    protected String addDisplayValue(final String value) {
        if (mapDisplayValuesToValues.containsValue(value)) {
            for (Map.Entry<String, String> entry : mapDisplayValuesToValues.entrySet()) {
                if (value.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
        }

        String displayValue = value;
        // Create special displayValue only for quoted constants longer than maxDisplayLength
        if (maxDisplayLength > 0 && value != null && StringUtils.isQuotedConstant(value) && value.length() > maxDisplayLength + 2) {
            String displayValueStart = value.substring(0,
                                                       maxDisplayLength + 1);
            int nextIndex = 0;
            for (String existingDisplayValue : mapDisplayValuesToValues.keySet()) {
                if (existingDisplayValue.startsWith(displayValueStart)) {
                    // Is it like "\"abcdeabcde...(01)\""
                    if (existingDisplayValue.length() == (maxDisplayLength + 9)) {
                        String sExistingIndex = existingDisplayValue.substring(existingDisplayValue.length() - 4,
                                                                               existingDisplayValue.length() - 2);
                        try {
                            int existingIndex = Integer.parseInt(sExistingIndex);
                            if (nextIndex <= existingIndex) {
                                nextIndex = existingIndex + 1;
                            }
                        } catch (NumberFormatException nfe) {
                            // do nothing
                        }
                    } else {
                        if (nextIndex == 0) {
                            nextIndex++;
                        }
                    }
                }
            }
            if (nextIndex == 0) {
                displayValue = displayValueStart + "..." + "\"";
            } else {
                String sNextIndex = Integer.toString(nextIndex);
                if (nextIndex < 10) {
                    sNextIndex = "0" + sNextIndex;
                }
                displayValue = displayValueStart + "...(" + sNextIndex + ")\"";
            }
        }
        mapDisplayValuesToValues.put(displayValue,
                                     value);

        return displayValue;
    }

    /**
     * Returns real unquoted value for a DisplayValue
     * @param key
     * @return
     */
    public String getValueForDisplayValue(final String key) {
        if (mapDisplayValuesToValues.containsKey(key)) {
            return mapDisplayValuesToValues.get(key);
        }
        return key;
    }

    public String getNonCustomValueForUserString(final String userValue) {
        if (valueTester != null) {
            return valueTester.getNonCustomValueForUserString(userValue);
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("acceptableValuesWithoutCustomValues:\n");
        for (String value : acceptableValuesWithoutCustomValues) {
            sb.append('\t').append(value).append(",\n");
        }
        sb.append('\n');
        sb.append("acceptableValuesWithCustomValues:\n");
        for (String value : acceptableValuesWithCustomValues) {
            sb.append('\t').append(value).append(",\n");
        }
        return sb.toString();
    }
}