/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.expressioneditor;

import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.expressioneditor.parser.ExpressionParser;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class ExpressionParserTest {

    Logger logger = LoggerFactory.getLogger(ExpressionParserTest.class);

    @Test
    //@Ignore
    public void testOneScriptPerLine() throws Exception {

     /*
    return      between(a,  "o\"ne"   , "two" ) ;
    return     isNull(variable1)  ;
    return     greaterThan(variable2    , " the value " );
    return isNull(   a);
    return greaterOrEqualThan(v, "one", " two");
    */

        LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(ExpressionEditorMessageMarshallingTest.class.getResourceAsStream("OneScriptPerLine.txt") ));
        String line = null;

        List<ConditionExpression> expectedExpressions = new ArrayList<ConditionExpression>();

        ConditionExpression expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        Condition condition = new Condition("between");
        condition.addParam("a");
        condition.addParam("o\"ne");
        condition.addParam("two");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition("isNull");
        condition.addParam("variable1");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition("greaterThan");
        condition.addParam("variable2");
        condition.addParam(" the value ");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition("isNull");
        condition.addParam("a");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);

        expression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        condition = new Condition("greaterOrEqualThan");
        condition.addParam("v");
        condition.addParam("one");
        expression.getConditions().add(condition);
        expectedExpressions.add(expression);


        while ((line = lineReader.readLine()) != null) {

            logger.debug("line(" + lineReader.getLineNumber() + ") :" + line);

            ExpressionParser parser = new ExpressionParser(line);

            ConditionExpression conditionExpression = parser.parse();
            assertEqualsExpression(expectedExpressions.get(lineReader.getLineNumber()-1) , conditionExpression);
        }

    }

    String readFile(String fileName) throws IOException {

        BufferedReader reader = new LineNumberReader(new InputStreamReader(ExpressionEditorMessageMarshallingTest.class.getResourceAsStream(fileName) ));
        StringBuilder result = new StringBuilder();
        String line = null;
        while ((line =  reader.readLine()) != null) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }


    @Test
    public void testScript1() throws Exception {

        String script = readFile("Script1.txt");

        ConditionExpression expectedExpression = new ConditionExpression(ConditionExpression.AND_OPERATOR);
        Condition expectedCondition = new Condition("greaterOrEqualThan");
        expectedCondition.addParam("variable");
        expectedCondition.addParam("o\\náéö great! \"\n   áéíóúñÑ @|#~!·$%&/()=?¿");
        expectedExpression.getConditions().add(expectedCondition);

        ExpressionParser parser = new ExpressionParser(script);
        ConditionExpression actualExpression = parser.parse();

        System.out.println(actualExpression.getOperator());
        for (Condition condition : actualExpression.getConditions()) {
            System.out.println("condition: " + condition.getFunction());
            for (String param : condition.getParameters()) {
                System.out.println("\""+param+"\"");
            }
        }

        assertEqualsExpression(expectedExpression, actualExpression);
    }

    public void assertEqualsExpression(ConditionExpression expected, ConditionExpression actual) {
        assertNotNull(actual);
        assertEquals(expected.getOperator(), actual.getOperator());
        assertEquals(expected.getConditions().size(), actual.getConditions().size());
        for (int i = 0; i < expected.getConditions().size(); i++) {
            assertEqualsCondition(expected.getConditions().get(i), actual.getConditions().get(i));
        }
    }

    public void assertEqualsCondition(Condition expected, Condition actual) {
        assertNotNull(actual);
        assertEquals(expected.getFunction(), actual.getFunction());
        assertEquals(expected.getParameters().size(), actual.getParameters().size());
        for (int i = 0; i < expected.getParameters().size(); i++) {
            assertEquals(expected.getParameters().get(i), actual.getParameters().get(i));
        }
    }
}
