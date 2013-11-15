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

package org.jbpm.designer.expressioneditor.marshalling;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonToken;
import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ExpressionEditorMessage;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ExpressionEditorMessageJSONUnmarshaller {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionEditorMessageJSONUnmarshaller.class);

    public ExpressionEditorMessageJSONUnmarshaller() {
    }

    public ExpressionEditorMessage unmarshall(String jsonMessage) throws Exception {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = jsonFactory.createJsonParser(jsonMessage);
        } catch (Exception e) {
            logger.error("It was not possible to create a json parser for the jsonMessage: " + jsonMessage, e);
            throw e;
        }
        return unmarshall(parser);
    }

    public ExpressionEditorMessage unmarshall(InputStream inputStream) throws Exception {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = null;
        try {
            parser = jsonFactory.createJsonParser(inputStream);
        } catch (Exception e) {
            logger.error("It was not possible to create the a json parser for the inputStream: " + inputStream, e);
            throw e;
        }
        return unmarshall(parser);
    }


    private ExpressionEditorMessage unmarshall(JsonParser parser) throws Exception {
        ExpressionEditorMessage message = new ExpressionEditorMessage();
        ConditionExpression expression = new ConditionExpression();

        try {
            String currentField;

            while (parser.nextToken() != JsonToken.END_OBJECT) {

                currentField = parser.getCurrentName();

                if (ExpressionEditorMessageTokens.OPERATOR_TOKEN.equals(currentField)) {
                    parser.nextToken();
                    expression.setOperator(parseText(parser));
                    message.setExpression(expression);
                } else if (ExpressionEditorMessageTokens.CONDITIONS_TOKEN.equals(currentField)) {
                    List<Condition> conditions = parseConditions(parser);
                    if (conditions.size() > 0) {
                        expression.setConditions(conditions);
                        message.setExpression(expression);
                    }
                } else if (ExpressionEditorMessageTokens.SCRIPT_TOKEN.equals(currentField)) {
                    parser.nextToken();
                    message.setScript(parseText(parser));
                } else if (ExpressionEditorMessageTokens.ERROR_CODE_TOKEN.equals(currentField)) {
                    parser.nextToken();
                    message.setErrorCode(parseText(parser));
                } else if (ExpressionEditorMessageTokens.ERROR_MESSAGE_TOKEN.equals(currentField)) {
                    parser.nextToken();
                    message.setErrorMessage(parseText(parser));
                }
            }
        } catch (IOException e) {
            logger.error("An error was produced during json message parsing. " + e);
            throw e;
        }
        return message;
    }

    private String parseText(JsonParser parser) throws IOException, JsonParseException {
        return parser.getText();
    }

    private List<Condition> parseConditions(JsonParser parser) throws IOException, JsonParseException {
        List<Condition> result = new ArrayList<Condition>();
        Condition condition;

        if (parser.nextToken() == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if ((condition = parseCondition(parser)) != null) {
                    result.add(condition);
                }
            }
        }
        return result;
    }

    private Condition parseCondition(JsonParser parser)  throws IOException, JsonParseException {
        Condition condition = null;
        if (parser.getCurrentToken() == JsonToken.START_OBJECT && parser.nextToken() != JsonToken.END_OBJECT) {
            condition = new Condition();
            if (ExpressionEditorMessageTokens.CONDITION_TOKEN.equals(parser.getCurrentName())) {
                parser.nextToken();
                condition.setFunction(parseText(parser));
            }
            if (parser.nextToken() != JsonToken.END_OBJECT && ExpressionEditorMessageTokens.PARAMETERS_TOKEN.equals(parser.getCurrentName())) {
                //parser the parameters
                if (parser.nextToken() == JsonToken.START_ARRAY) {
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        condition.addParam(parseText(parser));
                    }
                }
            }
        }
        return condition;
    }

}
