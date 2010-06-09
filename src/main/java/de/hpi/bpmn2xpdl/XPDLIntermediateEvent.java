package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("IntermediateEvent")
public class XPDLIntermediateEvent extends XMLConvertible {
	
	@Attribute("Trigger")
	protected String trigger;
	@Attribute("Implementation")
	protected String implementation;
	
	@Element("ResultError")
	protected XPDLResultError resultError;
	@Element("TriggerResultCompensation")
	protected XPDLTriggerResultCompensation triggerResultCompensation;
	@Element("TriggerConditional")
	protected XPDLTriggerConditional triggerConditional;
	@Element("TriggerResultMessage")
	protected XPDLTriggerResultMessage triggerResultMessage;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	@Element("TriggerTimer")
	protected XPDLTriggerTimer triggerTimer;
	@Element("TriggerResultLink")
	protected XPDLTriggerResultLink triggerResultLink;

	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public XPDLTriggerResultCompensation getTriggerResultCompensation() {
		return triggerResultCompensation;
	}
	
	public XPDLTriggerConditional getTriggerConditional() {
		return triggerConditional;
	}

	public XPDLTriggerResultLink getTriggerResultLink() {
		return triggerResultLink;
	}
	
	public XPDLTriggerResultMessage getTriggerResultMessage() {
		return triggerResultMessage;
	}
	
	public XPDLTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}
	
	public XPDLTriggerTimer getTriggerTimer() {
		return triggerTimer;
	}
	
	public XPDLResultError getResultError() {
		return resultError;
	}
	
	public void readJSONactivity(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("activity", modelElement.optString("activity"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerResultCompensation();
		getTriggerResultCompensation().parse(passObject);
	}
	
	public void readJSONcondition(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("condition", modelElement.optString("condition"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerConditional();
		getTriggerConditional().parse(passObject);
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		JSONObject errorObject = new JSONObject();
		errorObject.put("errorcode", modelElement.optString("errorcode"));
		errorObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeResultError();
		getResultError().parse(errorObject);
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONintermediateeventunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "intermediateeventunknowns");
	}
	
	public void readJSONlinkid(JSONObject modelElement) throws JSONException {
		JSONObject linkObject = new JSONObject();
		linkObject.put("linkid", modelElement.optString("linkid"));
		linkObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerResultLink();
		getTriggerResultLink().parse(linkObject);
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("message", modelElement.optString("message"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerResultMessage();
		getTriggerResultMessage().parse(passObject);
	}
	
	public void readJSONstencil(JSONObject modelElement) {
		String stencil = modelElement.optString("stencil");
		String catchThrow = determineCatchThrow(stencil);
		
		if (stencil.equals("IntermediateMessageEventThrowing") || stencil.equals("IntermediateMessageEventCatching")) {
			initializeTriggerResultMessage();
			getTriggerResultMessage().setCatchThrow(catchThrow);
		} else if (stencil.equals("IntermediateSignalEventThrowing") || stencil.equals("IntermediateSignalEventCatching")) {
			initializeTriggerResultSignal();
			getTriggerResultSignal().setCatchThrow(catchThrow);
		} else if (stencil.equals("IntermediateLinkEventCatching") || stencil.equals("IntermediateLinkEventThrowing")) {
			initializeTriggerResultLink();
			getTriggerResultLink().setCatchThrow(catchThrow);
		} else if (stencil.equals("IntermediateCompensationEventCatching") || stencil.equals("IntermediateCompensationEventThrowing")) {
			initializeTriggerResultCompensation();
			getTriggerResultCompensation().setCatchThrow(catchThrow);
		}
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerResultSignal();
		getTriggerResultSignal().parse(passObject);
	}	
	
	public void readJSONtimecycle(JSONObject modelElement) throws JSONException {
		passInformationToTriggerTimer(modelElement, "timecycle");
	}
	
	public void readJSONtimedate(JSONObject modelElement) throws JSONException {
		passInformationToTriggerTimer(modelElement, "timedate");
	}
	
	public void readJSONtrigger(JSONObject modelElement) {
		String trigger = modelElement.optString("trigger");
		if (trigger.equals("Rule")) {
			setTrigger("Conditional");
		} else {
			setTrigger(trigger);
		}
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
	}
	
	public void setResultError(XPDLResultError error) {
		resultError = error;
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setTrigger(String triggerValue) {
		trigger = triggerValue;
	}
	
	public void setTriggerConditional(XPDLTriggerConditional triggerConditional) {
		this.triggerConditional = triggerConditional;
	}
	
	public void setTriggerResultCompensation(XPDLTriggerResultCompensation triggerResultCompensation) {
		this.triggerResultCompensation = triggerResultCompensation;
	}

	public void setTriggerResultLink(XPDLTriggerResultLink link) {
		triggerResultLink = link;
	}
	
	public void setTriggerResultMessage(XPDLTriggerResultMessage message) {
		triggerResultMessage = message;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}
	
	public void setTriggerTimer(XPDLTriggerTimer timer) {
		triggerTimer = timer;
	}
	
	public void writeJSONeventtype(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "eventtype", "Intermediate");
	}
	
	public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "implementation", getImplementation());
	}
	
	public void writeJSONintermediateeventunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "intermediateeventunknowns");
	}
	
	public void writeJSONtrigger(JSONObject modelElement) throws JSONException {
		String triggerValue = getTrigger();
		
		if (triggerValue != null) {
			if (triggerValue.equalsIgnoreCase("Message")) {
				writeMessageProperties(modelElement);
			} else if (triggerValue.equalsIgnoreCase("Timer")) {
				putProperty(modelElement, "trigger", "Timer");
				appendStencil(modelElement, "TimerEvent");
			} else if (triggerValue.equalsIgnoreCase("Error")) {
				putProperty(modelElement, "trigger", "Error");
				appendStencil(modelElement, "ErrorEvent");
			} else if (triggerValue.equalsIgnoreCase("Cancel")) {
				putProperty(modelElement, "trigger", "Cancel");
				appendStencil(modelElement, "CancelEvent");
			} else if (triggerValue.equalsIgnoreCase("Compensation")) {
				writeCompensationProperties(modelElement);
			} else if (triggerValue.equalsIgnoreCase("Conditional")) {
				putProperty(modelElement, "trigger", "Rule");
				appendStencil(modelElement, "ConditionalEvent");				
			} else if (triggerValue.equalsIgnoreCase("Signal")) {
				writeSignalProperties(modelElement);
			} else if (triggerValue.equalsIgnoreCase("Multiple")) {
				writeMultipleProperties(modelElement);
			} else if(triggerValue.equalsIgnoreCase("Link")) {
				writeLinkProperties(modelElement);
			} else {
				putProperty(modelElement, "trigger", "None");
				appendStencil(modelElement, "Event");
			}
		} else {
			putProperty(modelElement, "trigger", "None");
			appendStencil(modelElement, "Event");
		}
	}
	
	public void writeJSONtriggerObjects(JSONObject modelElement) throws JSONException {
		if (getResultError() != null) {
			getResultError().write(modelElement);
		} else if (getTriggerConditional() != null) {
			getTriggerConditional().write(modelElement);
		} else if (getTriggerResultCompensation() != null) {
			getTriggerResultCompensation().write(modelElement);
		} else if (getTriggerResultLink() != null) {
			getTriggerResultLink().write(modelElement);
		} else if (getTriggerResultMessage() != null) {
			getTriggerResultMessage().write(modelElement);
		} else if (getTriggerResultSignal() != null) {
			getTriggerResultSignal().write(modelElement);
		} else if (getTriggerTimer() != null) {
			getTriggerTimer().write(modelElement);
		}
	}
	
	protected void appendStencil(JSONObject modelElement, String appendix) throws JSONException {
		String newStencil = modelElement.optJSONObject("stencil").optString("id") + appendix;
		
		JSONObject stencil = new JSONObject();
		stencil.put("id", newStencil);
		modelElement.put("stencil", stencil);
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected String determineCatchThrow(String stencil) {
		if (stencil.contains("Catching")) {
			return "CATCH";
		} else {
			return "THROW";
		}
	}
	
	protected void passInformationToTriggerTimer(JSONObject modelElement, String key) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		initializeTriggerTimer();
		getTriggerTimer().parse(passObject);
	}
	
	protected void initializeResultError() {
		if (getResultError() == null) {
			setResultError(new XPDLResultError());
		}
	}
	
	protected void initializeTriggerConditional() {
		if (getTriggerConditional() == null) {
			setTriggerConditional(new XPDLTriggerConditional());
		}
	}
	
	protected void initializeTriggerResultCompensation() {
		if (getTriggerResultCompensation() == null) {
			setTriggerResultCompensation(new XPDLTriggerResultCompensation());
		}
	}
	
	protected void initializeTriggerResultLink() {
		if (getTriggerResultLink() == null) {
			setTriggerResultLink(new XPDLTriggerResultLink());
		}
	}
	
	protected void initializeTriggerResultMessage() {
		if (getTriggerResultMessage() == null) {
			setTriggerResultMessage(new XPDLTriggerResultMessage());
		}
	}
	
	protected void initializeTriggerResultSignal() {
		if (getTriggerResultSignal() == null) {
			setTriggerResultSignal(new XPDLTriggerResultSignal());
		}
	}
	
	protected void initializeTriggerTimer() {
		if (getTriggerTimer() == null) {
			setTriggerTimer(new XPDLTriggerTimer());
		}
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
	
	protected void writeCompensationProperties(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "trigger", "Compensation");
		
		XPDLTriggerResultCompensation compensationObject = getTriggerResultCompensation();
		if (compensationObject != null) {
			if (compensationObject.getCatchThrow() != null) {
				if (compensationObject.getCatchThrow().equalsIgnoreCase("CATCH")) {
					appendStencil(modelElement, "CompensationEventCatching");
				} else {
					appendStencil(modelElement, "CompensationEventThrowing");
				}
			} else {
				appendStencil(modelElement, "CompensationEventThrowing");
			}
		} else {
			appendStencil(modelElement, "CompensationEventThrowing");
		}
	}
	
	protected void writeLinkProperties(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "trigger", "Link");
		
		XPDLTriggerResultLink linkObject = getTriggerResultLink();
		if (linkObject != null) {
			if (linkObject.getCatchThrow() != null) {
				if (linkObject.getCatchThrow().equalsIgnoreCase("CATCH")) {
					appendStencil(modelElement, "LinkEventCatching");
				} else {
					appendStencil(modelElement, "LinkEventThrowing");
				}
			} else {
				appendStencil(modelElement, "LinkEventThrowing");
			}
		} else {
			appendStencil(modelElement, "LinkEventThrowing");
		}
	}
	
	protected void writeMessageProperties(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "trigger", "Message");
		
		XPDLTriggerResultMessage messageObject = getTriggerResultMessage();
		if (messageObject != null) {
			if (messageObject.getCatchThrow() != null) {
				if (messageObject.getCatchThrow().equalsIgnoreCase("CATCH")) {
					appendStencil(modelElement, "MessageEventCatching");
				} else {
					appendStencil(modelElement, "MessageEventThrowing");
				}
			} else {
				appendStencil(modelElement, "MessageEventThrowing");
			}
		} else {
			appendStencil(modelElement, "MessageEventThrowing");
		}
	}
	
	protected void writeMultipleProperties(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "trigger", "Multiple");
		//Better stuff around here needed
		appendStencil(modelElement, "MultipleEventThrowing");
	}
	
	protected void writeSignalProperties(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "trigger", "Signal");
		
		XPDLTriggerResultSignal signalObject = getTriggerResultSignal();
		if (signalObject != null) {
			if (signalObject.getCatchThrow() != null) {
				if (signalObject.getCatchThrow().equalsIgnoreCase("CATCH")) {
					appendStencil(modelElement, "SignalEventCatching");
				} else {
					appendStencil(modelElement, "SignalEventThrowing");
				}
			} else {
				appendStencil(modelElement, "SignalEventThrowing");
			}
		} else {
			appendStencil(modelElement, "SignalEventThrowing");
		}
	}
}
