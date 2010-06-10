package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("StartEvent")
public class XPDLStartEvent extends XMLConvertible {

	@Attribute("Trigger")
	protected String trigger;
	@Attribute("Implementation")
	protected String implementation;
	
	@Element("TriggerConditional")
	protected XPDLTriggerConditional triggerConditional;
	@Element("TriggerResultMessage")
	protected XPDLTriggerResultMessage triggerResultMessage;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	@Element("TriggerTimer")
	protected XPDLTriggerTimer triggerTimer;
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
	}

	public XPDLTriggerConditional getTriggerConditional() {
		return triggerConditional;
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
	
	public void readJSONconditionref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("condition", modelElement.optString("condition"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		XPDLTriggerConditional condition = new XPDLTriggerConditional();
		condition.parse(passObject);
		
		setTriggerConditional(condition);
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		passInformationToTriggerResultMessage(modelElement, "message");
	}
	
	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		passInformationToTriggerResultMessage(modelElement, "messageunknowns");
	}
	
	public void readJSONstarteventunknowns(JSONObject modelElement) throws JSONException {
		readUnknowns(modelElement, "starteventunknowns");
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		XPDLTriggerResultSignal signal = new XPDLTriggerResultSignal();
		signal.parse(passObject);
		
		setTriggerResultSignal(signal);
	}
	
	public void readJSONstencil(JSONObject modelElement) {
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
		} else if (modelElement.optString("stencil").equals("StartSignalEvent")) {
			setTrigger("Signal");
		} else {
			setTrigger(trigger);
		}
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
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

	public void setTriggerTimer(XPDLTriggerTimer timer) {
		triggerTimer = timer;
	}
	
	public void setTriggerResultMessage(XPDLTriggerResultMessage message) {
		triggerResultMessage = message;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}
	
	public void writeJSONeventtype(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "eventtype", "Start");
	}
	
	public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "implementation", getImplementation());
	}
	
	public void writeJSONstarteventunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "starteventunknowns");
	}
	
	public void writeJSONtrigger(JSONObject modelElement) throws JSONException {
		String triggerValue = getTrigger();
		
		if (triggerValue != null) {
			if (triggerValue.equalsIgnoreCase("Conditional")) {
				putProperty(modelElement, "trigger", "Rule");
				appendStencil(modelElement, "ConditionalEvent");
			} else if (triggerValue.equalsIgnoreCase("Message")) {
				putProperty(modelElement, "trigger", "Message");
				appendStencil(modelElement, "MessageEvent");
			} else if (triggerValue.equalsIgnoreCase("Timer")) {
				putProperty(modelElement, "trigger", "Timer");
				appendStencil(modelElement, "TimerEvent");
			} else if (triggerValue.equalsIgnoreCase("Signal")) {
				//Yeah strange but true
				putProperty(modelElement, "trigger", "Multiple");
				appendStencil(modelElement, "SignalEvent");
			} else if (triggerValue.equalsIgnoreCase("Multiple")) {
				putProperty(modelElement, "trigger", "Multiple");
				appendStencil(modelElement, "MultipleEvent");
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
		if (getTriggerConditional() != null) {
			getTriggerConditional().write(modelElement);
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
	
	protected void initializeTriggerResultMessage() {
		if (getTriggerResultMessage() == null) {
			setTriggerResultMessage(new XPDLTriggerResultMessage());
		}
	}
	
	protected void initializeTriggerTimer() {
		if (getTriggerTimer() == null) {
			setTriggerTimer(new XPDLTriggerTimer());
		}
	}
	
	protected void passInformationToTriggerResultMessage(JSONObject modelElement, String key) throws JSONException {
		initializeTriggerResultMessage();
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		getTriggerResultMessage().parse(passObject);
	}

	protected void passInformationToTriggerTimer(JSONObject modelElement, String key) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		initializeTriggerTimer();
		getTriggerTimer().parse(passObject);
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
}
