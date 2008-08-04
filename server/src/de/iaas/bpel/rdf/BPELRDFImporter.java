package de.iaas.bpel.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.iaas.bpel.BPELFactory;
import de.iaas.bpel.models.Assign;
import de.iaas.bpel.models.BPELDiagram;
import de.iaas.bpel.models.BasicActivity;
import de.iaas.bpel.models.Catch;
import de.iaas.bpel.models.CatchAll;
import de.iaas.bpel.models.Compensante;
import de.iaas.bpel.models.CompensanteHandler;
import de.iaas.bpel.models.CompensanteScope;
import de.iaas.bpel.models.Container;
import de.iaas.bpel.models.Edge;
import de.iaas.bpel.models.Empty;
import de.iaas.bpel.models.EventHandler;
import de.iaas.bpel.models.Exit;
import de.iaas.bpel.models.ExtensionActivity;
import de.iaas.bpel.models.FaultHandler;
import de.iaas.bpel.models.Flow;
import de.iaas.bpel.models.ForEach;
import de.iaas.bpel.models.If;
import de.iaas.bpel.models.IfCondition;
import de.iaas.bpel.models.Invoke;
import de.iaas.bpel.models.OnAlarm;
import de.iaas.bpel.models.OnEvent;
import de.iaas.bpel.models.OnMessage;
import de.iaas.bpel.models.OpaqueActivity;
import de.iaas.bpel.models.Pick;
import de.iaas.bpel.models.Process;
import de.iaas.bpel.models.Receive;
import de.iaas.bpel.models.RepeatUntil;
import de.iaas.bpel.models.Reply;
import de.iaas.bpel.models.Scope;
import de.iaas.bpel.models.ScopeActivity;
import de.iaas.bpel.models.Sequence;
import de.iaas.bpel.models.SequenceFlow;
import de.iaas.bpel.models.SequenceIfFalse;
import de.iaas.bpel.models.SequenceIfTrue;
import de.iaas.bpel.models.SequenceOrder;
import de.iaas.bpel.models.StructuredActivity;
import de.iaas.bpel.models.TerminationHandler;
import de.iaas.bpel.models.Throw;
import de.iaas.bpel.models.Validate;
import de.iaas.bpel.models.Wait;
import de.iaas.bpel.models.While;

/**
 * Copyright (c) 2008 
 * 
 * Zhen Peng
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * main method: loadBPEL()
 * 
 * @author Zhen Peng
 * 
 */
public class BPELRDFImporter {

	protected Document doc;

	protected class ImportContext {
		BPELDiagram diagram;
		Map<String, de.iaas.bpel.models.DiagramObject> objects; // key = resource id,
		// value = diagram
		// object
		Map<String, de.iaas.bpel.models.DiagramObject> connections; // key = to resource
		// id, value = from
		// node
		Map<de.iaas.bpel.models.Node, String> parentRelationships; // key = child node,
		// value = parent
		// resource id
	}

	public BPELRDFImporter(Document doc) {
		this.doc = doc;
	}

	public BPELDiagram loadBPEL() {
		Node root = getRootNode(doc);
		if (root == null)
			return null;

		ImportContext c = new ImportContext();
		c.diagram = BPELFactory.eINSTANCE.createBPELDiagram();
		// Map map = new HashMap();
		c.objects = new HashMap(); // key = resource id, value = node
		c.connections = new HashMap(); // key = to resource id, value = from
		// node
		c.parentRelationships = new HashMap();

		List<Node> edges = new ArrayList<Node>();

		// handle nodes
		if (root.hasChildNodes()) {
			Node node = root.getFirstChild();
			while ((node = node.getNextSibling()) != null) {
				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null)
					continue;

				if (type.equals("BPELDiagram")) {
					handleDiagram(node, c);
				} else if (type.equals("Process")) {
					addProcess(node, c);
				} else if (type.equals("Invoke")) {
					addInvoke(node, c);
				} else if (type.equals("Receive")) {
					addReceive(node, c);
				} else if (type.equals("Reply")) {
					addReply(node, c);
				} else if (type.equals("Assign")) {
					addAssign(node, c);
				} else if (type.equals("Empty")) {
					addEmpty(node, c);
				} else if (type.equals("OpaqueActivity")){
					addOpaqueActivity(node,c);
				} else if (type.equals("Validat")){
					addValidat(node,c);
				} else if (type.equals("ExtensionActivity")){
					addExtensionActivity(node,c);
				} else if (type.equals("Wait")){
					addWait(node,c);
				} else if (type.equals("Throw")){
					addThrow(node,c);
				} else if (type.equals("Exit")){
					addExit(node,c);
				} else if (type.equals("Rethrow")){
					addRethrow(node,c);
					
					
				} else if (type.equals("If")){
					addIf(node,c);
				} else if (type.equals("IfCondiation")){
					addIfCondition(node,c);
				} else if (type.equals("Flow")){
					addFlow(node,c);
				} else if (type.equals("ForEach")){
					addForEach(node,c);
				} else if (type.equals("Pick")){
					addPick(node,c);
				} else if (type.equals("Sequence")){
					addSequence(node,c);
				} else if (type.equals("While")){
					addWhile(node,c);
				} else if (type.equals("RepeatUntil")){
					addRepeatUntil(node,c);
				} else if (type.equals("OnMessage")){
					addOnMessage(node,c);
				} else if (type.equals("OnAlarm")){
					addOnAlarm(node,c);
					
					
				} else if (type.equals("Scope")){
					addScope(node,c);
				} else if (type.equals("Compensante")){
					addCompensante(node,c);
				} else if (type.equals("CompensanteScope")){
					addCompensanteScope(node,c);
				} else if (type.equals("CompensanteHandler")){
					addCompensanteHandler(node,c);
				} else if (type.equals("EventHandler")){
					addEventHandler(node,c);
				} else if (type.equals("OnEvent")){
					addOnEvent(node,c);
				} else if (type.equals("TerminationHandler")){
					addTerminationHandler(node,c);
				} else if (type.equals("FaultHandler")){
					addFaultHandler(node,c);
				} else if (type.equals("Catch")){
					addCatch(node,c);
				} else if (type.equals("CatchAll")){
					addCatchAll(node,c);
					
					
				} else if (type.equals("SequenceOrder")) {
					edges.add(node);
				} else if (type.equals("SequenceFlow")) {
					edges.add(node);
				} else if (type.equals("SequenceIfTrue")) {
					edges.add(node);
				} else if (type.equals("SequenceIfFalse")) {
					edges.add(node);
				}
			}
		}

		// handle edges 
		for (Node edgeNode : edges) {
			String type = getType(edgeNode);
			if (type.equals("SequenceOrder")) {
				addSequenceOrder(edgeNode, c);
			} else if (type.equals("SequenceFlow")) {
				addSequenceFlow(edgeNode, c);
			} else if (type.equals("SequenceIfTrue")) {
				addSequenceIfTrue(edgeNode, c);
			} else if (type.equals("SequenceIfFalse")) {
				addSequenceIfFalse(edgeNode, c);
			}
		}

		// handle parent relationships
		setupParentRelationships(c);

		c.diagram.identifyProcesses();
		
		return c.diagram;
	}


	protected void setupParentRelationships(ImportContext c) {
		for (Entry<de.iaas.bpel.models.Node, String> entry : c.parentRelationships
				.entrySet()) {
			de.iaas.bpel.models.Node child = entry.getKey();
			de.iaas.bpel.models.DiagramObject parent = c.objects.get(entry.getValue());
			if (parent instanceof Container)
				child.setParent((Container) parent);
		}
	}

	protected void handleDiagram(Node node, ImportContext c) {
		if (node.hasChildNodes()) {

			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("title")) {
					c.diagram.setTitle(getContent(n));
				}
			}
		}
	}

	protected void addProcess(Node node, ImportContext c) {
		Process process = BPELFactory.eINSTANCE.createProcess();
		process.setResourceId(getResourceId(node));
		process.setParent(c.diagram);
		c.objects.put(process.getResourceId(), process);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("processId")) {
					process.setId(getContent(n));
				} else {
					handleStandardAttributes(attribute, n, process, c, "Name");
				}
			}
		}
		if (process.getId() == null)
			process.setId(process.getResourceId());
	}

	protected boolean handleStandardAttributes(String attribute, Node n,
			de.iaas.bpel.models.Node node, ImportContext c, String label) {
		if (attribute.equals("id")) {
			node.setId(getContent(n));
		} else if (attribute.equals("outgoing")) {
			c.connections.put(
					getResourceId(getAttributeValue(n, "rdf:resource")), node);
		} else if (node instanceof de.iaas.bpel.models.Node) {
			if (attribute.equals("parent")) {
				c.parentRelationships.put((de.iaas.bpel.models.Node) node,
						getResourceId(getAttributeValue(n, "rdf:resource")));
			} else if (attribute.equals(label)) {
				((de.iaas.bpel.models.Node) node).setLabel(getContent(n));
			}
		} else {
			return false;
		}
		return true;
	}

	protected void addInvoke(Node node, ImportContext c) {
		Invoke invoke = BPELFactory.eINSTANCE.createInvoke();
		invoke.setResourceId(getResourceId(node));
		c.objects.put(invoke.getResourceId(), invoke);

		if (node.hasChildNodes()) {

			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				handleStandardAttributes(attribute, n, invoke, c, "Name");
			}
		}

		if (invoke.getId() == null)
			invoke.setId(invoke.getResourceId());
	}


/*******************************  Handle Basic Activities  ********************************/
	protected void addReceive(Node node, ImportContext c) {
		Receive activity = BPELFactory.eINSTANCE.createReceive();
		handleBasicActivity(node, activity, c, "documentation");
	}
	
	protected void addReply(Node node, ImportContext c) {
		Reply activity = BPELFactory.eINSTANCE.createReply();
		handleBasicActivity(node, activity, c, "documentation");
	}
	
	protected void addAssign(Node node, ImportContext c) {
		Assign activity = BPELFactory.eINSTANCE.createAssign();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addEmpty(Node node, ImportContext c) {
		Empty activity = BPELFactory.eINSTANCE.createEmpty();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addOpaqueActivity(Node node, ImportContext c) {
		OpaqueActivity activity = BPELFactory.eINSTANCE.createOpaqueActivity();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addValidat(Node node, ImportContext c) {
		Validate activity = BPELFactory.eINSTANCE.createValidate();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addExtensionActivity(Node node, ImportContext c) {
		ExtensionActivity activity = BPELFactory.eINSTANCE.createExtensionActivity();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addWait(Node node, ImportContext c) {
		Wait activity = BPELFactory.eINSTANCE.createWait();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addThrow(Node node, ImportContext c) {
		Throw activity = BPELFactory.eINSTANCE.createThrow();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addExit(Node node, ImportContext c) {
		Exit activity = BPELFactory.eINSTANCE.createExit();
		handleBasicActivity(node, activity, c, "documentation");
	}
	protected void addRethrow(Node node, ImportContext c) {
		Receive activity = BPELFactory.eINSTANCE.createReceive();
		handleBasicActivity(node, activity, c, "documentation");
	}
	
	protected void handleBasicActivity(Node node, BasicActivity activity, ImportContext c,
			String label) {
		activity.setResourceId(getResourceId(node));
		activity.setParent(c.diagram);
		c.objects.put(activity.getResourceId(), activity);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				handleStandardAttributes(attribute, n, activity, c, label);
			}
		}
		if (activity.getId() == null)
			activity.setId(activity.getResourceId());
	}
	
	
	/********************************  Handle Structured Activities  *******************************/
	protected void addIf(Node node, ImportContext c) {
        If activity = BPELFactory.eINSTANCE.createIf();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addIfCondition(Node node, ImportContext c) {
		IfCondition activity = BPELFactory.eINSTANCE.createIfCondition();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addFlow(Node node, ImportContext c) {
		Flow activity = BPELFactory.eINSTANCE.createFlow();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addForEach(Node node, ImportContext c) {
		ForEach activity = BPELFactory.eINSTANCE.createForEach();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addPick(Node node, ImportContext c) {
		Pick activity = BPELFactory.eINSTANCE.createPick();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addSequence(Node node, ImportContext c) {
		Sequence activity = BPELFactory.eINSTANCE.createSequence();			handleStructuredActivity(node, activity, c);
	}
	
	protected void addWhile(Node node, ImportContext c) {
		While activity = BPELFactory.eINSTANCE.createWhile();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addRepeatUntil(Node node, ImportContext c) {
		RepeatUntil activity = BPELFactory.eINSTANCE.createRepeatUntil();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addOnMessage(Node node, ImportContext c) {
		OnMessage activity = BPELFactory.eINSTANCE.createOnMessage();
        handleStructuredActivity(node, activity, c);
	}
	
	protected void addOnAlarm(Node node, ImportContext c) {
		OnAlarm activity = BPELFactory.eINSTANCE.createOnAlarm();
        handleStructuredActivity(node, activity, c);
	}


	protected void handleStructuredActivity (Node node, StructuredActivity activity, ImportContext c) {
		activity.setResourceId(getResourceId(node));
		activity.setParent(c.diagram);
		c.objects.put(activity.getResourceId(), activity);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);
				handleStandardAttributes(attribute, n, activity, c,
						"Documentation");
			}
		}
		if (activity.getId() == null)
			activity.setId(activity.getResourceId());
	}
	
	
/********************************  Handle Scope Activities  *******************************/
	protected void addScope(Node node, ImportContext c) {
        Scope activity = BPELFactory.eINSTANCE.createScope();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addCompensante(Node node, ImportContext c) {
		Compensante activity = BPELFactory.eINSTANCE.createCompensante();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addCompensanteScope(Node node, ImportContext c) {
		CompensanteScope activity = BPELFactory.eINSTANCE.createCompensanteScope();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addCompensanteHandler(Node node, ImportContext c) {
		CompensanteHandler activity = BPELFactory.eINSTANCE.createCompensanteHandler();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addEventHandler(Node node, ImportContext c) {
		EventHandler activity = BPELFactory.eINSTANCE.createEventHandler();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addOnEvent(Node node, ImportContext c) {
		OnEvent activity = BPELFactory.eINSTANCE.createOnEvent();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addTerminationHandler(Node node, ImportContext c) {
		TerminationHandler activity = BPELFactory.eINSTANCE.createTerminationHandler();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addFaultHandler(Node node, ImportContext c) {
		FaultHandler activity = BPELFactory.eINSTANCE.createFaultHandler();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addCatch(Node node, ImportContext c) {
		Catch activity = BPELFactory.eINSTANCE.createCatch();
        handleScopeActivity(node, activity, c);
	}
	
	protected void addCatchAll(Node node, ImportContext c) {
		CatchAll activity = BPELFactory.eINSTANCE.createCatchAll();
        handleScopeActivity(node, activity, c);
	}


	protected void handleScopeActivity (Node node, ScopeActivity activity, ImportContext c) {
		activity.setResourceId(getResourceId(node));
		activity.setParent(c.diagram);
		c.objects.put(activity.getResourceId(), activity);

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);
				handleStandardAttributes(attribute, n, activity, c,
						"Documentation");
			}
		}
		if (activity.getId() == null)
			activity.setId(activity.getResourceId());
	}

/***********************************  Handle  Edges  ****************************************/
	protected void addSequenceFlow(Node node, ImportContext c) {
		SequenceFlow flow = BPELFactory.eINSTANCE.createSequenceFlow();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addSequenceOrder(Node node, ImportContext c) {
		SequenceOrder flow = BPELFactory.eINSTANCE.createSequenceOrder();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addSequenceIfTrue(Node node, ImportContext c) {
		SequenceIfTrue flow = BPELFactory.eINSTANCE.createSequenceIfTrue();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}

	protected void addSequenceIfFalse(Node node, ImportContext c) {
		SequenceIfFalse flow = BPELFactory.eINSTANCE.createSequenceIfFalse();
		c.diagram.getEdges().add(flow);
		setConnections(flow, node, c);
	}



	protected void setConnections(Edge edge, Node node, ImportContext c) {
		edge.setResourceId(getResourceId(node));
		c.objects.put(edge.getResourceId(), edge);
		edge.setSource((de.iaas.bpel.models.Node)c.connections.get(edge.getResourceId()));

		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(
						n.getNodeName().indexOf(':') + 1);

				if (attribute.equals("id")) {
					edge.setId(getContent(n));
				} else if (attribute.equals("outgoing")) {
					if (edge.getTarget() == null)
						edge.setTarget((de.iaas.bpel.models.Node)c.objects
								.get(getResourceId(getAttributeValue(n,
										"rdf:resource"))));
					else
						c.connections.put(getResourceId(getAttributeValue(n,
								"rdf:resource")), edge);
				}
			}
		}
		if (edge.getId() == null)
			edge.setId(edge.getResourceId());
	}

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	protected String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}

	protected String getResourceId(String id) {
		return id.substring(id.indexOf('#')+1);
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}

}
