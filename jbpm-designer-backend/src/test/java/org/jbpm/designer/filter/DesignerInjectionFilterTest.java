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

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

public class DesignerInjectionFilterTest {

    private static final String INIT_STRING = "<head>some text</head><body>body content</body>";
    private static final StringBuffer INIT_BUFFER = new StringBuffer(INIT_STRING);
    private static final String EMPTY_RULE_XML = "<ruleNode name=\"named\" enabled=\"true\" insert-at=\"\"></ruleNode>";

    @Test
    public void testFindAndReplaceWithSameText() {
        String result = DesignerInjectionFilter.findAndReplace("</body>",
                                                               INIT_STRING,
                                                               "</body>");
        assertEquals("Find and replace the same text. Result string have to be unchanged.",
                     INIT_STRING,
                     result);
    }

    @Test
    public void testFindAndReplace() {
        String result = DesignerInjectionFilter.findAndReplace(" extended</body>",
                                                               INIT_STRING,
                                                               "</body>");
        final String DIFFERENCE = " extended";

        assertEquals("Source string have to be extended by \" extended\" value inside tag <body>",
                     "<head>some text</head><body>body content" + DIFFERENCE + "</body>",
                     result);
    }

    @Test
    public void testProcessContentWithoutFilters() throws Exception {
        InjectionRules rules = new InjectionRules();
        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        assertEquals("Missing rules can't to change source string.",
                     INIT_STRING,
                     result);
    }

    @Test
    public void testProcessContentWithEmptyFilter() throws Exception {
        InjectionRules rules = new InjectionRules();
        Element node = createDocument().getDocumentElement();

        InjectionRule rule = new InjectionRule(node,
                                               "/");
        //Missing rule will execute HEAD_BEGIN case because default value of int is 0. Is it correct behaviour?
        rules.add(rule);
        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        assertEquals("Missing filters can't to change source string.",
                     INIT_STRING,
                     result);
    }

    @Test
    public void testProcessContentWithEmptyFilters() throws Exception {
        InjectionRules rules = new InjectionRules();
        Element node = createDocument().getDocumentElement();

        // head-begin and body-begin have to be twice to test all possible branches of code
        String[] insertAtVariations = {"head-begin", "head-begin", "head-end", "body-begin", "body-begin", "body-end"};
        for (String insertAt : insertAtVariations) {
            node.setAttribute("insert-at",
                              insertAt);
            InjectionRule rule = new InjectionRule(node,
                                                   "/");
            rules.add(rule);
        }

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        assertEquals("Empty filters can't to change source string.",
                     INIT_STRING,
                     result);
    }

    @Test
    public void testProcessContentHeadBegin() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("head-begin",
                             "Test"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "\nTest\n<!-- mg#head#begin#marker -->";

        assertEquals("Head tag have to start with " + DIFFERENCE,
                     "<head>" + DIFFERENCE + "some text</head><body>body content</body>",
                     result);
    }

    @Test
    public void testProcessContentHeadBeginTwice() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("head-begin",
                             "Test"));
        rules.add(createRule("head-begin",
                             "Second"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "\nTest\nSecond\n<!-- mg#head#begin#marker -->";

        assertEquals("Head tag have to start with " + DIFFERENCE,
                     "<head>" + DIFFERENCE + "some text</head><body>body content</body>",
                     result);
    }

    @Test
    public void testProcessContentHeadEnd() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("head-end",
                             "Test"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "Test\n";

        assertEquals("Head tag have to end with " + DIFFERENCE,
                     "<head>some text" + DIFFERENCE + "</head><body>body content</body>",
                     result);
    }

    @Test
    public void testProcessContentBodyBegin() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("body-begin",
                             "Test"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "\nTest\n<!-- mg#body#begin#marker -->";

        assertEquals("Body tag have to start with " + DIFFERENCE,
                     "<head>some text</head><body>" + DIFFERENCE + "body content</body>",
                     result);
    }

    @Test
    public void testProcessContentBodyBeginTwice() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("body-begin",
                             "Test"));
        rules.add(createRule("body-begin",
                             "Second"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "\nTest\nSecond\n<!-- mg#body#begin#marker -->";

        assertEquals("Body tag have to start with " + DIFFERENCE,
                     "<head>some text</head><body>" + DIFFERENCE + "body content</body>",
                     result);
    }

    @Test
    public void testProcessContentBodyEnd() throws Exception {
        InjectionRules rules = new InjectionRules();
        rules.add(createRule("body-end",
                             "Test"));

        String result = DesignerInjectionFilter.processContent(INIT_BUFFER,
                                                               rules);
        final String DIFFERENCE = "Test\n\n";

        assertEquals("Body tag have to end with " + DIFFERENCE,
                     "<head>some text</head><body>body content" + DIFFERENCE + "</body>",
                     result);
    }

    private InjectionRule createRule(String insertAt,
                                     String cdataValue) throws Exception {
        Document doc = createDocument();
        Element node = doc.getDocumentElement();
        node.setAttribute("insert-at",
                          insertAt);
        node.appendChild(doc.createCDATASection(cdataValue));
        return new InjectionRule(node,
                                 "/");
    }

    private Document createDocument() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(
                new StringReader(EMPTY_RULE_XML)
        );
        return builder.parse(is);
    }
}
