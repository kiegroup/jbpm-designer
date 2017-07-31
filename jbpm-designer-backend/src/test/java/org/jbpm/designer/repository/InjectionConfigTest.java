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

package org.jbpm.designer.repository;

import org.jbpm.designer.filter.InjectionConfig;
import org.jbpm.designer.filter.InjectionRule;
import org.jbpm.designer.filter.InjectionRules;
import org.jbpm.designer.helper.TestServletContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class InjectionConfigTest extends RepositoryBaseTest {

    @Test
    public void testInjectionConfig() {
        InjectionConfig injConf = new InjectionConfig(new TestServletContext());
        assertEquals("[headerincludes;/.*;1;true]",
                     injectionRulesToString(injConf.getRules()));
    }

    private String injectionRulesToString(InjectionRules rules) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rules.size(); i++) {
            InjectionRule rule = (InjectionRule) rules.get(0);
            sb.append("[");
            sb.append(rule.getName());
            sb.append(";");
            sb.append(rule.getPattern());
            sb.append(";");
            sb.append(rule.getInsertAt());
            sb.append(";");
            sb.append(rule.isEnabled());
            sb.append("]");
        }
        return sb.toString();
    }
}
