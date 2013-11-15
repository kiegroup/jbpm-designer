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
package org.jbpm.designer.expressioneditor.server;

import org.jbpm.designer.expressioneditor.marshalling.ExpressionEditorMessageJSONMarshaller;
import org.jbpm.designer.expressioneditor.marshalling.ExpressionEditorMessageJSONUnmarshaller;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.expressioneditor.model.ExpressionEditorMessage;
import org.jbpm.designer.expressioneditor.parser.ExpressionParser;
import org.jbpm.designer.expressioneditor.parser.ExpressionScriptGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionEditorProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionEditorProcessor.class);

    private static final String PARSE_COMMAND = "parseScript";

    private static final String GENERATE_COMMAND = "generateScript";

    public static final String MESSAGE_PARAM = "expression_editor_message";

    public static final String COMMAND_PARAM = "expression_editor_command";

    public ExpressionEditorProcessor() {
    }

    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        try {

            ExpressionEditorMessageJSONMarshaller marshaller = new ExpressionEditorMessageJSONMarshaller();
            ExpressionEditorMessageJSONUnmarshaller unmarshaller = new ExpressionEditorMessageJSONUnmarshaller();
            ExpressionEditorMessage requestMessage = null;
            ExpressionEditorMessage responseMessage = null;

            PrintWriter out = res.getWriter();

            String command = req.getParameter(COMMAND_PARAM);
            String message = req.getParameter(MESSAGE_PARAM);

            if (logger.isDebugEnabled()) {
                logger.debug("Processing request for parameters, command: " + command + ", message: " + message);
            }

            if (!isValidCommand(command)) {
                logger.error("Invalid command: " + command + " was sent to the ExpressionsEditorProcessor, " +
                        "request will be discarded.");
                return;
            }

            try {
                requestMessage = unmarshaller.unmarshall(message);
            } catch (Exception e) {
                logger.error("It was not possible to unmarshall json message, request will be discarded. message: " + message, e);
                return;
            }

            if (GENERATE_COMMAND.equals(command)) {
                responseMessage = doGenerateScript(requestMessage);
            } else if (PARSE_COMMAND.equals(command)) {
                responseMessage = doParseScript(requestMessage);
            }

            if (responseMessage != null) {
                try {
                    String jsonResponse = marshaller.marshall(responseMessage);
                    if (logger.isDebugEnabled()) {
                        logger.debug("sending response message: " + jsonResponse);
                    }
                    out.write(jsonResponse);
                } catch (Exception e) {
                    //unexpected error.
                    logger.error("It was not possible to marshal the responseMessage: " + responseMessage, e);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error during request processing.", e);
        }
    }

    private ExpressionEditorMessage doParseScript(ExpressionEditorMessage requestMessage) {

        //TODO add more fine grained controls.
        String script = requestMessage.getScript();
        ConditionExpression conditionExpression = null;

        if (logger.isDebugEnabled()) logger.debug("parsing script: " + script);

        try {
            ExpressionParser parser = new ExpressionParser(script);
            conditionExpression = parser.parse();
            requestMessage.setExpression(conditionExpression);
            requestMessage.setErrorCode(null);
            requestMessage.setErrorMessage(null);
        } catch (ParseException e) {
            logger.error("Script sent to server couldn't be parsed: " + script + " due to the following error: " + e.getMessage(), e);
            requestMessage.setErrorCode(ExpressionEditorErrors.SCRIPT_PARSING_ERROR);
            requestMessage.setErrorMessage(e.getMessage());
            requestMessage.setExpression(new ConditionExpression());
        }
        return requestMessage;
    }

    private ExpressionEditorMessage doGenerateScript(ExpressionEditorMessage requestMessage) {
        ExpressionEditorMessage responseMessage = new ExpressionEditorMessage();
        List<String> errors = new ArrayList<String>();
        ExpressionScriptGenerator generator = new ExpressionScriptGenerator();

        if (isValidMessageForCommand(GENERATE_COMMAND, requestMessage)) {
            ConditionExpression expression = requestMessage.getExpression();
            String script = generator.generateScript(expression, errors);

            if (script == null) {
                //process the errors.
                requestMessage.setErrorCode(ExpressionEditorErrors.SCRIPT_GENERATION_ERROR);
                responseMessage.setErrorMessage(concat(errors));
            }
            responseMessage.setScript(script);

        } else {
            responseMessage.setErrorCode(ExpressionEditorErrors.INVALID_MESSAGE_ERROR);
        }
        return responseMessage;
    }

    private boolean isValidMessageForCommand(String command, ExpressionEditorMessage message) {
        if (GENERATE_COMMAND.equals(command)) {
            if (message.getExpression() == null) {
                logger.error("No expression is present in message: " + message);
                return false;
            }
        }

        return true;
    }

    private boolean isValidCommand(String command) {
        return PARSE_COMMAND.equals(command) || GENERATE_COMMAND.equals(command);
    }

    private String concat(List<String> values) {
        StringBuilder result = new StringBuilder();
        if (values == null || values.size() == 0) return result.toString();
        boolean first = true;
        for (String value : values) {
            if (!first) {
                result.append(", ");
            }
            result.append(value);
            first = false;
        }
        return result.toString();
    }
}
