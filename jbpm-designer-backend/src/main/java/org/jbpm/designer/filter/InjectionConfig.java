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

package org.jbpm.designer.filter;

import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
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
        DocumentBuilder parser;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO log
            return;
        }
        InputStream is = null;
        try {
            is = this.getClass().getResourceAsStream(DEFAULT_INJECTION_CONF_FILE);
            if (is == null) {
                System.out.println("unable to find designer injection conf file "
                                           + DEFAULT_INJECTION_CONF_FILE);
            } else {
                Document doc = parser.parse(is);
                NodeList rulesConf = doc.getElementsByTagName("rule");
                rules = new InjectionRules();
                for (int i = 0; i < rulesConf.getLength(); i++) {
                    Node ruleNode = rulesConf.item(i);
                    InjectionRule rule = new InjectionRule(ruleNode,
                                                           contextPath);
                    if (rule.isEnabled()) {
                        rules.add(rule);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Log
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public InjectionRules getRules() {
        return rules;
    }
}
