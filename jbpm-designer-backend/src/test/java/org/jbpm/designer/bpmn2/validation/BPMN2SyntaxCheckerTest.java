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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BPMN2SyntaxCheckerTest {

    @Test
    public void testIsCompensatingFlowNodeInSubprocessForTextAnnotation() throws Exception {
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker("", "", null);

        SubProcess subprocess = Bpmn2Factory.eINSTANCE.createSubProcess();
        TextAnnotation textAnnotation = Bpmn2Factory.eINSTANCE.createTextAnnotation();

        subprocess.getFlowElements().add(textAnnotation);

        assertTrue(syntaxChecker.isCompensatingFlowNodeInSubprocess(textAnnotation, subprocess));
    }
}
