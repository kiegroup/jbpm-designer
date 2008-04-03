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
	
	void setFireTypeAutomatic(Document doc, Element parent, Boolean automatic){
		Element fire;
		if (!hasChildWithName(parent, "fire")){
			fire = (Element) parent.appendChild(doc.createElement("fire"));
		}else{
			fire =  (Element) parent.getElementsByTagName("fire").item(0);
		}
		fire.setAttribute("type", "automatic");
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
}
