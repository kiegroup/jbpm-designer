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

package org.jbpm.designer.expressioneditor.marshalling;

public interface ExpressionEditorMessageTokens {

    static final String OPERATOR_TOKEN = "operator";
    static final String CONDITION_TOKEN = "condition";
    static final String CONDITIONS_TOKEN = "conditions";
    static final String PARAMETERS_TOKEN = "parameters";
    static final String SCRIPT_TOKEN = "script";
    static final String ERROR_CODE_TOKEN = "errorCode";
    static final String ERROR_MESSAGE_TOKEN = "errorMessage";
}
