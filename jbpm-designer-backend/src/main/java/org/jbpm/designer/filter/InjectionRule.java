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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.CDATASection;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InjectionRule {

    private String name;

    private Pattern pattern;
    private boolean enabled;
    private String elements = "";
    private int insertAt;
    private String strInsertAt;

    public InjectionRule(Node ruleNode,
                         String contextPath) throws TransformerFactoryConfigurationError,
            TransformerException {

        name = getAttrValue(ruleNode,
                            "name");
        setPattern(getAttrValue(ruleNode,
                                "url-pattern"));
        enabled = getAttrValue(ruleNode,
                               "enabled").equals("true");
        strInsertAt = getAttrValue(ruleNode,
                                   "insert-at");
        setInsertAt(strInsertAt);

        if (enabled) {
            NodeList items = ruleNode.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node item = items.item(i);
                if (item.getNodeType() == Node.CDATA_SECTION_NODE) {
                    CDATASection cdata = (CDATASection) item;
                    elements += cdata.getNodeValue().trim() + "\n";
                }
            }

            elements = elements.replaceAll("@contextpath@",
                                           contextPath);
        }
    }

    private static String getAttrValue(Node n,
                                       String attrName) {
        if (n == null) {
            return null;
        }
        NamedNodeMap attrs = n.getAttributes();
        if (attrs == null) {
            return null;
        }
        Node attr = attrs.getNamedItem(attrName);
        if (attr == null) {
            return null;
        }
        String val = attr.getNodeValue();
        if (val == null) {
            return null;
        }
        return val.trim();
    }

    public String toString() {
        return this.elements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(String p) throws PatternSyntaxException {
        try {
            pattern = Pattern.compile(p);
        } catch (Exception e) {
            // TODO log
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getInsertAt() {
        return insertAt;
    }

    public void setInsertAt(String strInsertAt) {
        if (strInsertAt.equals("head-begin".toLowerCase())) {
            insertAt = InsertAt.HEAD_BEGIN;
        }
        if (strInsertAt.equals("head-end".toLowerCase())) {
            insertAt = InsertAt.HEAD_END;
        }
        if (strInsertAt.equals("body-begin".toLowerCase())) {
            insertAt = InsertAt.BODY_BEGIN;
        }
        if (strInsertAt.equals("body-end".toLowerCase())) {
            insertAt = InsertAt.BODY_END;
        }
    }
}
