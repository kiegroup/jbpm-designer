package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Event")
public class XPDLEvent extends XMLConvertible {
	
	@Element("EndEvent")
	protected XPDLEndEvent endEvent;
	@Element("IntermediateEvent")
	protected XPDLIntermediateEvent intermediateEvent;
	@Element("StartEvent")
	protected XPDLStartEvent startEvent;

	public XPDLEndEvent getEndEvent() {
		return endEvent;
	}

	public XPDLIntermediateEvent getIntermediateEvent() {
		return intermediateEvent;
	}

	public XPDLStartEvent getStartEvent() {
		return startEvent;
	}
	
	public void readJSONactivity(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "activity");
		}
	}
	
	public void readJSONactivityref(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "activityref");
		}
	}

	public void readJSONcondition(JSONObject modelElement) throws JSONException {
		passInformationToIntermediateEvent(modelElement, "condition");
	}
	
	public void readJSONconditionref(JSONObject modelElement) throws JSONException {
		passInformationToStartEvent(modelElement, "conditionref");
	}
	
	public void readJSONendeventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToEndEvent(modelElement, "endeventunknowns");
	}
	
	public void readJSONerrorcode(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "errorcode");
		} else if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "errorcode");
		}
	}
	
	public void readJSONeventtype(JSONObject modelElement) {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			initializeEndEvent();
		} else if (typeOfEvent.equals("Intermediate")) {
			initializeIntermediateEvent();
		} else if (typeOfEvent.equals("Start")) {
			initializeStartEvent();
		}
	}
	
	public void readJSONeventunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "eventunknowns");
	}
	
	public void readJSONimplementation(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "implementation");
		} else if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "implementation");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "implementation");
		}
	}
	
	public void readJSONintermediateeventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToIntermediateEvent(modelElement, "intermediateeventunknowns");
	}
	
	public void readJSONlinkid(JSONObject modelElement) throws JSONException {
		passInformationToIntermediateEvent(modelElement, "linkid");
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "message");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "message");
		} else if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "message");
		}
	}
	
	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "messageunknowns");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "messageunknowns");
		} else if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "messageunknowns");
		}
	}
	
	public void readJSONresult(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "result");
		} else if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "result");
		}
	}
	
	public void readJSONsignalref(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "signalref");
		} else if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "signalref");
		} else if (typeOfEvent.equals("End")) {
			passInformationToEndEvent(modelElement, "signalref");
		}
	}
	
	public void readJSONstarteventunknowns(JSONObject modelElement) throws JSONException {
		passInformationToStartEvent(modelElement, "starteventunknowns");
	}
	
	public void readJSONstencil(JSONObject modelElement) {
	}
	
	public void readJSONtimecycle(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "timecycle");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "timecycle");
		}
	}
	
	public void readJSONtimedate(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "timedate");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "timedate");
		}
	}
	
	public void readJSONtrigger(JSONObject modelElement) throws JSONException {
		String typeOfEvent = modelElement.optString("eventtype");
		if (typeOfEvent.equals("Intermediate")) {
			passInformationToIntermediateEvent(modelElement, "trigger");
		} else if (typeOfEvent.equals("Start")) {
			passInformationToStartEvent(modelElement, "trigger");
		}
	}
	
	public void readJSONtriggerresultunknowns(JSONObject modelElement) {
	}
	
	public void setEndEvent(XPDLEndEvent endEvent) {
		this.endEvent = endEvent;
	}

	public void setIntermediateEvent(XPDLIntermediateEvent intermediateEvent) {
		this.intermediateEvent = intermediateEvent;
	}

	public void setStartEvent(XPDLStartEvent startEvent) {
		this.startEvent = startEvent;
	}
	
	public void writeJSONendevent(JSONObject modelElement) throws JSONException {
		XPDLEndEvent endEventObject = getEndEvent();
		if (endEventObject != null) {
			writeStencil(modelElement, "End");
			endEventObject.write(modelElement);
		}
	}
	
	public void writeJSONeventunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "eventunknowns");
	}
	
	public void writeJSONintermediateevent(JSONObject modelElement) throws JSONException {
		XPDLIntermediateEvent intermediateEventObject = getIntermediateEvent();
		if (intermediateEventObject != null) {
			writeStencil(modelElement, "Intermediate");
			intermediateEventObject.write(modelElement);
		}
	}
	
	public void writeJSONstartevent(JSONObject modelElement) throws JSONException {
		XPDLStartEvent startEventObject = getStartEvent();
		if (startEventObject != null) {
			writeStencil(modelElement, "Start");
			startEventObject.write(modelElement);
		}
	}
	
	protected void initializeEndEvent() {
		if (getEndEvent() ==  null) {
			setEndEvent(new XPDLEndEvent());
		}
	}
	
	protected void initializeIntermediateEvent() {
		if (getIntermediateEvent() ==  null) {
			setIntermediateEvent(new XPDLIntermediateEvent());
		}
	}
	
	protected void initializeStartEvent() {
		if (getStartEvent() ==  null) {
			setStartEvent(new XPDLStartEvent());
		}
	}
	
	protected void passInformationToEndEvent(JSONObject modelElement, String key) throws JSONException {
		initializeEndEvent();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		getEndEvent().parse(passObject);
	}
	
	protected void passInformationToIntermediateEvent(JSONObject modelElement, String key) throws JSONException {
		initializeIntermediateEvent();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("stencil", modelElement.optString("stencil"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		getIntermediateEvent().parse(passObject);
	}
	
	protected void passInformationToStartEvent(JSONObject modelElement, String key) throws JSONException {
		initializeStartEvent();
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		passObject.put("stencil", modelElement.optString("stencil"));
		passObject.put("triggerresultunknowns", modelElement.optString("triggerresultunknowns"));
		
		getStartEvent().parse(passObject);
	}
	
	protected void writeStencil(JSONObject modelElement, String stencil) throws JSONException {
		JSONObject stencilObject = new JSONObject();
		stencilObject.put("id", stencil);
		
		modelElement.put("stencil", stencilObject);
	}
}
