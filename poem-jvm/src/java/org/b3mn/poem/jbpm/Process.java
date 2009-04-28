package org.b3mn.poem.jbpm;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Process {

	private String name;
	private String key;
	private String version;
	private String description;
	private List<org.b3mn.poem.jbpm.Node> childNodes;
	private HashMap<String, org.b3mn.poem.jbpm.Node> children;
	private Node root;

	public Process(Node rootNode) {
		this.root = rootNode;
		childNodes = new ArrayList<org.b3mn.poem.jbpm.Node>();
		children = new HashMap<String, org.b3mn.poem.jbpm.Node>();

		NamedNodeMap attributes = root.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.key = JpdlToJson.getAttribute(attributes, "key");
		this.version = JpdlToJson.getAttribute(attributes, "version");
		this.description = JpdlToJson.getAttribute(attributes, "description");

		if (root.hasChildNodes()) {
			int x = 0;
			try {
				for (Node node = root.getFirstChild(); node != null; node = node
						.getNextSibling()) {

					String stencil = node.getNodeName();
					org.b3mn.poem.jbpm.Node item = null;
					if (stencil.equals("start"))
						item = new StartEvent(node);
					else if (stencil.equals("end"))
						item = new EndEvent(node);
					else if (stencil.equals("end-error"))
						item = new EndErrorEvent(node);
					else if (stencil.equals("end-cancel"))
						item = new EndCancelEvent(node);
					else if (stencil.equals("task"))
						item = new Task(node);
					else if (stencil.equals("state"))
						item = new State(node);
					else if (stencil.equals("java"))
						item = new Java(node);
					else if (stencil.equals("esb"))
						item = new Esb(node);
					else if (stencil.equals("sql"))
						item = new Sql(node);
					else if (stencil.equals("hql"))
						item = new Hql(node);
					else if (stencil.equals("script"))
						item = new Script(node);
					else if (stencil.equals("join") || stencil.equals("fork"))
						item = new And(node);
					else if (stencil.equals("exclusive"))
						item = new Xor(node);
					if (item != null) {
						childNodes.add(item);
						try {
							String nodeName = node.getAttributes()
									.getNamedItem("name").getNodeValue();
							children.put(nodeName, item);
						} catch (Exception e) {
							children.put("start" + x, item);
							x++;
						}
					}
				}
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public Process(JSONObject process) {
		try {
			this.name = process.getJSONObject("properties").getString("name");
		} catch (JSONException e) {
		}

		try {
			this.key = process.getJSONObject("properties").getString("key");
		} catch (JSONException e) {
		}

		try {
			this.version = process.getJSONObject("properties").getString(
					"version");
		} catch (JSONException e) {
		}

		try {
			this.description = process.getJSONObject("properties").getString(
					"documentation");
		} catch (JSONException e) {
		}

		childNodes = new ArrayList<org.b3mn.poem.jbpm.Node>();

		try {
			JSONArray processElements = process.getJSONArray("childShapes");

			// Create all process nodes
			for (int i = 0; i < processElements.length(); i++) {
				JSONObject currentElement = processElements.getJSONObject(i);
				String currentElementID = currentElement.getJSONObject(
						"stencil").getString("id");
				org.b3mn.poem.jbpm.Node item = null;
				if (currentElementID.equals("StartEvent"))
					item = new StartEvent(currentElement);
				else if (currentElementID.equals("EndEvent"))
					item = new EndEvent(currentElement);
				else if (currentElementID.equals("EndErrorEvent"))
					item = new EndErrorEvent(currentElement);
				else if (currentElementID.equals("EndCancelEvent"))
					item = new EndCancelEvent(currentElement);
				else if (currentElementID.equals("Task"))
					item = new Task(currentElement);
				else if (currentElementID.equals("wait"))
					item = new State(currentElement);
				else if (currentElementID.equals("java"))
					item = new Java(currentElement);
				else if (currentElementID.equals("esb"))
					item = new Esb(currentElement);
				else if (currentElementID.equals("sql"))
					item = new Sql(currentElement);
				else if (currentElementID.equals("hql"))
					item = new Hql(currentElement);
				else if (currentElementID.equals("script"))
					item = new Script(currentElement);
				else if (currentElementID.equals("AND_Gateway"))
					item = new And(currentElement);
				else if (currentElementID.equals("Exclusive_Databased_Gateway"))
					item = new Xor(currentElement);
				if (item != null)
					childNodes.add(item);
			}
		} catch (JSONException e) {
		}
	}

	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();

		jpdl.write("<process");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("key", key));
		jpdl.write(JsonToJpdl.transformAttribute("version", version));
		jpdl.write(JsonToJpdl.transformAttribute("description", description));
		jpdl.write(JsonToJpdl.transformAttribute("xmlns",
				"http://jbpm.org/4/jpdl"));
		jpdl.write(" >\n");

		for (int i = 0; i < childNodes.size(); i++) {
			jpdl.write(childNodes.get(i).toJpdl());
		}

		jpdl.write("</process>");
		return jpdl.toString();
	}

	public void createTransitions() {
		int x = 0;
		for (Node node = root.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (!node.getNodeName().equals("#text")) {
				org.b3mn.poem.jbpm.Node currentStencil;
				try {
					String currentStencilName = node.getAttributes()
							.getNamedItem("name").getNodeValue();
					currentStencil = children.get(currentStencilName);
				} catch (Exception e) {
					currentStencil = children.get("start" + x);
					x++;
				}
				List<Transition> outgoings = new ArrayList<Transition>();
				if (node.hasChildNodes()) {
					for (Node item = node.getFirstChild(); item != null; item = item
							.getNextSibling()) {
						if (item.getNodeName().equals("transition")) {
							Transition t = new Transition(item);
							t.setStart(new Docker(currentStencil.getBounds()
									.getWidth() / 2, currentStencil.getBounds()
									.getHeight() / 2));
							outgoings.add(t);
						}
					}
				}
				currentStencil.setOutgoings(outgoings);
			}
		}
	}

	public String toJson() throws JSONException {
		JSONObject process = new JSONObject();

		JSONObject stencilset = new JSONObject();
		stencilset.put("url", "/oryx/stencilsets/bpmn1.1/bpmn1.1.json");

		JSONObject stencil = new JSONObject();
		stencil.put("id", "BPMNDiagram");

		JSONObject properties = new JSONObject();
		properties.put("ssextension",
				"http://oryx-editor.org/stencilsets/extensions/jbpm#");
		if (name != null)
			properties.put("name", name);
		if (key != null)
			properties.put("key", key);
		if (version != null)
			properties.put("version", version);
		if (description != null)
			properties.put("documentation", description);

		process.put("resourceId", "oryx-canvas123");
		process.put("stencilset", stencilset);
		process.put("stencil", stencil);
		process.put("properties", properties);
		JSONArray childShapes = new JSONArray();

		// add all childShapes
		for (org.b3mn.poem.jbpm.Node n : childNodes) {
			childShapes.put(n.toJson());
			for (Transition t : n.getOutgoings())
				childShapes.put(t.toJson());
		}

		process.put("childShapes", childShapes);
		return process.toString();
	}

	public org.b3mn.poem.jbpm.Node getTarget(String targetName) {
		return children.get(targetName);
	}
}
