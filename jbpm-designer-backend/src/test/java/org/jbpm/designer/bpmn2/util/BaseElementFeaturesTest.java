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
package org.jbpm.designer.bpmn2.util;

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.impl.BaseElementImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BaseElementFeaturesTest {

    @Test
    public void testBaseElementDocumentationFeature() {
        try {
            Documentation testDocumentation = Bpmn2Factory.eINSTANCE.createDocumentation();
            testDocumentation.setText("test docs");

            // using script task but any base element will do
            ScriptTask scriptTask = Bpmn2Factory.eINSTANCE.createScriptTask();
            scriptTask.getDocumentation().add(testDocumentation);

            BaseElementImpl baseElement = (BaseElementImpl) scriptTask;

            // for base elements the first ("0") feature should
            // always be documentation
            Object eGetFeatureObj = baseElement.eGet(0,
                                                     false,
                                                     true);

            assertNotNull(eGetFeatureObj);
            assertTrue(eGetFeatureObj instanceof List);

            List<Documentation> baseElementDocs = (List<Documentation>) eGetFeatureObj;
            assertEquals("test docs",
                         baseElementDocs.get(0).getText());
        } catch (Throwable t) {
            fail("Error getting base element feature: " + t.getMessage());
        }
    }
}
