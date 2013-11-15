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

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ExpressionEditorMessage;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;

import java.io.ByteArrayOutputStream;

public class ExpressionEditorMessageJSONMarshaller {

    public ExpressionEditorMessageJSONMarshaller() {

    }

    public String marshall(ExpressionEditorMessage message) throws Exception {

        if (message == null) return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator generator = jsonFactory.createJsonGenerator(outputStream, JsonEncoding.UTF8);

        generator.writeStartObject();

        if (message.getExpression() == null) message.setExpression(new ConditionExpression());

        if (message.getExpression() != null) {

            generator.writeFieldName(ExpressionEditorMessageTokens.OPERATOR_TOKEN);
            if (message.getExpression().getOperator() != null) {
                generator.writeString(message.getExpression().getOperator());
            } else {
                generator.writeNull();
            }

            generator.writeArrayFieldStart(ExpressionEditorMessageTokens.CONDITIONS_TOKEN);
            if (message.getExpression().getConditions().size() > 0) {
                for (Condition condition : message.getExpression().getConditions()) {
                    generator.writeStartObject();
                    generator.writeFieldName(ExpressionEditorMessageTokens.CONDITION_TOKEN);
                    generator.writeString(condition.getFunction());
                    if (condition.getParameters().size() > 0) {
                        generator.writeArrayFieldStart(ExpressionEditorMessageTokens.PARAMETERS_TOKEN);
                        for (String param : condition.getParameters()) {
                            generator.writeString(param);
                        }
                        generator.writeEndArray();
                    }
                    generator.writeEndObject();
                }
            }
            generator.writeEndArray();
        }

        generator.writeFieldName(ExpressionEditorMessageTokens.SCRIPT_TOKEN);
        if (message.getScript() != null) {
            generator.writeString(message.getScript());
        } else {
            generator.writeNull();
        }

        generator.writeFieldName(ExpressionEditorMessageTokens.ERROR_CODE_TOKEN);
        if (message.getErrorCode() != null) {
            generator.writeString(message.getErrorCode());
        } else {
            generator.writeNull();
        }

        generator.writeFieldName(ExpressionEditorMessageTokens.ERROR_MESSAGE_TOKEN);
        if (message.getErrorMessage() != null) {
            generator.writeString(message.getErrorMessage());
        } else {
            generator.writeNull();
        }

        generator.writeEndObject();

        generator.close();
        return outputStream.toString();

    }

}
