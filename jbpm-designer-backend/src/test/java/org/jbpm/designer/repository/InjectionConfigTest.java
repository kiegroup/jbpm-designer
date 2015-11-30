package org.jbpm.designer.repository;

import static org.junit.Assert.assertEquals;

import org.jbpm.designer.filter.InjectionConfig;
import org.jbpm.designer.filter.InjectionRule;
import org.jbpm.designer.filter.InjectionRules;
import org.jbpm.designer.helper.TestServletContext;
import org.junit.Test;

public class InjectionConfigTest extends RepositoryBaseTest{

    @Test
    public void testInjectionConfig() {
        InjectionConfig injConf = new InjectionConfig(new TestServletContext());
        assertEquals("[headerincludes;/.*;1;true]", injectionRulesToString(injConf.getRules()));
    }

    private String injectionRulesToString(InjectionRules rules) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rules.size(); i++) {
            InjectionRule rule = (InjectionRule)rules.get(0);
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
