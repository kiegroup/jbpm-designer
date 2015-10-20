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
package org.jbpm.designer.client.util;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.uberfire.workbench.events.NotificationEvent;

public class ValidatingTextBox extends TextBox {

    Set<String> invalidValues;
    boolean isCaseSensitive = false;
    String invalidValueErrorMessage;

    @Inject
    private Event<NotificationEvent> notification;

    public ValidatingTextBox() {
        super();
        setup();
    }

    protected void setup() {
        final TextBox me = this;

        //Add validation when loses focus (for when values are pasted in by user)
        this.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                String value = me.getText();
                String validValue = "";
                if (value != null) {
                    validValue = value.trim();
                }
                String validationError = isValidValue(validValue);
                if (validationError != null) {
                    notification.fire(new NotificationEvent(validationError, NotificationEvent.NotificationType.ERROR));
                    validValue = makeValidValue(value);
                    me.setValue(validValue);
                 }
                else if (! validValue.equals(value)){
                    me.setValue(validValue);
                }
            }

        });
    }

    /**
     * Sets the invalid values for the TextBox
     *
     * @param invalidValues
     * @param isCaseSensitive
     * @param invalidValueErrorMessage
     */
    public void setInvalidValues(Set<String> invalidValues, boolean isCaseSensitive, String invalidValueErrorMessage) {
        if (isCaseSensitive) {
            this.invalidValues = invalidValues;
        }
        else {
            this.invalidValues = new HashSet<String>();
            for (String value : invalidValues) {
                this.invalidValues.add(value.toLowerCase());
            }
        }
        this.isCaseSensitive = isCaseSensitive;
        this.invalidValueErrorMessage = invalidValueErrorMessage;
    }

    /**
     * Tests whether a value is valid
     *
     * @param value
     * @return an error message to be reported
     */
    public String isValidValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (!isCaseSensitive) {
            value = value.toLowerCase();
        }
        if (invalidValues != null && invalidValues.contains(value)) {
            return invalidValueErrorMessage;
        }
        else {
            return null;
        }
    }

    /**
     * If validation fails (e.g. as a result of a user pasting a value) when the
     * TextBox looses focus this method is called to transform the current value
     * into one which is valid.
     * @param value Current value
     * @return A valid value
     */
    protected String makeValidValue(String value) {
        return "";
    }

}
