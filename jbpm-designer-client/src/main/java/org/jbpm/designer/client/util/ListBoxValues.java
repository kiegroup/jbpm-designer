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
import java.util.List;

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

    protected String customPrompt;
    protected String editPrefix;
    public static final String EDIT_SUFFIX = " ...";

    public interface ValueTester {
        String getNonCustomValueForUserString(String userValue);
    };

    ValueTester valueTester = null;

    public ListBoxValues(final String customPrompt, final String editPrefix, final ValueTester valueTester) {
        this.customPrompt = customPrompt;
        this.editPrefix = editPrefix;
        this.valueTester = valueTester;
    }

    public String getEditPrefix()
    {
        return editPrefix;
    }

    public void addValues(List<String> acceptableValues) {
        clear();
        acceptableValuesWithoutCustomValues.addAll(acceptableValues);

        acceptableValuesWithCustomValues.add("");
        acceptableValuesWithCustomValues.add(customPrompt);
        acceptableValuesWithCustomValues.addAll(acceptableValues);
    }

    public void addCustomValue(String newValue, String oldValue) {
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

    public List<String> update(String currentValue) {
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
        return acceptableValuesWithCustomValues;
   }

    public List<String> getAcceptableValuesWithCustomValues() {
        return acceptableValuesWithCustomValues;
    }
    public List<String> getAcceptableValuesWithoutCustomValues() {
        return acceptableValuesWithoutCustomValues;
    }

    public boolean isCustomValue(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        else {
            return customValues.contains(value);
        }
    }

    protected void clear() {
        customValues.clear();
        acceptableValuesWithCustomValues.clear();
        acceptableValuesWithoutCustomValues.clear();
    }

    protected String getEditValuePrompt(String editPrefix) {
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

    public String getNonCustomValueForUserString(String userValue) {
        if (valueTester != null) {
            return valueTester.getNonCustomValueForUserString(userValue);
        }
        else {
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
