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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.server.JSONStreamDecoder;
import org.jbpm.designer.client.shared.util.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StringUtils.class})
public class AssignmentDataMarshallerTest extends AssignmentBaseTest {

    private AssignmentDataMarshaller marshaller;

    private List<AssignmentRow> inputs;
    private List<AssignmentRow> outputs;
    private List<String> dataTypes;
    private List<String> dataTypesDisplayNames;
    private Map<String, List<String>> mapCustomAssignmentProperties;

    @Mock
    private MarshallingSession session;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        marshaller = new AssignmentDataMarshaller();
        inputs = new ArrayList<AssignmentRow>();
        outputs = new ArrayList<AssignmentRow>();
        dataTypes = new ArrayList<String>();
        dataTypesDisplayNames = new ArrayList<String>();
        mapCustomAssignmentProperties = new HashMap<String, List<String>>();

        dataTypes.add("String");
        dataTypesDisplayNames.add("String");
        dataTypes.add("Integer");
        dataTypesDisplayNames.add("Integer");
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testEmpty() {
        marshallAndDemarshall();
    }

    @Test
    public void testInputs() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     "varName",
                                     null));
        marshallAndDemarshall();
    }

    @Test
    public void testInputsCustom() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     "varName",
                                     null));
        marshallAndDemarshall();
    }

    @Test
    public void testInputsOnlyTaskVar() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     null,
                                     null,
                                     null));
        marshallAndDemarshall();
    }

    @Test
    public void testInputsOnlyVars() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     null,
                                     "varName",
                                     null));
        marshallAndDemarshall();
    }

    @Test
    public void testOutputs() {
        outputs.add(new AssignmentRow("name",
                                      Variable.VariableType.OUTPUT,
                                      "String",
                                      null,
                                      "varName",
                                      null));
        marshallAndDemarshall();
    }

    @Test
    public void testOutputsCustom() {
        outputs.add(new AssignmentRow("name",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      "customStringType",
                                      "varName",
                                      null));
        marshallAndDemarshall();
    }

    @Test
    public void testOutputsOnlyTaskVar() {
        outputs.add(new AssignmentRow("name",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      null,
                                      null,
                                      null));
        marshallAndDemarshall();
    }

    @Test
    public void testOutputsOnlyVars() {
        outputs.add(new AssignmentRow("name",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      null,
                                      "varName",
                                      null));
        marshallAndDemarshall();
    }

    @Test
    public void testConstant() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "hello"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "value={\"true\"}"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "\"abcdef\""));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "\"abc\"def\"ghi\""));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "123"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     null,
                                     "123.456"));
        marshallAndDemarshall();
    }

    @Test
    public void testConstantCustom() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "hello"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "value={\"true\"}"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "\"abcdef"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "\"abc\"def\"ghi\""));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "123"));
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customStringType",
                                     null,
                                     "123.456"));
        marshallAndDemarshall();
    }

    @Test
    public void testMultipleAssignments() {
        inputs.add(new AssignmentRow("name",
                                     Variable.VariableType.INPUT,
                                     "String",
                                     null,
                                     "varName",
                                     null));
        inputs.add(new AssignmentRow("name2",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customString",
                                     "varName2",
                                     null));
        inputs.add(new AssignmentRow("name3",
                                     Variable.VariableType.INPUT,
                                     null,
                                     "customString",
                                     null,
                                     null));
        outputs.add(new AssignmentRow("name4",
                                      Variable.VariableType.INPUT,
                                      "Integer",
                                      null,
                                      null,
                                      null));
        outputs.add(new AssignmentRow("onlyname",
                                      Variable.VariableType.INPUT,
                                      null,
                                      null,
                                      null,
                                      null));
        outputs.add(new AssignmentRow("name",
                                      Variable.VariableType.OUTPUT,
                                      "String",
                                      null,
                                      "varName",
                                      null));
        outputs.add(new AssignmentRow("name2",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      "customString",
                                      "varName2",
                                      null));
        outputs.add(new AssignmentRow("name3",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      "customString",
                                      null,
                                      null));
        outputs.add(new AssignmentRow("name4",
                                      Variable.VariableType.OUTPUT,
                                      "Integer",
                                      null,
                                      null,
                                      null));
        outputs.add(new AssignmentRow("onlyname",
                                      Variable.VariableType.OUTPUT,
                                      null,
                                      null,
                                      null,
                                      null));
        marshallAndDemarshall();
    }

    @Test
    public void testCustomAssignmentProperties() {
        // "FaultToUri:Henry;Rod;Tony;,TruckType:Mazda;Tonka;Mercedes;,FromUri:,ReplyToUri:Jane;,"
        mapCustomAssignmentProperties.put("FaultToUri",
                                          Arrays.asList(new String[]{"Henry", "Rod", "Tony"}));
        mapCustomAssignmentProperties.put("TruckType",
                                          Arrays.asList(new String[]{"Mazda", "Tonka", "Mercedes"}));
        mapCustomAssignmentProperties.put("FromUri",
                                          Arrays.asList(new String[]{}));
        mapCustomAssignmentProperties.put("ReplyToUri",
                                          Arrays.asList(new String[]{"Jane"}));

        AssignmentData original = new AssignmentData();
        original.setCustomAssignmentProperties(mapCustomAssignmentProperties);
        String json = marshaller.marshall(original,
                                          session);
        EJValue jsonObject = new JSONStreamDecoder(new ByteArrayInputStream(json.getBytes())).parse();
        AssignmentData demarshalled = marshaller.demarshall(jsonObject,
                                                            session);
        assertEquals(original,
                     demarshalled);
    }

    private void marshallAndDemarshall(Map<String, List<String>> mapCustomAssignmentProperties) {
        AssignmentData original = new AssignmentData(inputs,
                                                     outputs,
                                                     dataTypes,
                                                     dataTypesDisplayNames);
        if (mapCustomAssignmentProperties != null) {
            original.setCustomAssignmentProperties(mapCustomAssignmentProperties);
        }
        String json = marshaller.marshall(original,
                                          session);
        EJValue jsonObject = new JSONStreamDecoder(new ByteArrayInputStream(json.getBytes())).parse();
        AssignmentData demarshalled = marshaller.demarshall(jsonObject,
                                                            session);
        assertEquals(original,
                     demarshalled);
    }

    private void marshallAndDemarshall() {
        marshallAndDemarshall(null);
    }
}
