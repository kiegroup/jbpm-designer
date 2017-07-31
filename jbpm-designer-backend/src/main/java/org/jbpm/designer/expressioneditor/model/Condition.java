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

package org.jbpm.designer.expressioneditor.model;

import java.util.ArrayList;
import java.util.List;

public class Condition {

    public static final String BETWEEN = "between";
    public static final String IS_NULL = "isNull";
    public static final String EQUALS_TO = "equalsTo";
    public static final String IS_EMPTY = "isEmpty";
    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";
    public static final String GREATER_THAN = "greaterThan";
    public static final String GREATER_OR_EQUAL_THAN = "greaterOrEqualThan";
    public static final String LESS_THAN = "lessThan";
    public static final String LESS_OR_EQUAL_THAN = "lessOrEqualThan";
    public static final String IS_TRUE = "isTrue";
    public static final String IS_FALSE = "isFalse";

    private String function;

    private List<String> parameters = new ArrayList<String>();

    public Condition() {
    }

    public Condition(String function) {
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public void addParam(String param) {
        parameters.add(param);
    }
}
