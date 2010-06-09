package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Package")
public class XPDLPackage extends XPDLThing {
	
	@Attribute("Language")
	protected String language;
	@Attribute("QueryLanguage")
	protected String queryLanguage;
	
	@Element("PackageHeader")
	protected XPDLPackageHeader packageHeader;
	@Element("RedefinableHeader")
	protected XPDLRedefinableHeader redefinableHeader;
	@Element("ConformanceClass")
	protected XPDLConformanceClass conformanceClass;
	@Element("Script")
	protected XPDLScript script;
	
	protected XPDLPool mainPool;
	
	@Element("Artifacts")
	protected XPDLArtifacts artifacts;
	@Element("Associations")
	protected XPDLAssociations associations;
	@Element("MessageFlows")
	protected XPDLMessageFlows messageFlows;
	@Element("Pools")
	protected XPDLPools pools;
	@Element("WorkflowProcesses")
	protected XPDLWorkflowProcesses workflowProcesses;
	
	public XPDLPackage() {
		setConformanceClass(new XPDLConformanceClass());
	}
	
	public void createAndDistributeMapping() {
		Map<String, XPDLThing> mapping = new HashMap<String, XPDLThing>();
		
		if (getArtifacts() != null) {
			getArtifacts().createAndDistributeMapping(mapping);
		}
		if (getAssociations() != null) {
			getAssociations().createAndDistributeMapping(mapping);
		}
		if (getMessageFlows() != null) {
			getMessageFlows().createAndDistributeMapping(mapping);
		}
		if (getPools() != null) {
			getPools().createAndDistributeMapping(mapping);
		}
		if (getWorkflowProcesses() != null) {
			getWorkflowProcesses().createAndDistributeMapping(mapping);
		}
		
		for (Entry<String, XPDLThing> x : mapping.entrySet()) {
			System.out.println(x.getKey() + " " + x.getValue() + "\n");
		}
	}
	
	public XPDLArtifacts getArtifacts() {
		return artifacts;
	}
	
	public XPDLAssociations getAssociations() {
		return associations;
	}
	
	public XPDLConformanceClass getConformanceClass() {
		return conformanceClass;
	}

	public String getLanguage() {
		return language;
	}
	
	public XPDLMessageFlows getMessageFlows() {
		return messageFlows;
	}
	
	public XPDLPackageHeader getPackageHeader() {
		return packageHeader;
	}
	
	public XPDLPools getPools() {
		return pools;
	}
	
	public String getQueryLanguage() {
		return queryLanguage;
	}
	
	public XPDLRedefinableHeader getRedefinableHeader() {
		return redefinableHeader;
	}
	
	public XPDLScript getScript() {
		return script;
	}
	
	public XPDLWorkflowProcesses getWorkflowProcesses() {
		return workflowProcesses;
	}
	
	public void readJSONartifactsunknowns(JSONObject modelElement) throws JSONException {
		passInformationToArtifacts(modelElement, "artifactsunknowns");
	}
	
	public void readJSONassociationsunknowns(JSONObject modelElement) throws JSONException {
		passInformationToAssociations(modelElement, "associationsunknowns");
	}
	
	public void readJSONauthor(JSONObject modelElement) throws JSONException {
		passInformationToRedefinableHeader(modelElement, "author");
	}
	
	public void readJSONauthorunknowns(JSONObject modelElement) throws JSONException {
		passInformationToRedefinableHeader(modelElement, "authorunknowns");
	}
	
	public void readJSONbounds(JSONObject modelElement) {
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null) {
			for (int i = 0; i<childShapes.length(); i++) {
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if  (XPDLActivity.handlesStencil(stencil)) {
					createMainProcessChild(childShape);
					readJSONchildShapes(childShape);
				} else if (XPDLArtifact.handlesStencil(stencil)) {
					createArtifact(childShape);
					readJSONchildShapes(childShape);
				} else if (XPDLAssociation.handlesStencil(stencil)) {
					createAssociation(childShape);
					readJSONchildShapes(childShape);
				} else if (XPDLMessageFlow.handlesStencil(stencil)) {
					createMessageFlow(childShape);
					readJSONchildShapes(childShape);
				} else if (XPDLPool.handlesStencil(stencil)) {
					createPool(childShape);
				} else if (XPDLTransition.handlesStencil(stencil)) {
					createMainProcessChild(childShape);
					readJSONchildShapes(childShape);
				}
			}
		}
	}
	
	public void readJSONconformanceclassunknowns(JSONObject modelElement) throws JSONException {
		passInformationToConformanceClass(modelElement, "conformanceclassunknowns");
	}
	
	public void readJSONcreationdate(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "creationdate");
	}
	
	public void readJSONcreationdateunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "creationdateunknowns");
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "documentation");
	}
	
	public void readJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "documentationunknowns");
	}
	
	public void readJSONexpressionlanguage(JSONObject modelElement) throws JSONException {
		passInformationToScript(modelElement, "expressionlanguage");
	}
	
	public void readJSONexpressionunknowns(JSONObject modelElement) throws JSONException {
		passInformationToScript(modelElement, "expressionunknowns");
	}
	
	public void readJSONlanguage(JSONObject modelElement) {
		setLanguage(modelElement.optString("language"));
	}
	
	public void readJSONmessageflowsunknowns(JSONObject modelElement) throws JSONException {
		passInformationToMessageFlows(modelElement, "messageflowsunknowns");
	}
	
	public void readJSONmodificationdate(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "modificationdate");
	}
	
	public void readJSONmodificationdateunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "modificationdateunknowns");
	}
	
	public void readJSONpackageheaderunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "packageheaderunknowns");
	}
	
	public void readJSONpools(JSONObject modelElement) {
		createExtendedAttribute("pools", modelElement.optString("pools"));
	}
	
	public void readJSONpoolsunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPools(modelElement, "poolsunknowns");
	}
	
	public void readJSONquerylanguage(JSONObject modelElement) {
		setQueryLanguage(modelElement.optString("querylanguage"));
	}
	
	public void readJSONredefinableheaderunknowns(JSONObject modelElement) throws JSONException {
		passInformationToRedefinableHeader(modelElement, "redefinableheaderunknowns");
	}
	
	public void readJSONssextensions(JSONObject modelElement) {
	}
	
	public void readJSONstencilset(JSONObject modelElement) {
		//No need for export. Will be mapped to default values during import.
	}
	
	public void readJSONvendorunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "vendorunknowns");
	}
	
	public void readJSONversion(JSONObject modelElement) throws JSONException {
		passInformationToRedefinableHeader(modelElement, "version");
	}
	
	public void readJSONversionunknowns(JSONObject modelElement) throws JSONException {
		passInformationToRedefinableHeader(modelElement, "versionunknowns");
	}
	
	public void readJSONworkflowprocessesunknowns(JSONObject modelElement) throws JSONException {
		passInformationToWorkflowProcesses(modelElement, "workflowprocessesunknowns");
	}
	
	public void readJSONxpdlversionunknowns(JSONObject modelElement) throws JSONException {
		passInformationToPackageHeader(modelElement, "xpdlversionunknowns");
	}
	
	public void setArtifacts(XPDLArtifacts artifactsValue) {
		artifacts = artifactsValue;
	}
	
	public void setAssociations(XPDLAssociations associationsValue) {
		associations = associationsValue;
	}
	
	public void setConformanceClass(XPDLConformanceClass conformance) {
		conformanceClass = conformance;
	}
	
	public void setLanguage(String languageValue) {
		language = languageValue;
	}
	
	public void setMessageFlows(XPDLMessageFlows flows) {
		messageFlows = flows;
	}
	
	public void setPackageHeader(XPDLPackageHeader header) {
		packageHeader = header;
	}
	
	public void setPools(XPDLPools poolsValue) {
		pools = poolsValue;
	}
	
	public void setQueryLanguage(String languageValue) {
		queryLanguage = languageValue;
	}
	
	public void setRedefinableHeader(XPDLRedefinableHeader header) {
		redefinableHeader = header;
	}
	
	public void setWorkflowProcesses(XPDLWorkflowProcesses processes) {
		workflowProcesses = processes;
	}
	
	public void setScript(XPDLScript scriptValue) {
		script = scriptValue;
	}
	
	public void setWorklfowProcesses(XPDLWorkflowProcesses processes) {
		workflowProcesses = processes;
	}
	
	public void writeJSONbounds(JSONObject modelElement) throws JSONException {
		JSONObject upperLeft = new JSONObject();
		upperLeft.put("x", 0);
		upperLeft.put("y", 0);
		
		//Do some more sophisticated stuff here?!
		JSONObject lowerRight = new JSONObject();
		lowerRight.put("x", 1485);
		lowerRight.put("y", 1050);
		
		JSONObject bounds = new JSONObject();
		bounds.put("lowerRight", lowerRight);
		bounds.put("upperLeft", upperLeft);
		
		modelElement.put("bounds", bounds);
	}
	
	public void writeJSONartifacts(JSONObject modelElement) throws JSONException {
		XPDLArtifacts artifactsList = getArtifacts();
		if (artifactsList != null) {
			artifactsList.write(modelElement);
		}
	}
	
	public void writeJSONassociations(JSONObject modelElement) throws JSONException {
		XPDLAssociations associationsList = getAssociations();
		if (associationsList != null) {
			associationsList.write(modelElement);
		}
	}
	
	public void writeJSONconformanceClass(JSONObject modelElement) throws JSONException {
		XPDLConformanceClass classObject = getConformanceClass();
		if (classObject != null) {
			initializeProperties(modelElement);
			classObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONlanguage(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "language", getLanguage());
	}
	
	public void writeJSONmessageflows(JSONObject modelElement) throws JSONException {
		XPDLMessageFlows flowList = getMessageFlows();
		if (flowList != null) {
			flowList.write(modelElement);
		}
	}
	
	public void writeJSONquerylanguage(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "querylanguage", getQueryLanguage());
	}
	
	public void writeJSONpackageHeader(JSONObject modelElement) throws JSONException {
		XPDLPackageHeader header = getPackageHeader();
		if (header != null) {
			initializeProperties(modelElement);
			header.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONpools(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "pools", "");
	}
	
	public void writeJSONpoolsAndProcesses(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLWorkflowProcess> unmapped = null;
		if (getPools() != null) {
			ArrayList<XPDLPool> poolsList = getPools().getPools();
			if (poolsList != null && getWorkflowProcesses() != null) {
				unmapped = getWorkflowProcesses().getWorkflowProcesses();
				if (unmapped != null) {
					for (int i = 0; i < poolsList.size(); i++) {
						for (int j = 0; j < unmapped.size(); j++) {
							XPDLPool mapPool = poolsList.get(i);
							XPDLWorkflowProcess mapProcess = unmapped.get(j);
							
							if (mapProcess.getId().equals(mapPool.getProcess())) {
								//Interconnect process and pool
								mapPool.setAccordingProcess(unmapped.get(j));
								unmapped.remove(j);
								break;
							}
						}
					}
				}
			}
			//Write pools now, be cause process may be connected now
			getPools().write(modelElement);
		}
		writeUnmappedProcesses(unmapped, modelElement);
	}
	
	public void writeJSONredefinableHeader(JSONObject modelElement) throws JSONException {
		XPDLRedefinableHeader header = getRedefinableHeader();
		if (header != null) {
			initializeProperties(modelElement);
			header.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
		modelElement.put("resourceId", "oryx-canvas123");
	}
	
	public void writeJSONscript(JSONObject modelElement) throws JSONException {
		XPDLScript scriptObject = getScript();
		if (scriptObject != null) {
			initializeProperties(modelElement);
			scriptObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "BPMNDiagram");
		
		modelElement.put("stencil", stencil);
	}
	
	public void writeJSONstencilset(JSONObject modelElement) throws JSONException {
		JSONObject stencilset = new JSONObject();
		stencilset.put("url", "/oryx//stencilsets/bpmn1.1/bpmn1.1.json");
		stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn1.1#");
		
		modelElement.put("stencilset", stencilset);
	}
	
	public void writeJSONssextensions(JSONObject modelElement) throws JSONException {
		modelElement.put("ssextensions", new JSONArray());
	}
	
	public void writeJSONworkflowprocesses(JSONObject modelElement) {
		XPDLWorkflowProcesses processes = getWorkflowProcesses();
		if (processes != null) {
			processes.write(modelElement);
		}
	}
	
	protected void createArtifact(JSONObject modelElement) {
		initializeArtifacts();
		
		XPDLArtifact nextArtifact = new XPDLArtifact();
		nextArtifact.setResourceIdToShape(getResourceIdToShape());
		nextArtifact.parse(modelElement);
		getArtifacts().add(nextArtifact);
	}
	
	protected void createAssociation(JSONObject modelElement) {
		initializeAssociations();
		
		XPDLAssociation nextAssociation = new XPDLAssociation();
		nextAssociation.setResourceIdToShape(getResourceIdToShape());
		nextAssociation.parse(modelElement);
		getAssociations().add(nextAssociation);
	}
	
	protected void createMessageFlow(JSONObject modelElement) {
		initializeMessageFlows();
		
		XPDLMessageFlow nextFlow = new XPDLMessageFlow();
		nextFlow.setResourceIdToShape(getResourceIdToShape());
		nextFlow.parse(modelElement);
		getMessageFlows().add(nextFlow);
	}
	
	protected XPDLPool createPool(JSONObject modelElement) {	
		initializeWorkflowProcesses();
		
		XPDLWorkflowProcess accordingProcess = new XPDLWorkflowProcess();
		accordingProcess.parse(modelElement);
		getWorkflowProcesses().add(accordingProcess);
		
		initializePools();
		
		XPDLPool nextPool = new XPDLPool();
		nextPool.setResourceIdToShape(getResourceIdToShape());
		nextPool.setAccordingProcess(accordingProcess);
		nextPool.parse(modelElement);
		getPools().add(nextPool);
		
		return nextPool;
	}
	
	protected void createMainProcessChild(JSONObject modelElement) throws JSONException {
		initializeMainPool();
		
		JSONArray childShapesArray = new JSONArray();
		childShapesArray.put(modelElement);
		
		JSONObject childShapes = new JSONObject();
		childShapes.put("childShapes", childShapesArray);
		
		XPDLWorkflowProcess mainProcess = getMainPool().getAccordingProcess();
		mainProcess.setResourceIdToShape(getResourceIdToShape());
		mainProcess.parse(childShapes);
	}
	
	protected XPDLPool getMainPool() {
		return mainPool;
	}
	
	protected void initializeArtifacts() {
		if (getArtifacts() == null) {
			setArtifacts(new XPDLArtifacts());
		}
	}
	
	protected void initializeAssociations() {
		if (getAssociations() == null) {
			setAssociations(new XPDLAssociations());
		}
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializeMainPool() throws JSONException {
		if (getMainPool() == null) {
			JSONObject modelElement = new JSONObject(XPDLWorkflowProcess.implicitPool);
			XPDLPool newMainPool = createPool(modelElement);
			
			setMainPool(newMainPool);
		}
	}
	
	protected void initializeMessageFlows() {
		if (getMessageFlows() == null) {
			setMessageFlows(new XPDLMessageFlows());
		}
	}
	
	protected void initializePackageHeader() {
		if (getPackageHeader() == null) {
			setPackageHeader(new XPDLPackageHeader());
		}
	}
	
	protected void initializePools() {
		if (getPools() == null) {
			setPools(new XPDLPools());
		}
	}
	
	protected void initializeRedefinableHeader() {
		if (getRedefinableHeader() == null) {
			setRedefinableHeader(new XPDLRedefinableHeader());
		}
	}
	
	protected void initializeScript() {
		if (getScript() == null) {
			setScript(new XPDLScript());
		}
	}
	
	protected void initializeWorkflowProcesses() {
		if (getWorkflowProcesses() == null) {
			setWorklfowProcesses(new XPDLWorkflowProcesses());
		}
	}
	
	protected void passInformationToArtifacts(JSONObject modelElement, String key) throws JSONException {
		initializeArtifacts();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getArtifacts().parse(passObject);
	}
	
	protected void passInformationToAssociations(JSONObject modelElement, String key) throws JSONException {
		initializeAssociations();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getAssociations().parse(passObject);
	}
	
	protected void passInformationToConformanceClass(JSONObject modelElement, String key) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getConformanceClass().parse(passObject);
	}
	
	protected void passInformationToMessageFlows(JSONObject modelElement, String key) throws JSONException {
		initializeMessageFlows();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getMessageFlows().parse(passObject);
	}
	
	protected void passInformationToPackageHeader(JSONObject modelElement, String key) throws JSONException {
		initializePackageHeader();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getPackageHeader().parse(passObject);
	}
	
	protected void passInformationToPools(JSONObject modelElement, String key) throws JSONException {
		initializePools();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getPools().parse(passObject);
	}
	
	protected void passInformationToRedefinableHeader(JSONObject modelElement, String key) throws JSONException {
		initializeRedefinableHeader();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getRedefinableHeader().parse(passObject);
	}
	
	protected void passInformationToScript(JSONObject modelElement, String key) throws JSONException {
		initializeScript();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getScript().parse(passObject);
	}
	
	protected void passInformationToWorkflowProcesses(JSONObject modelElement, String key) throws JSONException {
		initializeWorkflowProcesses();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getWorkflowProcesses().parse(passObject);
	}
	
	protected void setMainPool(XPDLPool poolValue) {
		mainPool = poolValue;
	}
	
	protected void writeUnmappedProcesses(ArrayList<XPDLWorkflowProcess> unmapped, JSONObject modelElement) throws JSONException {
		if (unmapped != null) {
			if (unmapped.size() > 0) {
				for (int i = 0; i < unmapped.size(); i++) {
					unmapped.get(i).writeChildrenOnly(modelElement);
				}
			}
		}
	}
}
