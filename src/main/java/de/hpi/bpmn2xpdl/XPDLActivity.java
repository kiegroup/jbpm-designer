package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Activity")
public class XPDLActivity extends XPDLThingNodeGraphics {

	@Attribute("CompletionQuantity")
	protected String completionQuantity;
	@Attribute("IsATransaction")
	protected String isATransaction;
	@Attribute("IsForCompensation")
	protected String isForCompensation;
	@Attribute("StartQuantity")
	protected String startQuantity;
	@Attribute("Status")
	protected String status;
	
	@Element("Implementation")
	protected XPDLImplementation implementation;
	@Element("Event")
	protected XPDLEvent event;
	@Element("Loop")
	protected XPDLLoop loop;
	@Element("Route")
	protected XPDLRoute route;
	@Element("BlockActivity")
	protected XPDLBlockActivity blockActivity;
	@Element("Documentation")
	protected XPDLDocumentation documentation;
	@Element("Assignments")
	protected XPDLAssignments assignments;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				//Gateways
				"Complex_Gateway",
				"OR_Gateway",
				"AND_Gateway",
				"Exclusive_Eventbased_Gateway",
				"Exclusive_Databased_Gateway",
				
				//Events
				"StartEvent",
				"StartConditionalEvent",
				"StartMessageEvent",
				"StartMultipleEvent",
				"StartSignalEvent",
				"StartTimerEvent",
				
				"IntermediateEvent",
				"IntermediateCancelEvent",
				"IntermediateCompensationEventCatching",
				"IntermediateConditionalEvent",
				"IntermediateErrorEvent",
				"IntermediateLinkEventCatching",
				"IntermediateMessageEventCatching",
				"IntermediateMultipleEventCatching",
				"IntermediateSignalEventCatching",
				"IntermediateTimerEvent",
				
				"IntermediateCompensationEventThrowing",
				"IntermediateLinkEventThrowing",
				"IntermediateMessageEventThrowing",
				"IntermediateMultipleEventThrowing",
				"IntermediateSignalEventThrowing",
				
				"EndEvent",
				"EndCancelEvent",
				"EndCompensationEvent",
				"EndErrorEvent",
				"EndMessageEvent",
				"EndMultipleEvent",
				"EndSignalEvent",
				"EndTerminateEvent",
				
				//Task
				"Task",
				
				//Subprocesses
				"CollapsedSubprocess",
				"Subprocess"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public XPDLAssignments getAssignments() {
		return assignments;
	}
	
	public XPDLBlockActivity getBlockActivity() {
		return blockActivity;
	}

	public String getCompletionQuantity() {
		return completionQuantity;
	}
	
	public XPDLDocumentation getDocumentation() {
		return documentation;
	}
	
	public XPDLEvent getEvent() {
		return event;
	}
	
	public XPDLImplementation getImplementation() {
		return implementation;
	}
	
	public String getIsATransaction() {
		return isATransaction;
	}
	
	public String getIsForCompensation() {
		return isForCompensation;
	}
	
	public XPDLLoop getLoop() {
		return loop;
	}
	
	public XPDLRoute getRoute() {
		return route;
	}
	
	public String getStartQuantity() {
		return startQuantity;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void readJSONactivity(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "activity");
	}
	
	public void readJSONactivitytype(JSONObject modelElement) {
		createExtendedAttribute("activitytype", modelElement.optString("activitytype"));
	}
	
	public void readJSONactivityref(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "activityref");
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
	}
	
	public void readJSONassignments(JSONObject modelElement) throws JSONException {
		JSONArray items = modelElement.optJSONObject("assignments").optJSONArray("items");
		
		if (items != null) {
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.optJSONObject(i);
				createAssignment(item);
				createExtendedAttribute("assignmentTo", item.optString("to"));
				createExtendedAttribute("assignmentFrom", item.optString("from"));
			}
		}
	}
	
	public void readJSONassignmentsunknowns(JSONObject modelElement) throws JSONException {
		initializeAssignments();
		
		JSONObject passObject = new JSONObject();
		passObject.put("assignmentsunknowns", modelElement.optString("assignmentsunknowns"));
		getAssignments().parse(passObject);
	}
	
	public void readJSONblockunknowns(JSONObject modelElement) throws JSONException {
		passInformationToBlockActivity(modelElement, "blockunknowns");
	}
	
	public void readJSONcompletionquantity(JSONObject modelElement) {
		setCompletionQuantity(modelElement.optString("completionquantity"));
	}
	
	public void readJSONcomplexmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "complexmi_condition");
	}
	
	public void readJSONcondition(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "conditionref");
	}
	
	public void readJSONconditionref(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "conditionref");
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		passInformationToDocumentation(modelElement, "documentation");
	}
	
	public void readJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
		passInformationToDocumentation(modelElement, "documentationunknowns");
	}
	
	public void readJSONdefaultgate(JSONObject modelElement) {
		createExtendedAttribute("defaultgate", modelElement.optString("defaultgate"));
	}
	
	public void readJSONdiagramref(JSONObject modelElement) {
		createExtendedAttribute("diagramref", modelElement.optString("diagramref"));
	}
	
	public void readJSONendeventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "endeventunknowns");
	}
	
	public void readJSONentry(JSONObject modelElement) {
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "errorcode");
	}
	
	public void readJSONeventtype(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "eventtype");
	}
	
	public void readJSONeventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "eventunknowns");
	}
	
	public void readJSONgates(JSONObject modelElement) {
		createExtendedAttribute("gates", modelElement.optString("gates"));
	}
	
	public void readJSONgate_assignments(JSONObject modelElement) {
		createExtendedAttribute("gate_assignments", modelElement.optString("gate_assignments"));
	}
	
	public void readJSONgates_assignments(JSONObject modelElement) {
		createExtendedAttribute("gates_assignments", modelElement.optString("gates_assignments"));
	}
	
	public void readJSONgate_outgoingsequenceflow(JSONObject modelElement) {
		createExtendedAttribute("gate_outgoingsequenceflow", modelElement.optString("gate_outgoingsequenceflow"));
	}
	
	public void readJSONgates_outgoingsequenceflow(JSONObject modelElement) {
		createExtendedAttribute("gates_outgoingsequenceflow", modelElement.optString("gates_outgoingsequenceflow"));
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "gatewaytype");
	}
	
	public void readJSONimplementation(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("stencil").contains("Event")) {
			passInformationToEvent(modelElement, "implementation");
		} else if (modelElement.optString("stencil").contains("Task")) {
			passInformationToImplementation(modelElement, "implementation");
		}
	}
	
	public void readJSONimplementationunknowns(JSONObject modelElement) throws JSONException {
		passInformationToImplementation(modelElement, "implementationunknowns");
	}
		
	public void readJSONincomingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "incomingcondition");
	}
	
	public void readJSONinmessage(JSONObject modelElement) {
		createExtendedAttribute("inmessage", modelElement.optString("inmessage"));
	}
	
	public void readJSONinputmaps(JSONObject modelElement) {
	}
	
	public void readJSONinputs(JSONObject modelElement) {
		createExtendedAttribute("inputs", modelElement.optString("inputs"));
	}
	
	public void readJSONinputsets(JSONObject modelElement) {
		createExtendedAttribute("inputsets", modelElement.optString("inputsets"));
	}
	
	
	public void readJSONinstantiate(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("stencil").contains("Gateway")) {
			passInformationToRoute(modelElement, "instantiate");
		} else if (modelElement.optString("stencil").contains("Task")) {
			passInformationToImplementation(modelElement, "instantiate");
		}
	}
	
	public void readJSONintermediateeventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "intermediateeventunknowns");
	}
	
	public void readJSONiorules(JSONObject modelElement) {
		createExtendedAttribute("iorules", modelElement.optString("iorules"));
	}
	
	public void readJSONisadhoc(JSONObject modelElement) {
	}
	
	public void readJSONisatransaction(JSONObject modelElement) {
		setIsATransaction(modelElement.optString("isatransaction"));
	}
		
	public void readJSONiscompensation(JSONObject modelElement) throws JSONException {
		setIsForCompensation(modelElement.optString("iscompensation"));
	}
	
	public void readJSONlinkid(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "linkid");
	}
	
	public void readJSONloopcondition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopcondition");
	}
	
	public void readJSONloopcounter(JSONObject modelElement) throws JSONException {
		if (!modelElement.optString("looptype").equals("None")) {
			initializeLoop();
		
			JSONObject loopPassObject = new JSONObject();
			loopPassObject.put("loopcounter", modelElement.optString("loopcounter"));
			loopPassObject.put("looptype", modelElement.optString("looptype"));
		
			getLoop().parse(loopPassObject);
		}
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopmaximum");
	}
	
	public void readJSONlooptype(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "looptype");
	}
	
	public void readJSONloopunknowns(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "loopunknowns");
	}
	
	public void readJSONmarkervisible(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "markervisible");
	}
	
	public void readJSONmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_condition");
	}
	
	public void readJSONmi_flowcondition(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_flowcondition");
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "message");
	}
	
	public void readJSONmessageref(JSONObject modelElement) {
		createExtendedAttribute("messageref", modelElement.optString("messageref"));
	}
	
	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "messageunknowns");
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "mi_ordering");
	}
	
	public void readJSONmiloopunknowns(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "miloopunknowns");
	}
	
	public void readJSONnounknowns(JSONObject modelElement) throws JSONException {
		passInformationToImplementation(modelElement, "nounknowns");
	}
	
	public void readJSONoutgoingcondition(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "outgoingcondition");
	}
	
	public void readJSONoutmessage(JSONObject modelElement) {
		createExtendedAttribute("outmessage", modelElement.optString("outmessage"));
	}
	
	public void readJSONoutputs(JSONObject modelElement) {
		createExtendedAttribute("outputs", modelElement.optString("outputs"));
	}
	
	public void readJSONoutputmaps(JSONObject modelElement) {
	}
	
	public void readJSONoutputsets(JSONObject modelElement) {
		createExtendedAttribute("outputsets", modelElement.optString("outputsets"));
	}
	
	public void readJSONperformers(JSONObject modelElement) {
		createExtendedAttribute("performers", modelElement.optString("performers"));
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
		createExtendedAttribute("processref", modelElement.optString("processref"));
	}
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException {
		try{
			JSONObject properties = modelElement.optJSONObject("properties");
			properties.put("resourceId", getProperId(modelElement));
			properties.put("stencil", modelElement.optJSONObject("stencil").optString("id"));
			properties.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
			parse(properties);
		} catch (Exception e) {
			//dirty hack: Event could be MultiInstance and may have a unwanted subkey properties
		}
	}
	
	public void readJSONresult(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "result");
	}
	
	public void readJSONrouteunknowns(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "routeunknowns");
	}
	
	public void readJSONscript(JSONObject modelElement) {
		createExtendedAttribute("script", modelElement.optString("script"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "signalref");
	}
	
	public void readJSONstandardloopunknowns(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "standardloopunknowns");
	}
	
	public void readJSONstarteventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "starteventunknowns");
	}
	
	public void readJSONstartquantity(JSONObject modelElement) {
		setStartQuantity(modelElement.optString("startquantity"));
	}
	
	public void readJSONstatus(JSONObject modelElement) {
		setStatus(modelElement.optString("status"));
	}
	
	public void readJSONsubprocesstype(JSONObject modelElement) throws JSONException {
		passInformationToBlockActivity(modelElement, "subprocesstype");
	}

	public void readJSONtarget(JSONObject modelElement) {
		createExtendedAttribute("target", modelElement.optString("target"));
	}
	
	public void readJSONtaskref(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			passInformationToImplementation(modelElement, "taskref");
		}
	}
	
	public void readJSONtasktype(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			passInformationToImplementation(modelElement, "tasktype");
		}
	}
	
	public void readJSONtasktypeunknowns(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			passInformationToImplementation(modelElement, "tasktypeunknowns");
		}
	}
	
	public void readJSONtaskunknowns(JSONObject modelElement) throws JSONException {
		passInformationToImplementation(modelElement, "taskunknowns");
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToLoop(modelElement, "testtime");
	}
	
	public void readJSONtimecycle(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "timecycle");
	}
	
	public void readJSONtimedate(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "timedate");
	}
	
	public void readJSONtransaction(JSONObject modelElement) {
	}
	
	public void readJSONtrigger(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "trigger");
	}
	
	public void readJSONtriggers(JSONObject modelElement) {
		createExtendedAttribute("triggers", modelElement.optString("triggers"));
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEvent(modelElement, "triggerresultunknowns");
	}
	
	public void readJSONxortype(JSONObject modelElement) throws JSONException {
		passInformationToRoute(modelElement, "xortype");
	}
	
	public void setAssignments(XPDLAssignments assignmentsValue) {
		assignments = assignmentsValue;
	}
	
	public void setBlockActivity(XPDLBlockActivity blockActivity) {
		this.blockActivity = blockActivity;
	}

	public void setCompletionQuantity(String quantity) {
		completionQuantity = quantity;
	}
	
	public void setDocumentation(XPDLDocumentation documentationValue) {
		documentation = documentationValue;
	}
	
	public void setEvent(XPDLEvent eventValue) {
		event = eventValue;
	}
	
	public void setImplementation(XPDLImplementation implementationValue) {
		implementation = implementationValue;
	}
	
	public void setIsATransaction(String status) {
		isATransaction = status;
	}
	
	public void setIsForCompensation(String status) {
		isForCompensation = status;
	}
	
	public void setLoop(XPDLLoop loopValue) {
		loop = loopValue;
	}
	
	public void setRoute(XPDLRoute routeValue) {
		route = routeValue;
	}
	
	public void setStartQuantity(String quantity) {
		startQuantity = quantity;
	}
	
	public void setStatus(String statusValue) {
		status = statusValue;
	}
	
	public void writeJSONassignments(JSONObject modelElement) {
		XPDLAssignments assignmentsObject = getAssignments();
		if (assignmentsObject != null) {
			assignmentsObject.write(modelElement);
		}
	}
	
	public void writeJSONblockactivity(JSONObject modelElement) {
		XPDLBlockActivity block = getBlockActivity();
		if (block != null) {
			block.write(modelElement);
		}
	}
	
	public void writeJSONcompletionquantity(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "completionquantity", getCompletionQuantity());
	}
	
	public void writeJSONdefaultstencil(JSONObject modelElement) throws JSONException {
		if (!modelElement.has("stencil")) {
			writeStencil(modelElement, "Task");
		}
	}
	
	public void writeJSONdocumentation(JSONObject modelElement) throws JSONException {
		XPDLDocumentation doc = getDocumentation();
		if (doc != null) {
			initializeProperties(modelElement);
			doc.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONevent(JSONObject modelElement) {
		XPDLEvent eventObject = getEvent();
		if (eventObject != null) {
			eventObject.write(modelElement);
		}
	}
	
	public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
		XPDLImplementation implementationObject = getImplementation();
		if (implementationObject != null) {
			initializeProperties(modelElement);
			implementationObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONisatransaction(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "isatransaction", getIsATransaction());
	}
	
	public void writeJSONiscompensation(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "iscompensation", getIsForCompensation());
	}
	
	public void writeJSONloop(JSONObject modelElement) {
		XPDLLoop loopObject = getLoop();
		if (loopObject != null) {
			loopObject.write(modelElement);
		}
	}
	
	public void writeJSONroute(JSONObject modelElement) throws JSONException {
		XPDLRoute routeObject = getRoute();
		if (routeObject != null) {
			routeObject.write(modelElement);
		}
	}
	
	public void writeJSONstartquantity(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "startquantity", getStartQuantity());
	}
	
	public void writeJSONstatus(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "status", getStatus());
	}
	
	protected void createAssignment(JSONObject modelElement) throws JSONException {
		initializeAssignments();
		
		JSONObject assignmentObject = new JSONObject();
		assignmentObject.put("assigntime", modelElement.optString("assigntime"));
		assignmentObject.put("assignmentunknowns", modelElement.optString("assignmentunknowns"));
		
		XPDLAssignment newAssignment = new XPDLAssignment();
		newAssignment.parse(assignmentObject);
		
		getAssignments().add(newAssignment);
	}
	
	protected void initializeAssignments() {
		if (getAssignments() == null) {
			setAssignments(new XPDLAssignments());
		}
	}
	
	protected void initializeBlockActivity() {
		if (getBlockActivity() == null) {
			setBlockActivity(new XPDLBlockActivity());
		}
	}
	
	protected void initializeDocumentation() {
		if (getDocumentation() == null) {
			setDocumentation(new XPDLDocumentation());
		}
	}
	
	protected void initializeEvent() {
		if (getEvent() == null) {
			setEvent(new XPDLEvent());
		}
	}
	
	protected void initializeImplementation() {
		if (getImplementation() == null) {
			setImplementation(new XPDLImplementation());
		}
	}
	
	protected void initializeLoop() {
		if (getLoop() == null) {
			setLoop(new XPDLLoop());
		}
	}
	
	protected void initializeRoute() {
		if (getRoute() == null) {
			setRoute(new XPDLRoute());
		}
	}
	
	protected void passInformationToBlockActivity(JSONObject modelElement, String key) throws JSONException {
		initializeBlockActivity();
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("stencil", modelElement.optString("stencil"));
		passObject.put("id", modelElement.optString("resourceId"));
		getBlockActivity().parse(passObject);
	}
	
	protected void passInformationToDocumentation(JSONObject modelElement, String key) throws JSONException {
		initializeDocumentation();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getDocumentation().parse(passObject);
	}
	
	protected void passInformationToEvent(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Event")) {
			initializeEvent();
		
			JSONObject eventPassObject = new JSONObject();
			eventPassObject.put(key, modelElement.optString(key));
			eventPassObject.put("eventtype", modelElement.optString("eventtype"));
			eventPassObject.put("stencil", modelElement.optString("stencil"));
			eventPassObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
			
			getEvent().parse(eventPassObject);
		}
	}
	
	protected void passInformationToImplementation(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			initializeImplementation();
			
			JSONObject passObject = new JSONObject();
			passObject.put(key, modelElement.optString(key));
			passObject.put("taskref", modelElement.optString("taskref"));
			passObject.put("tasktype", modelElement.optString("tasktype"));
			passObject.put("implementation", modelElement.optString("implementation"));
			passObject.put("instantiate", modelElement.optString("instantiate"));
			
			getImplementation().parse(passObject);
		}
	}
	
	protected void passInformationToLoop(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Task")) {
			if (!modelElement.optString("looptype").equals("None")) {
				initializeLoop();
		
				JSONObject loopPassObject = new JSONObject();
				loopPassObject.put(key, modelElement.optString(key));
				loopPassObject.put("looptype", modelElement.optString("looptype"));
		
				getLoop().parse(loopPassObject);
			}
		}
	}
	
	protected void passInformationToRoute(JSONObject modelElement, String key) throws JSONException {
		if (modelElement.optString("stencil").contains("Gateway")) {
			initializeRoute();
		
			JSONObject routePassObject = new JSONObject();
			routePassObject.put(key, modelElement.optString(key));

			getRoute().parse(routePassObject);
		}
	}
}
