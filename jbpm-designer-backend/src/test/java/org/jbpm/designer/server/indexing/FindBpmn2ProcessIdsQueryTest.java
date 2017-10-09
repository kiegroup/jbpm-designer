/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.designer.server.indexing;

import java.util.HashSet;
import java.util.Set;

import org.jbpm.designer.query.FindBpmn2ProcessIdsQuery;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FindBpmn2ProcessIdsQueryTest {

    private final static String ERROR_MSG = ResourceType.JAVA.toString() + "' can not be used";

    @Test
    public void findBpmnProcessIdsQueryTermsTest() {
        FindBpmn2ProcessIdsQuery query = new FindBpmn2ProcessIdsQuery();

        Set<ValueIndexTerm> queryTerms = new HashSet<>();
        try {
            query.validateTerms(queryTerms);
            fail("The required resources term is missing, but no exception was thrown.");
        } catch (IllegalArgumentException iae) {
            assertTrue("Incorrect error message: " + iae.getMessage(),
                       iae.getMessage().contains("Expected 'ValueResourceIndexTerm' term was not found."));
        }

        queryTerms = new HashSet<>();
        queryTerms.add(new ValueResourceIndexTerm("not-bpmn2-resources",
                                                  ResourceType.JAVA));
        try {
            query.validateTerms(queryTerms);
            fail("The required resources term is missing, but no exception was thrown.");
        } catch (IllegalArgumentException iae) {
            assertTrue("Incorrect error message: " + iae.getMessage(),
                       iae.getMessage().contains(ERROR_MSG));
        }

        queryTerms = new HashSet<>();
        queryTerms.add(new ValueResourceIndexTerm("*",
                                                  ResourceType.BPMN2,
                                                  ValueIndexTerm.TermSearchType.WILDCARD));
        try {
            query.validateTerms(queryTerms);
        } catch (IllegalArgumentException iae) {
            fail("The activation term is acceptable here, but an exception was thrown.");
        }
    }
}
