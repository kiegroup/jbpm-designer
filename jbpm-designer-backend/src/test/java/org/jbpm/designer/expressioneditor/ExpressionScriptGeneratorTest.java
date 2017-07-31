/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.expressioneditor;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.expressioneditor.parser.ExpressionScriptGenerator;
import org.jbpm.designer.expressioneditor.server.ExpressionEditorErrors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionScriptGeneratorTest {

    private ExpressionScriptGenerator generator = new ExpressionScriptGenerator();
    private ConditionExpression expression;
    private List<String> errors;
    private Condition condition;
    private List<String> parameters;

    @Before
    public void setUp() throws Exception {
        expression = new ConditionExpression();
        errors = new ArrayList<String>();
        expression.setOperator("AND");
        expression.setConditions(new ArrayList<Condition>());
        condition = new Condition();
        parameters = new ArrayList<String>();
    }

    @Test
    public void testEqual() throws Exception {
        condition.setFunction(Condition.EQUALS_TO);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.equalsTo(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testEqualEmptyValue() throws Exception {
        condition.setFunction(Condition.EQUALS_TO);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testContains() throws Exception {
        condition.setFunction(Condition.CONTAINS);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.contains(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testContainsEmptyValue() throws Exception {
        condition.setFunction(Condition.CONTAINS);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testIsNull() throws Exception {
        condition.setFunction(Condition.IS_NULL);
        parameters.add("variable");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.isNull(variable);",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        condition.setFunction(Condition.IS_EMPTY);
        parameters.add("variable");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.isEmpty(variable);",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testStartsWith() throws Exception {
        condition.setFunction(Condition.STARTS_WITH);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.startsWith(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testStartsWithEmptyValue() throws Exception {
        condition.setFunction(Condition.STARTS_WITH);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testEndsWith() throws Exception {
        condition.setFunction(Condition.ENDS_WITH);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.endsWith(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testEndsWithEmptyValue() throws Exception {
        condition.setFunction(Condition.ENDS_WITH);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testIsTrue() throws Exception {
        condition.setFunction(Condition.IS_TRUE);
        parameters.add("variable");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.isTrue(variable);",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testIsFalse() throws Exception {
        condition.setFunction(Condition.IS_FALSE);
        parameters.add("variable");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.isFalse(variable);",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testBetween() throws Exception {
        condition.setFunction(Condition.BETWEEN);
        parameters.add("variable");
        parameters.add("value");
        parameters.add("secondValue");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.between(variable, \"value\", \"secondValue\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testBetweenEmptyValue() throws Exception {
        condition.setFunction(Condition.BETWEEN);
        parameters.add("variable");
        parameters.add("");
        parameters.add("secondValue");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testBetweenEmptySecondValue() throws Exception {
        condition.setFunction(Condition.BETWEEN);
        parameters.add("variable");
        parameters.add("value");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testGreaterThan() throws Exception {
        condition.setFunction(Condition.GREATER_THAN);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.greaterThan(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testGreaterThanEmptyValue() throws Exception {
        condition.setFunction(Condition.GREATER_THAN);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testLessThan() throws Exception {
        condition.setFunction(Condition.LESS_THAN);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.lessThan(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testLessThanEmptyValue() throws Exception {
        condition.setFunction(Condition.LESS_THAN);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testGreaterOrEqualThan() throws Exception {
        condition.setFunction(Condition.GREATER_OR_EQUAL_THAN);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.greaterOrEqualThan(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testGreaterOrEqualThanEmptyValue() throws Exception {
        condition.setFunction(Condition.GREATER_OR_EQUAL_THAN);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testLessOrEqualThan() throws Exception {
        condition.setFunction(Condition.LESS_OR_EQUAL_THAN);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.lessOrEqualThan(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testLessOrEqualThanEmptyValue() throws Exception {
        condition.setFunction(Condition.LESS_OR_EQUAL_THAN);
        parameters.add("variable");
        parameters.add("");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals(ExpressionEditorErrors.PARAMETER_NULL_EMPTY,
                     errors.get(0));
    }

    @Test
    public void testAnd() throws Exception {
        condition.setFunction(Condition.GREATER_THAN);
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertEquals("return  KieFunctions.greaterThan(variable, \"value\") && KieFunctions.greaterThan(variable, \"value\");",
                     script);
        assertEquals(0,
                     errors.size());
    }

    @Test
    public void testInvalidFunction() throws Exception {
        condition.setFunction("invalidFunction");
        parameters.add("variable");
        parameters.add("value");
        condition.setParameters(parameters);
        expression.getConditions().add(condition);
        String script = generator.generateScript(expression,
                                                 errors);
        assertNull(script);
        assertEquals(1,
                     errors.size());
        assertEquals("Invalid function: invalidFunction",
                     errors.get(0));
    }
}
