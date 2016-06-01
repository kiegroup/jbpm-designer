/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.bpmn2.validation;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TextAnnotation;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BPMN2SyntaxCheckerTest {

    private Bpmn2Loader loader = new Bpmn2Loader(BPMN2SyntaxCheckerTest.class);

    private Map<String, List<BPMN2SyntaxChecker.ValidationSyntaxError>> errors;

    @Test
    public void testUserTaskWithTaskName() throws Exception {
        loader.loadProcessFromXml("userTaskWithTaskName.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testUserTaskWithoutTaskName() throws Exception {
        JSONObject process = loader.loadProcessFromXml("userTaskWithoutTaskName.bpmn2");
        JSONObject userTask = loader.getChildByName(process, "User Task");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String userTaskId = userTask.getString("resourceId");
        assertTrue(errors.keySet().contains(userTaskId));
        assertEquals(1, errors.get(userTaskId).size());
        assertEquals(SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME, errors.get(userTaskId).get(0).getError());
    }

    @Test
    public void testIsCompensatingFlowNodeInSubprocessForTextAnnotation() throws Exception {
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker("", "", null);

        SubProcess subprocess = Bpmn2Factory.eINSTANCE.createSubProcess();
        TextAnnotation textAnnotation = Bpmn2Factory.eINSTANCE.createTextAnnotation();

        subprocess.getFlowElements().add(textAnnotation);

        assertTrue(syntaxChecker.isCompensatingFlowNodeInSubprocess(textAnnotation, subprocess));
    }
}
