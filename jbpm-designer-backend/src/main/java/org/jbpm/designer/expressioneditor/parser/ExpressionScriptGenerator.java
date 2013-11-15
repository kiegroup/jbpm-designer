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

package org.jbpm.designer.expressioneditor.parser;

import org.jbpm.designer.expressioneditor.model.Condition;
import org.jbpm.designer.expressioneditor.model.ConditionExpression;
import org.jbpm.designer.expressioneditor.server.ExpressionEditorErrors;

import java.util.List;


public class ExpressionScriptGenerator {


    public String generateScript(ConditionExpression expression, List<String> errors) {

        StringBuilder script = new StringBuilder();
        String operator = null;
        int validTerms = 0;

        //First version implementation. At the moment we don't need a more elaborated programming or templates
        //to generate the script.
        //For version 6.1 we can provide a more elaborated generation if needed.

        if ("OR".equals(expression.getOperator())) {
            operator = "||";
        } else if ("AND".equals(expression.getOperator())) {
            operator = "&&";
        } else if (expression.getConditions().size() > 1) {
            //we have multiple conditions and the operator is not defined.
            //the default operator will be AND
            operator = "&&";
        }

        for (Condition condition : expression.getConditions()) {
            if (addConditionToScript(condition, script, operator, validTerms, errors) > 0) {
                validTerms++;
            } else {
                //we have an invalid condition.
                //at the moment the approach is that all the generation fails.
                errors.add(ExpressionEditorErrors.INVALID_CONDITION_ERROR);
                return null;
            }
        }

        return "return " + script.toString() + ";";
    }

    private int addConditionToScript(final Condition condition, final StringBuilder script, final String operator, final int validTerms, final List<String> errors) {
        if (condition == null) return 0;
        if (!isValidFunction(condition.getFunction())) {
            errors.add("Invalid function : " + condition.getFunction());
            return 0;
        }
        //TODO evaluate if we put more validations.
        if (validTerms > 0) {
            script.append(" " + operator + " ");
        } else {
            script.append(" ");
        }
        script.append(ExpressionParser.KIE_FUNCTIONS + condition.getFunction().trim());
        script.append("(");
        boolean first = true;
        for (String param : condition.getParameters()) {
            if (first) {
                //first parameter is always a process variable name.
                script.append(param);
                first = false;
            } else {
                //the other parameters are always string parameters.
                //TODO escape " and line break charactrers.
                script.append(", ");
                script.append("\""+escapeStringParam(param)+"\"");
            }
        }
        script.append(")");
        return 1;
    }

    private String escapeStringParam(String param) {
        if (param == null) return null;
        StringBuilder escapedParam = new StringBuilder(param.length() * 2);
        char c;
        for (int i = 0; i < param.length(); i++) {
            c = param.charAt(i);
            switch (c) {
                case '"' :
                    escapedParam.append('\\');
                    escapedParam.append('"');
                    break;
                case '\n' :
                    escapedParam.append('\\');
                    escapedParam.append('n');
                    break;
                case '\\' :
                    escapedParam.append('\\');
                    escapedParam.append('\\');
                    break;
                default:
                    escapedParam.append(c);

            }
        }
        return escapedParam.toString();
    }

    private boolean isValidFunction(String function) {
        return function != null && !"".equals(function.trim());
    }
}
