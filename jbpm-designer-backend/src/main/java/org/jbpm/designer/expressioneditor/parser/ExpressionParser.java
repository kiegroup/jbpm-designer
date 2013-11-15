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

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {

    private static final String VARIABLE_NAME_PARAM_REGEX = "[$_a-zA-Z][$_a-zA-Z0-9]*";

    public static final String KIE_FUNCTIONS = "KieFunctions.";

    private static Map<String, FunctionDef> functionsRegistry = new TreeMap<String, FunctionDef>();

    private int parseIndex = 0;

    private String expression;

    private String functionName = null;

    public static final String FUNCTION_NAME_NOT_RECOGNIZED_ERROR = "The function name \'{0}\' is not recognized by system.";

    public static final String FUNCTION_CALL_NOT_FOUND_ERROR = "Function call was not found, a token like \'" + KIE_FUNCTIONS + "functionName(variable, params)\' is expected.";

    public static final String VALID_FUNCTION_CALL_NOT_FOUND_ERROR = "The \'" + KIE_FUNCTIONS + "\' keyword must be followed by one of the following function names: \'{0}\'";

    public static final String FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR = "Function call \'{0}\' is not closed properly, character \')\' is expected.";

    public static final String SENTENCE_NOT_CLOSED_PROPERLY_ERROR = "Script not closed properly, character \';\' is expected.";

    public static final String VARIABLE_NAME_EXPECTED_ERROR = "Variable name not found, a valid process variable name is expected.";

    public static final String PARAMETER_DELIMITER_EXPECTED_ERROR = "Parameter delimiter \',\' is expected.";

    public static final String STRING_PARAMETER_EXPECTED_ERROR = "String parameter value like \"some value\" is expected.";

    public static final String RETURN_SENTENCE_EXPECTED_ERROR = "Sentence \'{0}\' is expected.";

    public static final String BLANK_AFTER_RETURN_EXPECTED_ERROR = "Sentence \'{0}\' must be followed by a blank space or a line break.";

    private static String functionNames = null;

    static {

        //Operators for all types:

        FunctionDef isNull = new FunctionDef("isNull");
        isNull.addParam("param1", Object.class);
        functionsRegistry.put(isNull.getName(), isNull);

        //Global operators:

        FunctionDef equalsTo = new FunctionDef("equalsTo");
        equalsTo.addParam("param1", Object.class);
        equalsTo.addParam("param2", String.class);
        functionsRegistry.put(equalsTo.getName(), equalsTo);

        //Operators for String type:

        FunctionDef isEmpty = new FunctionDef("isEmpty");
        isEmpty.addParam("param1", Object.class);
        functionsRegistry.put(isEmpty.getName(), isEmpty);

        FunctionDef contains = new FunctionDef("contains");
        contains.addParam("param1", Object.class);
        contains.addParam("param2", String.class);
        functionsRegistry.put(contains.getName(), contains);

        FunctionDef startsWith = new FunctionDef("startsWith");
        startsWith.addParam("param1", Object.class);
        startsWith.addParam("param2", String.class);
        functionsRegistry.put(startsWith.getName(), startsWith);

        FunctionDef endsWith = new FunctionDef("endsWith");
        endsWith.addParam("param1", Object.class);
        endsWith.addParam("param2", String.class);
        functionsRegistry.put(endsWith.getName(), endsWith);

        // Operators for Numeric types:

        FunctionDef greaterThan = new FunctionDef("greaterThan");
        greaterThan.addParam("param1", Object.class);
        greaterThan.addParam("param2", String.class);
        functionsRegistry.put(greaterThan.getName(), greaterThan);

        FunctionDef greaterOrEqualThan = new FunctionDef("greaterOrEqualThan");
        greaterOrEqualThan.addParam("param1", Object.class);
        greaterOrEqualThan.addParam("param2", String.class);
        functionsRegistry.put(greaterOrEqualThan.getName(), greaterOrEqualThan);

        FunctionDef lessThan = new FunctionDef("lessThan");
        lessThan.addParam("param1", Object.class);
        lessThan.addParam("param2", String.class);
        functionsRegistry.put(lessThan.getName(), lessThan);

        FunctionDef lessOrEqualThan = new FunctionDef("lessOrEqualThan");
        lessOrEqualThan.addParam("param1", Object.class);
        lessOrEqualThan.addParam("param2", String.class);
        functionsRegistry.put(lessOrEqualThan.getName(), lessOrEqualThan);

        FunctionDef between = new FunctionDef("between");
        between.addParam("param1", Object.class);
        between.addParam("param2", String.class);
        between.addParam("param3", String.class);
        functionsRegistry.put(between.getName(), between);

        // Operators for Boolean type:

        FunctionDef isTrue = new FunctionDef("isTrue");
        isTrue.addParam("param1", Object.class);
        functionsRegistry.put(isTrue.getName(), isTrue);


        FunctionDef isFalse = new FunctionDef("isFalse");
        isFalse.addParam("param1", Object.class);
        functionsRegistry.put(isFalse.getName(), isFalse);

        StringBuilder functionNamesBuilder = new StringBuilder();
        functionNamesBuilder.append("{");
        boolean first = true;
        for (String functionName : functionsRegistry.keySet()) {
            if (!first) functionNamesBuilder.append(", ");
            functionNamesBuilder.append(functionName);
            first = false;
        }
        functionNamesBuilder.append("}");
        functionNames = functionNamesBuilder.toString();
    }

    public ExpressionParser(String expression) {
        this.expression = expression;
        this.parseIndex = expression != null ? 0 : -1;
    }

    public ConditionExpression parse() throws ParseException {

        ConditionExpression conditionExpression = new ConditionExpression();
        Condition condition = null;
        FunctionDef functionDef = null;

        parseReturnSentence();

        functionName = parseFunctionName();
        functionName = functionName.substring(KIE_FUNCTIONS.length(), functionName.length());
        functionDef = functionsRegistry.get(functionName);

        if (functionDef == null) throw new ParseException(errorMessage(FUNCTION_NAME_NOT_RECOGNIZED_ERROR, functionName), parseIndex);

        conditionExpression.setOperator(ConditionExpression.AND_OPERATOR);
        condition = new Condition(functionName);
        conditionExpression.getConditions().add(condition);

        String param = null;
        boolean first = true;

        for (ParamDef paramDef : functionDef.getParams()) {
            if (first) {
                first = false;
            } else {
                parseParamDelimiter();
            }

            if (Object.class.getName().equals(paramDef.getType().getName())) {
                param = parseVariableName();
            } else {
                param = parseStringParameter();
            }
            condition.addParam(param);
        }

        //all parameters were consumed
        parseFunctionClose();
        parseSentenceClose();


       return conditionExpression;
    }

    private String parseReturnSentence() throws ParseException {

        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(RETURN_SENTENCE_EXPECTED_ERROR, "return"), parseIndex);

        if (!expression.startsWith("return", index)) {
            //the expression does not start with return.
            throw new ParseException(errorMessage(RETURN_SENTENCE_EXPECTED_ERROR, "return"), parseIndex);
        }

        parseIndex = index + "return".length();

        //next character after return must be a \n or a " "
        if (!isBlank(expression.charAt(parseIndex))) throw new ParseException(errorMessage(BLANK_AFTER_RETURN_EXPECTED_ERROR, "return"), parseIndex);

        return "return";
    }

    private String parseFunctionName() throws ParseException {

        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(FUNCTION_CALL_NOT_FOUND_ERROR), parseIndex);
        String functionName = null;

        if (!expression.startsWith(KIE_FUNCTIONS, index)) throw new ParseException(errorMessage(FUNCTION_CALL_NOT_FOUND_ERROR), parseIndex);

        for(FunctionDef functionDef : functionsRegistry.values()) {
            if (expression.startsWith(KIE_FUNCTIONS+functionDef.getName()+"(", index)) {
                functionName = KIE_FUNCTIONS+functionDef.getName();
                break;
            }
        }

        if (functionName == null) throw new ParseException(errorMessage(VALID_FUNCTION_CALL_NOT_FOUND_ERROR, functionNames()), parseIndex);

        parseIndex = index + functionName.length() +1;

        return functionName;
    }

    private String parseFunctionClose() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR, functionName), parseIndex);

        if (expression.charAt(index) != ')') throw new ParseException(errorMessage(FUNCTION_CALL_NOT_CLOSED_PROPERLY_ERROR, functionName), parseIndex);

        parseIndex = index +1;
        return ")";
    }

    private String parseSentenceClose() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(SENTENCE_NOT_CLOSED_PROPERLY_ERROR), parseIndex);

        if (expression.charAt(index) != ';') throw new ParseException(errorMessage(SENTENCE_NOT_CLOSED_PROPERLY_ERROR), parseIndex);

        parseIndex = index +1;
        while (parseIndex < expression.length()) {
            if (!isBlank(expression.charAt(parseIndex))) throw new ParseException(errorMessage(SENTENCE_NOT_CLOSED_PROPERLY_ERROR), parseIndex);
            parseIndex++;
        }

        return ";";
    }

    private String parseVariableName() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(VARIABLE_NAME_EXPECTED_ERROR), parseIndex);

        Pattern variableNameParam = Pattern.compile(VARIABLE_NAME_PARAM_REGEX);
        Matcher variableMatcher = variableNameParam.matcher(expression.substring(index, expression.length()));

        if (!Pattern.matches(VARIABLE_NAME_PARAM_REGEX, String.valueOf(expression.charAt(index)))) throw new ParseException(errorMessage(VARIABLE_NAME_EXPECTED_ERROR), parseIndex);

        String variableName = null;
        if (variableMatcher.find()) {
            variableName = variableMatcher.group();
        } else {
            throw new ParseException(errorMessage(VARIABLE_NAME_EXPECTED_ERROR), parseIndex);
        }

        parseIndex = index + variableName.length();

        return variableName;
    }

    private String parseParamDelimiter() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) throw new ParseException(errorMessage(PARAMETER_DELIMITER_EXPECTED_ERROR), parseIndex);

        if (expression.charAt(index) != ',') {
            throw new ParseException(errorMessage(PARAMETER_DELIMITER_EXPECTED_ERROR), parseIndex);
        }

        parseIndex = index + 1;
        return ",";
    }

    private String parseStringParameter() throws ParseException {
        int index = nextNonBlank();
        if (index < 0) throw new ParseException(STRING_PARAMETER_EXPECTED_ERROR, parseIndex);

        if (expression.charAt(index) != '"') {
            throw new ParseException(STRING_PARAMETER_EXPECTED_ERROR, parseIndex);
        }

        int shift = 1;
        Character scapeChar = Character.valueOf('\\');
        Character last = null;
        boolean strReaded = false;
        StringBuilder param = new StringBuilder();
        for (int i = index+1; i < expression.length(); i++) {
            if (expression.charAt(i) == '\\') {
                if (scapeChar.equals(last)) {
                    shift += 2;
                    param.append('\\');
                    last = null;
                } else {
                    last = expression.charAt(i);
                }
            } else if (expression.charAt(i) == '"') {
                if (scapeChar.equals(last)) {
                    shift += 2;
                    param.append('"');
                    last = null;
                } else {
                    shift++;
                    strReaded = true;
                    break;
                }
            } else if (expression.charAt(i) == 'n') {
                if (scapeChar.equals(last)) {
                    shift += 2;
                    param.append('\n');
                } else {
                    shift += 1;
                    param.append(expression.charAt(i));
                }
                last = null;
            } else {
                if (last != null) {
                    shift++;
                    param.append(last);
                }
                last = null;
                shift++;
                param.append(expression.charAt(i));
            }

        }

        if (!strReaded) throw new ParseException(STRING_PARAMETER_EXPECTED_ERROR, parseIndex);

        parseIndex = index + shift;
        return param.toString();
    }

    private int nextNonBlank() {
        if (parseIndex < 0) return -1;

        for (int i = parseIndex; i < expression.length(); i++) {
            if (!isBlank(expression.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    private int nextBlank() {
        if (parseIndex < 0) return -1;

        for (int i = parseIndex; i < expression.length(); i++) {
            if (isBlank(expression.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    private boolean isBlank(Character character) {
        return character != null && (character.equals('\n') || character.equals(' '));
    }

    private String errorMessage(String message, Object ... params) {
        return MessageFormat.format(message, params);
    }

    private String functionNames() {
        return functionNames;
    }
}