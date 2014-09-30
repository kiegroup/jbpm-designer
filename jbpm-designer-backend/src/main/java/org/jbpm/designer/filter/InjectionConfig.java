package org.jbpm.designer.filter;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InjectionConfig {
    private static final String DEFAULT_INJECTION_CONF_FILE = "/injection/designerinjection.xml";
    private static ServletContext context;

    private InjectionRules rules;

    public InjectionConfig(ServletContext sc) {
        context = sc;
        load(sc.getContextPath());
    }

    public synchronized void load(String contextPath) {
        InputStream is = null;
        is = this.getClass().getResourceAsStream(DEFAULT_INJECTION_CONF_FILE);
        if (is == null) {
            System.out.println("unable to find designer injection conf file "
                    + DEFAULT_INJECTION_CONF_FILE);
        }

        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        // factory.setIgnoringComments(true);
        // factory.setIgnoringElementContentWhitespace(true);
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO log
            return;
        }
        try {
            Document doc = parser.parse(is);
            NodeList rulesConf = doc.getElementsByTagName("rule");
            rules = new InjectionRules();
            for (int i = 0; i < rulesConf.getLength(); i++) {
                Node ruleNode = rulesConf.item(i);
                InjectionRule rule = new InjectionRule(ruleNode, contextPath);
                if (rule.isEnabled())
                    rules.add(rule);
            }
        } catch (Exception e) {
            // TODO Log
            e.printStackTrace();
        }
    }

    public InjectionRules getRules() {
        return rules;
    }
}
