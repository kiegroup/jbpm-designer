package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("EndEvent")
public class XPDLEndEvent extends XMLConvertible {
	
	@Attribute("Result")
	protected String result;
	@Attribute("Implementation")
	protected String implementation;
	
	@Element("TriggerResultMessage")
	protected XPDLTriggerResultMessage triggerResultMessage;
	@Element ("TriggerResultCompensation")
	protected XPDLTriggerResultCompensation triggerResultCompensation;
	@Element("ResultError")
	protected XPDLResultError resultError;
	@Element("TriggerResultSignal")
	protected XPDLTriggerResultSignal triggerResultSignal;
	
	public String getImplementation() {
		return implementation;
	}
	
	public String getResult() {
		return result;
	}
	
	public XPDLResultError getResultError() {
		return resultError;
	}
	
	public XPDLTriggerResultCompensation getTriggerResultCompensation() {
		return triggerResultCompensation;
	}
	
	public XPDLTriggerResultMessage getTriggerResultMessage() {
		return triggerResultMessage;
	}
	
	public XPDLTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}
	
	public void readJSONactivityref(JSONObject modelElement) throws JSONException {
		setTriggerResultCompensation(new XPDLTriggerResultCompensation());
		
		JSONObject passObject = new JSONObject();
		passObject.put("activity", modelElement.optString("activityref"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		getTriggerResultCompensation().parse(passObject);
	}
	
	public void readJSONendeventunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "endeventunknowns");
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		JSONObject errorObject = new JSONObject();
		errorObject.put("errorcode", modelElement.optString("errorcode"));
		errorObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		XPDLResultError error = new XPDLResultError();
		error.parse(errorObject);
		
		setResultError(error);
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
	
	public void readJSONresult(JSONObject modelElement) {
		setResult(modelElement.optString("result"));
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("signalref", modelElement.optString("signalref"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		XPDLTriggerResultSignal signal = new XPDLTriggerResultSignal();
		signal.parse(passObject);
		
		setTriggerResultSignal(signal);
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setResult(String resultValue) {
		result = resultValue;
	}
	
	public void setResultError(XPDLResultError error) {
		resultError = error;
	}
	
	public void setTriggerResultCompensation(XPDLTriggerResultCompensation trigger) {
		this.triggerResultCompensation = trigger;
	}
	
	public void setTriggerResultMessage(XPDLTriggerResultMessage triggerResultMessage) {
		this.triggerResultMessage = triggerResultMessage;
	}
	
	public void setTriggerResultSignal(XPDLTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}
	
	public void writeJSONendeventunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "endeventunknowns");
	}
	
	public void writeJSONeventtype(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "eventtype", "End");
	}
	
	public void writeJSONimplementation(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "implementation", getImplementation());
	}
	
	public void writeJSONresult(JSONObject modelElement) throws JSONException {
		String resultValue = getResult();
		
		if (resultValue != null) {
			if (resultValue.equalsIgnoreCase("Message")) {
				putProperty(modelElement, "result", "Message");
				appendStencil(modelElement, "MessageEvent");
			} else if (resultValue.equalsIgnoreCase("Error")) {
				putProperty(modelElement, "result", "Error");
				appendStencil(modelElement, "ErrorEvent");
			} else if (resultValue.equalsIgnoreCase("Cancel")) {
				putProperty(modelElement, "result", "Cancel");
				appendStencil(modelElement, "CancelEvent");
			} else if (resultValue.equalsIgnoreCase("Compensation")) {
				putProperty(modelElement, "result", "Compensation");
				appendStencil(modelElement, "CompensationEvent");
			} else if (resultValue.equalsIgnoreCase("Signal")) {
				putProperty(modelElement, "result", "Signal");
				appendStencil(modelElement, "SignalEvent");
			} else if (resultValue.equalsIgnoreCase("Multiple")) {
				putProperty(modelElement, "result", "Multiple");
				appendStencil(modelElement, "MultipleEvent");
			} else if (resultValue.equalsIgnoreCase("Terminate")) {
				putProperty(modelElement, "result", "Terminate");
				appendStencil(modelElement, "TerminateEvent");
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
		if (getTriggerResultCompensation() != null) {
			getTriggerResultCompensation().write(modelElement);
		} else if (getTriggerResultMessage() != null) {
			getTriggerResultMessage().write(modelElement);
		} else if (getTriggerResultSignal() != null) {
			getTriggerResultSignal().write(modelElement);
		} else if (getResultError() != null) {
			getResultError().write(modelElement);
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
	
	protected void passInformationToTriggerResultMessage(JSONObject modelElement, String key) throws JSONException {
		initializeTriggerResultMessage();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		getTriggerResultMessage().parse(passObject);
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
}
