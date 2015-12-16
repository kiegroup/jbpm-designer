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

import org.jbpm.designer.client.resources.i18n.DesignerEditorConstants;
import org.jbpm.designer.client.shared.AssignmentData;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListBoxValuesTest {

    /**
     * General test for adding custom values to ProcessVar ListBoxValues
     */
    @Test
    public void testProcessVarListBoxValues() {
        List<String> processVarStartValues = Arrays.asList(
                "** Variable Definitions **", "employee",  "reason", "performance"
        );

        ListBoxValues processVarValues = new ListBoxValues("Constant ...", "Edit ", null);
        processVarValues.addValues(processVarStartValues);
        processVarValues.addCustomValue("\"abc\"", "");
        processVarValues.update("\"abc\"");
        processVarValues.update("reason");
        processVarValues.addCustomValue("\"ghi\"", "");
        processVarValues.update("\"ghi\"");
        processVarValues.addCustomValue("\"def\"", "\"ghi\"");
        processVarValues.update("\"def\"");
        processVarValues.update("reason");
        // Add Constant with same value as a ProcessVar
        processVarValues.addCustomValue("\"employee\"", "");
        processVarValues.update("\"employee\"");
        processVarValues.update("performance");
        processVarValues.addCustomValue("123", "");
        processVarValues.update("123");
        processVarValues.update("\"reason\"");
        processVarValues.addCustomValue("\"jkl\"", "\"reason\"");
        processVarValues.update("\"jkl\"");

        String[] acceptableValuesWithoutCustomValues = {
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        };

        String[] expectedAcceptableValuesWithCustomValues = {
                "",
                "\"jkl\"",
                "Edit \"jkl\" ...",
                "123",
                "\"employee\"",
                "\"def\"",
                "\"abc\"",
                "Constant ...",
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        };

        assertArrayEquals(acceptableValuesWithoutCustomValues, processVarValues.getAcceptableValuesWithoutCustomValues().toArray());
        assertArrayEquals(expectedAcceptableValuesWithCustomValues, processVarValues.getAcceptableValuesWithCustomValues().toArray());
    }

    /**
     * General test for adding custom values to DataTypes ListBoxValues
     */
    String sDataTypes1 = "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object, ******:******,UserCommand [org.jbpm.examples.cmd]:org.jbpm.examples.cmd.UserCommand,User [org.jbpm.examples.data]:org.jbpm.examples.data.User,Invoice [org.kie.test]:org.kie.test.Invoice,InvoiceLine [org.kie.test]:org.kie.test.InvoiceLine,PositionTest1 [org.kie.test]:org.kie.test.PositionTest1,PositionTest2 [org.kie.test]:org.kie.test.PositionTest2,PositionTest3 [org.kie.test]:org.kie.test.PositionTest3,PositionTest5 [org.kie.test]:org.kie.test.PositionTest5,SubComponent [org.kie.test]:org.kie.test.SubComponent,TestFormulas [org.kie.test]:org.kie.test.TestFormulas,TestPatterns [org.kie.test]:org.kie.test.TestPatterns,TestTypes [org.kie.test]:org.kie.test.TestTypes,TestTypesLine [org.kie.test]:org.kie.test.TestTypesLine";
    AssignmentData assignmentData1 = new AssignmentData(null, null, null, null, sDataTypes1, null);
    @Test
    public void testDataTypeListBoxValues() {

        ListBoxValues dataTypeValues = new ListBoxValues("Custom ...", "Edit ",
                new ListBoxValues.ValueTester() {
                    public String getNonCustomValueForUserString(String userValue) {
                        if (assignmentData1 != null) {
                            return assignmentData1.getDataTypeDisplayNameForUserString(userValue);
                        }
                        else {
                            return null;
                        }
                    }
                });

        dataTypeValues.addValues(assignmentData1.getDataTypeDisplayNames());
        dataTypeValues.addCustomValue("com.test.MyType", "");
        dataTypeValues.update("com.test.MyType");
        dataTypeValues.update("String");
        dataTypeValues.addCustomValue("com.test.YourType", "String");
        dataTypeValues.update("com.test.YourType");
        // Get known type for SimpleType entered by user
        String nonCustomValue = dataTypeValues.getNonCustomValueForUserString("InvoiceLine");
        dataTypeValues.update(nonCustomValue);
        dataTypeValues.addCustomValue("com.test.HisType", "");
        dataTypeValues.update("com.test.HisType");

        String[] acceptableValuesWithoutCustomValues = {
                "String",
                "Integer",
                "Boolean",
                "Float",
                "Object",
                "UserCommand [org.jbpm.examples.cmd]",
                "User [org.jbpm.examples.data]",
                "Invoice [org.kie.test]",
                "InvoiceLine [org.kie.test]",
                "PositionTest1 [org.kie.test]",
                "PositionTest2 [org.kie.test]",
                "PositionTest3 [org.kie.test]",
                "PositionTest5 [org.kie.test]",
                "SubComponent [org.kie.test]",
                "TestFormulas [org.kie.test]",
                "TestPatterns [org.kie.test]",
                "TestTypes [org.kie.test]",
                "TestTypesLine [org.kie.test]"

        };

        String[] expectedAcceptableValuesWithCustomValues = {
                "",
                "com.test.HisType",
                "Edit com.test.HisType ...",
                "com.test.YourType",
                "com.test.MyType",
                "Custom ...",
                "Integer",
                "Boolean",
                "Float",
                "Object",
                "UserCommand [org.jbpm.examples.cmd]",
                "User [org.jbpm.examples.data]",
                "Invoice [org.kie.test]",
                "InvoiceLine [org.kie.test]",
                "PositionTest1 [org.kie.test]",
                "PositionTest2 [org.kie.test]",
                "PositionTest3 [org.kie.test]",
                "PositionTest5 [org.kie.test]",
                "SubComponent [org.kie.test]",
                "TestFormulas [org.kie.test]",
                "TestPatterns [org.kie.test]",
                "TestTypes [org.kie.test]",
                "TestTypesLine [org.kie.test]"

        };

        assertArrayEquals(acceptableValuesWithoutCustomValues, dataTypeValues.getAcceptableValuesWithoutCustomValues().toArray());
        assertArrayEquals(expectedAcceptableValuesWithCustomValues, dataTypeValues.getAcceptableValuesWithCustomValues().toArray());
    }


}
