package de.hpi.execpn.pnml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ToolspecificPNMLHelper {
	
	private static final String toolTitle = "Petri Net Engine";
	private static final String toolVersion = "1.0";
	
	Element addToolspecificElement(Document doc, Element parent){
		Element ts = (Element) parent.appendChild(doc.createElement("toolspecific"));
		ts.setAttribute("tool", toolTitle);
		ts.setAttribute("version", toolVersion);
		return ts;
	}
	
	boolean hasChildWithName(Element parent, String name){
		return parent.getElementsByTagName(name).getLength() != 0;
	}
	
	void addModelReference(Document doc, Element parent, String modelURL){
		if (modelURL == null){
			return;
		}
		Element output, model;
		//create new 'output' and 'model' elements, if not already there
		if (!hasChildWithName(parent, "output")){		//no 'output' there -> create both new
			output = (Element) parent.appendChild(doc.createElement("output"));
			model = (Element) output.appendChild(doc.createElement("model"));
		}else{
			output = (Element) parent.getElementsByTagName("output").item(0);			
			if (!hasChildWithName(output, "model")){
				model = (Element) output.appendChild(doc.createElement("model"));
			}else{
				model = (Element) parent.getElementsByTagName("model").item(0);
			}
		}
		model.setAttribute("href", modelURL);
	}
	
	void setTaskAndAction(Document doc, Element parent, String task, String action){
		Element worklist;
		if (!hasChildWithName(parent, "worklist")){
			worklist = (Element) parent.appendChild(doc.createElement("worklist"));
		}else{
			worklist =  (Element) parent.getElementsByTagName("worklist").item(0);
		}
		worklist.setAttribute("task", task);
		worklist.setAttribute("action", action);
	}
	
	void addLocator(Document doc, Element parent, Locator loc){
		Element locator = (Element)parent.appendChild(doc.createElement("locator"));
		Element nameEl = (Element)locator.appendChild(doc.createElement("name"));
		nameEl.setTextContent(loc.getName());
		Element typeEl = (Element)locator.appendChild(doc.createElement("type"));
		typeEl.setTextContent(loc.getDatatype());
		Element exprEl = (Element)locator.appendChild(doc.createElement("expr"));
		exprEl.setTextContent(loc.getXpath());
	}

	
	void setGuard(Document doc, Element parent, String guardText) {
		Element guard = (Element)parent.appendChild(doc.createElement("guard"));
		Element expr = (Element)guard.appendChild(doc.createElement("expr"));
		expr.setTextContent(guardText);
	}
	
	void setRolename(Document doc, Element parent, String roleText) {
		Element role = (Element)parent.appendChild(doc.createElement("role"));
		role.setTextContent(roleText);
	}
	
	void setContextPlaceID(Document doc, Element parent, String id){
		Element contextPlaceID = (Element)parent.appendChild(doc.createElement("contextPlaceID"));
		contextPlaceID.setTextContent(id);
	}

	void setFireXsltURL(Document doc, Element parent, String url) {
		Element fire;
		if (!hasChildWithName(parent, "fire")){	
			fire = (Element) parent.appendChild(doc.createElement("fire"));
		}else{
			fire =  (Element) parent.getElementsByTagName("fire").item(0);
		}
		fire.setAttribute("href", url);
	}

	void setArcTransformationURL(Document doc, Element parent, String url) {
		Element trans = (Element) parent.appendChild(doc.createElement("transformation"));
		trans.setAttribute("href", url);
	}
	
	void addFormAndBindings(Document doc, Element parent, String formURL, String bindingURL){
		Element output, form, bindings;
		if (!hasChildWithName(parent, "output")){
			output = (Element) parent.appendChild(doc.createElement("output"));
		}else{
			output =  (Element) parent.getElementsByTagName("output").item(0);
		}
		if (formURL != null){
			if (!hasChildWithName(output, "form")){
				form = (Element) parent.appendChild(doc.createElement("form"));
			}else{
				form =  (Element) parent.getElementsByTagName("form").item(0);
			}
			form.setAttribute("href", formURL);
		}
		if (bindingURL != null){
			if (!hasChildWithName(output, "bindings")){
				bindings = (Element) parent.appendChild(doc.createElement("bindings"));
			}else{
				bindings =  (Element) parent.getElementsByTagName("bindings").item(0);
			}
			bindings.setAttribute("href", bindingURL);
		}
	}
}
