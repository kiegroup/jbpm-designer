package org.jbpm.designer.filter;

import java.util.ArrayList;
import java.util.Iterator;

public class InjectionRules extends ArrayList {
    public InjectionRules() {
        super();
    }

    public String toString() {
        String sRule = "";
        Iterator iter = this.iterator();

        while (iter.hasNext()) {
            InjectionRule rule = (InjectionRule) iter.next();
            sRule += rule.toString();
        }
        return sRule;
    }
}
