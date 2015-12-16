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

package org.jbpm.designer.server;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author gero.decker
 */
public class StencilSetUtil {

	public String getStencilSet(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		
		node = node.getFirstChild();
		while (node != null) {
			Node typeChild = getChild(node, "rdf:type");
			if (typeChild != null) {
				String resource = getAttributeValue(typeChild, "rdf:resource");
				if (resource != null && resource.equals("http://oryx-editor.org/canvas")) break;
			}
			node = node.getNextSibling();
		}
		if (node != null) {
			String type = getAttributeValue(getChild(node, "stencilset"), "rdf:resource");
			if (type != null) {
				return type.substring(type.lastIndexOf('/')+1);
			}
		}
		return null;
	}

//	protected String getContent(Node node) {
//		if (node != null && node.hasChildNodes())
//			return node.getFirstChild().getNodeValue();
//		return null;
//	}
	
	private String getAttributeValue(Node node, String attribute) {
		if (node.getAttributes() != null){ // text nodes have no attributes
			Node item = node.getAttributes().getNamedItem(attribute);
			if (item != null)
				return item.getNodeValue();
		}
		
		return null;
	}

	private Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

}


