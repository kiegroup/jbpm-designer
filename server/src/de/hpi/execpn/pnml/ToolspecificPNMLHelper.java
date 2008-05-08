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
	
	void setRoles(Document doc, Element parent, String rolename, String rightInitProcess, String rightExecuteTask,
				  String rightSkipTask, String rightDelegateTask) {
		Element role = (Element)parent.appendChild(doc.createElement("role"));
		role.setTextContent(rolename);
		
		Element initProcess = (Element)role.appendChild(doc.createElement("initProcess"));
		initProcess.setTextContent(rightInitProcess);
		
		Element trAllocate = (Element)role.appendChild(doc.createElement("trAllocate"));
		trAllocate.setTextContent(rightExecuteTask);
		
		Element trSuspend = (Element)role.appendChild(doc.createElement("trSuspend"));
		trSuspend.setTextContent(rightExecuteTask);
		
		Element trResume = (Element)role.appendChild(doc.createElement("trResume"));
		trResume.setTextContent(rightExecuteTask);
		
		Element trSubmit = (Element)role.appendChild(doc.createElement("trSubmit"));
		trSubmit.setTextContent(rightExecuteTask);
		
		Element trDelegate = (Element)role.appendChild(doc.createElement("trDelegate"));
		trDelegate.setTextContent(rightDelegateTask);
		
		Element trReview = (Element)role.appendChild(doc.createElement("trReview"));
		trReview.setTextContent(rightDelegateTask);
		
		Element trSkip = (Element)role.appendChild(doc.createElement("trSkip"));
		trSkip.setTextContent(rightSkipTask);		
	}
	
	void setFireTypeManual(Document doc, Element parent, boolean triggerManually) {
		Element fire = (Element)parent.appendChild(doc.createElement("fire"));
		if (triggerManually){
			fire.setAttribute("type", "manual");
		}else{
			fire.setAttribute("type", "automatic");
		}
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

}