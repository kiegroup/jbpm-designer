/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.designer.client.shared;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.google.gwt.junit.GWTMockUtilities;
import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.util.StringUtils;
import org.mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

public class AssignmentBaseTest {

    public void setUp() throws Exception {
        // Prevent runtime GWT.create() error at DesignerEditorConstants.INSTANCE
        GWTMockUtilities.disarm();
        // MockDesignerEditorConstants replaces DesignerEditorConstants.INSTANCE
        setFinalStaticField(DesignerEditorConstants.class.getDeclaredField("INSTANCE"), new MockDesignerEditorConstants());

        // Mock StringUtils URL Encoding methods
        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.urlEncode(Mockito.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return urlEncode((String) args[0]);
            }
        });
        PowerMockito.when(StringUtils.urlDecode(Mockito.anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return urlDecode((String) args[0]);
            }
        });

    }

    public void tearDown() {
        GWTMockUtilities.restore();
    }

    /**
     * Implementation of urlEncode for PowerMocked StringUtils
     *
     * @param s
     * @return
     */
    public String urlEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    /**
     * Implementation of urlDecode for PowerMocked StringUtils
     *
     * @param s
     * @return
     */
    public String urlDecode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return s;
        }
    }


    private void setFinalStaticField(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private class MockDesignerEditorConstants implements DesignerEditorConstants {
        public String Add() {return "Add";}
        public String businessProcess() {return "Business Process";}
        public String businessProcessResourceTypeDescription() {return "Business Process";}
        public String Cancel() {return "Cancel";}
        public String Constant() {return "Constant";}
        public String Custom() {return "Custom";}
        public String Data_Input() {return "data input";}
        public String Data_Inputs() {return "data inputs";}
        public String Data_Input_and_Assignment() {return "Data Input and Assignment";}
        public String Data_Inputs_and_Assignments() {return "Data Inputs and Assignments";}
        public String Data_Output() {return "data output";}
        public String Data_Outputs() {return "data outputs";}
        public String Data_Output_and_Assignment() {return "Data Output and Assignment";}
        public String Data_Outputs_and_Assignments() {return "Data Outputs and Assignments";}
        public String Data_IO() {return "Data I/O";}
        public String Edit() {return "Edit";}
        public String Enter_constant() {return "Enter constant";}
        public String Enter_type() {return "Enter type";}
        public String Invalid_character_in_name() {return "Invalid character in name";}
        public String No_Data_Input() {return "no data input";}
        public String No_Data_Output() {return "no data output";}
        public String Only_single_entry_allowed() {return "Only single entry allowed";}
        public String Save() {return "Save";}
        public String Source() {return "Source";}
        public String Target() {return "Target";}
        public String This_input_should_be_entered_as_a_property_for_the_task() {return "This Input should be entered as a property for the task";}
        public String Removed_invalid_characters_from_name() {return "Removed invalid characters from name";}
        public String A_Data_Input_with_this_name_already_exists() {return "A Data Input with this name already exists";}
        public String ProcessModel() {return "Process Model";}
    }

}
