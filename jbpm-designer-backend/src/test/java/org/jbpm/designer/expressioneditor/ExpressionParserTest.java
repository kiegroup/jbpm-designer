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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.expressioneditor.parser.ExpressionParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ExpressionParserTest {

    Logger logger = LoggerFactory.getLogger(ExpressionParserTest.class);

    private ConditionExpression expectedExpression;
    private Condition expectedCondition;

    @Before
    public void setUp() throws Exception {
        expectedExpression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        expectedCondition = new Condition();
        expectedCondition.addParam("variable");
        expectedCondition.addParam("value");
        expectedExpression.getConditions().add(expectedCondition);
    }

    @Test
    public void testOneScriptPerLine() throws Exception {

        LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(ExpressionEditorMessageMarshallingTest.class.getResourceAsStream("OneScriptPerLine.txt")));
        String line = null;

        List<ConditionExpression> expectedExpressions = new ArrayList<ConditionExpression>();

        ConditionExpression expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        Condition condition = new Condition(Condition.BETWEEN);
        condition.addParam("a");
        condition.addParam("o\"ne");
        condition.addParam("two");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition(Condition.IS_NULL);
        condition.addParam("variable1");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition(Condition.GREATER_THAN);
        condition.addParam("variable2");
        condition.addParam(" the value ");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition(Condition.IS_NULL);
        condition.addParam("a");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition(Condition.GREATER_OR_EQUAL_THAN);
        condition.addParam("v");
        condition.addParam("one");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        while ((line = lineReader.readLine()) != null) {

            logger.debug("line(" + lineReader.getLineNumber() + "): " + line);

            ExpressionParser parser = new ExpressionParser(line);

            ConditionExpression conditionExpression = parser.parse();
            assertEqualsExpression(expectedExpressions.get(lineReader.getLineNumber() - 1),
                                   conditionExpression);
        }
    }

    private String readFile(String fileName) throws IOException {

        BufferedReader reader = new LineNumberReader(new InputStreamReader(ExpressionEditorMessageMarshallingTest.class.getResourceAsStream(fileName)));
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

    @Test
    public void testScript1() throws Exception {

        String script = readFile("Script1.txt");

        ConditionExpression expectedExpression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        Condition expectedCondition = new Condition(Condition.GREATER_OR_EQUAL_THAN);
        expectedCondition.addParam("variable");
        expectedCondition.addParam("o\\náéö great! \"\n   áéíóúñÑ @|#~!·$%&/()=?¿");
        expectedExpression.getConditions().add(expectedCondition);

        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();

        logger.debug(actualExpression.getOperator());
        for (Condition condition : actualExpression.getConditions()) {
            logger.debug("condition: " + condition.getFunction());
            for (String param : condition.getParameters()) {
                logger.debug("\"" + param + "\"");
            }
        }

        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testBetween() throws Exception {
        String script = readFile("between.txt");
        expectedCondition.setFunction(Condition.BETWEEN);
        expectedCondition.addParam("secondValue");

        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();

        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testEqualsTo() throws Exception {
        String script = readFile("equalsTo.txt");
        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();
        expectedCondition.setFunction(Condition.EQUALS_TO);
        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testGreaterThan() throws Exception {
        String script = readFile("greaterThan.txt");
        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();
        expectedCondition.setFunction(Condition.GREATER_THAN);
        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testLessThan() throws Exception {
        String script = readFile("lessThan.txt");
        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();
        expectedCondition.setFunction(Condition.LESS_THAN);
        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testIsNull() throws Exception {
        String script = readFile("isNull.txt");
        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();
        expectedCondition.setFunction(Condition.IS_NULL);
        expectedCondition.getParameters().remove(1);
        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    @Test
    public void testMissingBracket() throws Exception {
        String script = readFile("missingBracket.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertEquals(e.getMessage(),
                         MessageFormat.format(ExpressionParser.FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR,
                                              "between"));
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    public void testMissingReturn() throws Exception {
        String script = readFile("missingReturn.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertEquals(e.getMessage(),
                         MessageFormat.format(ExpressionParser.RETURN_SENTENCE_EXPECTED_ERROR,
                                              "return"));
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    public void testMissingDelimiter() throws Exception {
        String script = readFile("missingDelimiter.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertEquals(e.getMessage(),
                         ExpressionParser.PARAMETER_DELIMITER_EXPECTED_ERROR);
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    public void testStringExpected() throws Exception {
        String script = readFile("stringExpected.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertEquals(e.getMessage(),
                         ExpressionParser.STRING_PARAMETER_EXPECTED_ERROR);
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    public void testUnknownFunction() throws Exception {
        String script = readFile("unknownFunction.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertTrue(e.getMessage().contains("must be followed"));
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    public void testMissingVariable() throws Exception {
        String script = readFile("missingVariable.txt");
        ExpressionParser parser = new ExpressionParser(script);
        boolean exceptionCaught = false;
        try {
            parser.parse();
        } catch (ParseException e) {
            assertEquals(e.getMessage(),
                         ExpressionParser.VARIABLE_NAME_EXPECTED_ERROR);
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    @Ignore
    public void testAnd() throws Exception {
        String script = readFile("and.txt");
        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();
        expectedCondition.setFunction(Condition.EQUALS_TO);
        expectedExpression.getConditions().add(expectedCondition);
        assertEqualsExpression(expectedExpression,
                               actualExpression);
    }

    public void assertEqualsExpression(ConditionExpression expected,
                                       ConditionExpression actual) {
        assertNotNull(actual);
        assertEquals(expected.getOperator(),
                     actual.getOperator());
        assertEquals(expected.getConditions().size(),
                     actual.getConditions().size());
        for (int i = 0; i < expected.getConditions().size(); i++) {
            assertEqualsCondition(expected.getConditions().get(i),
                                  actual.getConditions().get(i));
        }
    }

    public void assertEqualsCondition(Condition expected,
                                      Condition actual) {
        assertNotNull(actual);
        assertEquals(expected.getFunction(),
                     actual.getFunction());
        assertEquals(expected.getParameters().size(),
                     actual.getParameters().size());
        for (int i = 0; i < expected.getParameters().size(); i++) {
            assertEquals(expected.getParameters().get(i),
                         actual.getParameters().get(i));
        }
    }
}
