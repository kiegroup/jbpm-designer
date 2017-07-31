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

package org.jbpm.designer.bpmn2.validation;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public interface SyntaxChecker {

    public void checkSyntax();

    public Map<String, List<BPMN2SyntaxChecker.ValidationSyntaxError>> getErrors();

    public JSONObject getErrorsAsJson();

    public boolean errorsFound();

    public void clearErrors();
}
