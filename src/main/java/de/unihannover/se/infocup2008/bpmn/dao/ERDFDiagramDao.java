/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.unihannover.se.infocup2008.bpmn.dao;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingDockers;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagramERDF;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElementERDF;

/**
 * This class gets eRDF from a file or a oryxid and parses it
 * 
 * @author Team Royal Fawn
 * 
 */
public class ERDFDiagramDao {

	private BPMNDiagramERDF bpmnDiagram = null;

	private Document document = null;

	private DocumentBuilder db = null;

	public ERDFDiagramDao() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			this.db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads the model from the given file an parses it
	 * 
	 * @param filename
	 *            the filename of the file to parse
	 * @return the diagram or <code>null</code> in case of errors
	 */
	public BPMNDiagram getBPMNDiagramFromFile(String filename) {
		// load file from disc
		try {
			this.document = db.parse(new File(filename));
			TreeWalker treeWalker = normalizeDocumentAndPrepareTreewalker();
			this.bpmnDiagram = walkTree(treeWalker);
			return this.bpmnDiagram;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Reads the model with the given id from the oryx repository and parses it
	 * 
	 * @param oryxId
	 *            the id in the oryx repository
	 * @return the diagram or <code>null</code> in case of errors
	 */
	public BPMNDiagram getBPMNDiagramFromOryxId(String oryxId) {
		String eRDF = OryxRepositoryDao.getERDFFromOryx(oryxId);
		return getBPMNDiagramFromString(eRDF);
	}

	/**
	 * Reads the model from the eRDF and parses it
	 * 
	 * @param eRDF
	 *            the eRDF
	 * @return the diagram or <code>null</code> in case of errors
	 */
	public BPMNDiagramERDF getBPMNDiagramFromString(String eRDF) {
		if (eRDF == null) {
			return null;
		}
		try {
			this.document = db.parse(new InputSource(new StringReader(eRDF)));
			TreeWalker treeWalker = normalizeDocumentAndPrepareTreewalker();
			this.bpmnDiagram = walkTree(treeWalker);
			return this.bpmnDiagram;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private TreeWalker normalizeDocumentAndPrepareTreewalker() {
		this.document.getDocumentElement().normalize();
		DocumentTraversal docTraversal = (DocumentTraversal) this.document;
		return docTraversal.createTreeWalker(
				this.document.getDocumentElement(), NodeFilter.SHOW_ALL, null,
				false);

	}

	private BPMNDiagramERDF walkTree(TreeWalker walker) {
		BPMNDiagramERDF diagram = new BPMNDiagramERDF();

		walker.setCurrentNode(walker.getRoot());
		if (walker.getRoot().getNodeName().equals("html")) {
			walkHTMLVersion(walker, diagram);
		}

		processProcessData(walker, diagram);

		return diagram;
	}

	private void walkHTMLVersion(TreeWalker walker, BPMNDiagram diagram) {
		// walk children and find body
		for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
			if (n.getNodeName().equals("body")) {
				return;
			}

		}
	}

	private void processProcessData(TreeWalker walker, BPMNDiagramERDF diagram) {
		Node currentNode = walker.getCurrentNode();

		// walk children
		for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
			if (n.getNodeName().equals("div")) {
				if (!n.getAttributes().getNamedItem("id").getNodeValue().trim()
						.startsWith("oryx-canvas")) {
					// one Element
					processElement(walker, diagram);
				}
			}
		}

		// return position to the current node
		walker.setCurrentNode(currentNode);
	}

	private void processElement(TreeWalker walker, BPMNDiagramERDF diagram) {
		Node currentNode = walker.getCurrentNode();

		String id = currentNode.getAttributes().getNamedItem("id")
				.getNodeValue().trim();

		BPMNElementERDF element = (BPMNElementERDF) diagram.getElement(id);

		// walk children
		for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {
			if (n.getNodeName().equals("a")) {
				if (n.getAttributes().getNamedItem("rel").getNodeValue().trim()
						.equals("raziel-outgoing")) {// outgoing link found
					// get target ID
					String refId = n.getAttributes().getNamedItem("href")
							.getNodeValue().trim().substring(1);
					// get target Element
					BPMNElement followElement = (BPMNElement) diagram.getElement(refId);
					// connect
					element.addOutgoingLink(followElement);
					followElement.addIncomingLink(element);
				} else if (n.getAttributes().getNamedItem("rel").getNodeValue()
						.trim().equals("raziel-parent")) {
					// get target ID
					String refId = n.getAttributes().getNamedItem("href")
							.getNodeValue().trim().substring(1);

					// do not save parent if it is the canvas
					if (!refId.startsWith("oryx-canvas")) {
						// get target Element
						BPMNElement parentElement = (BPMNElement) diagram.getElement(refId);
						// set parent
						element.setParent(parentElement);
					}
				}
			} else if (n.getNodeName().equals("span")) {
				// attribute found, class describes which
				String nodeClass = n.getAttributes().getNamedItem("class")
						.getNodeValue().trim();
				if (nodeClass.equals("oryx-type")) {
					// type of element
					String type = n.getFirstChild().getNodeValue().trim();
					// int hashPos = type.indexOf("#");
					// type = type.substring(hashPos + 1);
					element.setType(type);
				} else if (nodeClass.equals("oryx-bounds")) {
					// bounds (x1,y1,x2,y2)
					String[] values = n.getFirstChild().getNodeValue().split(
							",");
					element.setBoundsNode(n.getFirstChild()); // remember node
					// to change
					// values

					double x = Double.valueOf(values[0]);
					double y = Double.valueOf(values[1]);
					element.setGeometry(new LayoutingBoundsImpl(x, y, Double
							.valueOf(values[2])
							- x, Double.valueOf(values[3]) - y));

				} else if (nodeClass.equals("oryx-dockers")
						|| nodeClass.equals("oryx-docker")) {
					// remember dockers-node
					element.setDockersNode(n.getFirstChild());
					LayoutingDockers dockers = element.getDockers();

					dockers.getPoints().clear();
					String[] values = n.getFirstChild().getNodeValue().split(
							" +");
					if (values.length > 2) {
						if (values.length % 2 != 1) {
							// values.length = number of coordinates * 2 +
							// trailing '#'
							throw new RuntimeException(
									"There must be even docker coordinates. Was parsing "
											+ Arrays.toString(values));
						}
						try {
							for (int i = 0; i < values.length - 1; i += 2) {
								dockers.addPoint(Double.parseDouble(values[i]),
										Double.parseDouble(values[i + 1]));
							}
						} catch (NumberFormatException e) {
							throw new RuntimeException(
									"Exception while parsing "
											+ Arrays.toString(values), e);
						}
					}
				}
			}

		}

		// return position to the current node
		walker.setCurrentNode(currentNode);
	}

	public void saveToFile(String filename) {
		try {

			// Prepare the DOM document for writing
			Source source = new DOMSource(this.document);

			// Prepare the output file
			File file = new File(filename);
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();

			xformer.transform(source, result);

		} catch (Exception e) {
			System.err
					.println("Unable to write XML to file " + filename + "\n");
			e.printStackTrace();
		}

	}

	public void saveToWriter(Writer writer) {

		// Prepare the DOM document for writing
		Source source = new DOMSource(this.document);

		// Prepare the output
		Result result = new StreamResult(writer);

		// Write the DOM document to the file
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
