/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.bpmn2_0.exceptions.MigrationHelperException;


/**
 * @author Philipp Giese
 * This is a helper class for migrating the BPMN1.1 attributes into 
 * the new structure of BPMN2.0
 */

public class MigrationHelper {
	
	/* List of BPMN 1.1 Acitivities
	 * <-- */
	private static final String[] activitiesArray = {
		"Task",
		"CollapsedSubprocess",
		"Subprocess"
	};
	
	public static final HashSet<String> acitivityIds = new HashSet<String>(
			Arrays.asList(activitiesArray));	
	/* --> */
	
	/* List of BPMN 1.1 Gateways
	 * <-- */
	private static final String[] gatewaysArray = {
		"Exclusive_Databased_Gateway",
		"Exclusive_Eventbased_Gateway",
		"AND_Gateway",
		"OR_Gateway",
		"Complex_Gateway"
	};
	
	public static final HashSet<String> gatewayIds = new HashSet<String>(
			Arrays.asList(gatewaysArray));
	/* --> */
	
	/* List of BPMN 1.1 Swimlanes
	 * <-- */
	private static final String[] swimlaneArray = {
		"Pool",
		"CollapsedPool",
		"Lane"
	};
	
	public static final HashSet<String> swimlaneIds = new HashSet<String>(
			Arrays.asList(swimlaneArray));
	/* --> */
	
	/* List of BPMN 1.1 Artifacts
	 * <-- */
	private static final String[] artifactsArray = {
		"Group",
		"TextAnnotation",
		"DataObject"
	};
	
	public static final HashSet<String> artifactIds = new HashSet<String>(
			Arrays.asList(artifactsArray));
	/* --> */
	
	/* List of BPMN 1.1 StartEvents
	 * <-- */
	private static final String[] startEventsArray = {
		"StartEvent",
		"StartMessageEvent",
		"StartTimerEvent",
		"StartConditionalEvent",
		"StartSignalEvent",
		"StartMultipleEvent"
	};
	
	public static final HashSet<String> startEventIds = new HashSet<String>(
			Arrays.asList(startEventsArray));
	/* --> */
	
	/* List of BPMN 1.1 CatchingIntermediateEvents
	 * <-- */
	private static final String[] catchingIntermediateEventsArray = {
		"IntermediateEvent",
		"IntermediateMessageEventCatching",
		"IntermediateTimerEvent",
		"IntermediateErrorEvent",
		"IntermediateCancelEvent",
		"IntermediateCompensationEventCatching",
		"IntermediateConditionalEvent",
		"IntermediateSignalEventCatching",
		"IntermediateMultipleEventCatching",
		"IntermediateLinkEventCatching"
	};
	
	public static final HashSet<String> catchingIntermediateEventIds = new HashSet<String>(
			Arrays.asList(catchingIntermediateEventsArray));
	/* --> */
	
	/* List of BPMN 1.1 ThrowingIntermediateEvents
	 * <-- */
	private static final String[] throwingIntermediateEventsArray = {
		"IntermediateMessageEventThrowing",
		"IntermediateCompensationEventThrowing",
		"IntermediateSignalEventThrowing",
		"IntermediateMultipleEventThrowing",
		"IntermediateLinkEventThrowing"
	};
	
	public static final HashSet<String> throwingIntermediateEventIds = new HashSet<String>(
			Arrays.asList(throwingIntermediateEventsArray));
	/* --> */
	
	/* List of BPMN 1.1 EndEvents
	 * <-- */
	private static final String[] endEventsArray = {
		"EndEvent",
		"EndMessageEvent",
		"EndErrorEvent",
		"EndCancelEvent",
		"EndCompensationEvent",
		"EndSignalEvent",
		"EndMultipleEvent",
		"EndTerminateEvent"
	};
	
	public static final HashSet<String> endEventIds = new HashSet<String>(
			Arrays.asList(endEventsArray));
	/* --> */
	
	/* List of BPMN 1.1 Edges
	 * <-- */
	private static final String[] connectorArray = {
		"SequenceFlow",
		"MessageFlow",
		"Association_Undirected",
		"Association_Unidirectional",
		"Association_Bidirectional"
	};
	
	public static final HashSet<String> connectorIds = new HashSet<String>(
			Arrays.asList(connectorArray));
	/* --> */
	
	/* SencilID => PropertyPackage */
	private HashMap<String, HashSet<String>> packagesForStencil;

	/* StencilID => Properties */
	private HashMap<String, HashSet<String>> propertiesForStencil;
	
	/* PropertyPackageID => Properties */
	private HashMap<String, HashSet<String>> propertiesForPackage;
	
	/* PropertyID => { PropertyType => DefaultValue } */
	private HashMap<String, String> defaultsForProperty;
	
	
	public MigrationHelper(String path) throws MigrationHelperException {
	
		this.packagesForStencil 	= new HashMap<String, HashSet<String>>();
		this.propertiesForPackage 	= new HashMap<String, HashSet<String>>();
		this.propertiesForStencil 	= new HashMap<String, HashSet<String>>();
		this.defaultsForProperty 	= new HashMap<String, String>();
	
		try {
		
			File json = new File(path + "/bpmn2.0.json");
			BufferedReader br = new BufferedReader(new FileReader(json));
			StringBuffer bpmnJson = new StringBuffer();
			String line;
					
			while((line = br.readLine()) != null) {
				/*
				 * Comments are not defined in json (referr to json.org)
				 * so i'm stripping every line that looks like a comment
				 */			
				if(!(line.matches("\t*//(.*)") || line.matches("\t*/\\*(.*)\\*/")))	
					bpmnJson.append(line);
			}
					
			JSONObject jsonObject 		= new JSONObject(bpmnJson.toString());		
			JSONArray propertyPackages 	= jsonObject.getJSONArray("propertyPackages");
			JSONArray stencils			= jsonObject.getJSONArray("stencils");
			
			this.parsePropertyPackages(propertyPackages);
			this.parseStencils(stencils);
			
		} catch(Exception e) {
			throw new MigrationHelperException("Error while preparing the data!");
		}
	}
	
	public HashMap<String, String> getProperties(String key) {
		HashSet<String> propertyPackages = this.packagesForStencil.get(key);
		HashMap<String, String> properties = new HashMap<String, String>();
		
		/* find all properties that are associated via propertyPackages */
		if(propertyPackages != null) 
			for(String propertyPackage : propertyPackages) 
				if(this.propertiesForPackage.containsKey(propertyPackage)) 
					for(String property : this.propertiesForPackage.get(propertyPackage)) 
						properties.put(property, this.defaultsForProperty.get(propertyPackage + property));
			
		/* find all properties that are declared at the stencil */
		if(this.propertiesForStencil.containsKey(key)) 
			for(String property : this.propertiesForStencil.get(key)) 
				properties.put(property, this.defaultsForProperty.get(key + property));

		return properties;
	}
	
	/**
	 * Parses the StencilSet for Stencils and enlists them with their
	 * belonging propertyPackages and properties 
	 * 
	 * @param stencils
	 * @throws JSONException
	 */
	private void parseStencils(JSONArray stencils) throws JSONException {
		if(stencils != null) {
			for(int i = 0; i < stencils.length(); i++) {
				JSONObject currentStencil = stencils.getJSONObject(i);
				
				/* find all propertyPackages */
				JSONArray pp = currentStencil.optJSONArray("propertyPackages");				
				HashSet<String> propertyPackages = new HashSet<String>();
				
				if(pp != null) 
					for(int j = 0; j < pp.length(); j++) 										
						propertyPackages.add(pp.getString(j));
				
				this.packagesForStencil.put(currentStencil.getString("id"), propertyPackages);
				
				/* find all extra properties */
				JSONArray props = currentStencil.optJSONArray("properties");
				HashSet<String> properties = new HashSet<String>();
				
				if(props != null) 
					for(int j = 0; j < props.length(); j++) {
						JSONObject currentProperty = props.getJSONObject(j);
						
						properties.add(currentProperty.getString("id"));
						
						this.setDefinitionsForProperty(currentStencil.getString("id"), currentProperty);
					}
				
				this.propertiesForStencil.put(currentStencil.getString("id"), properties);
			}
		}
	}
	
	/**
	 * Parses the StencilSet for PropertyPackages and enlists them
	 * in propertyPackages
	 * 
	 * @param propertyPackages
	 * @throws JSONException
	 */
	private void parsePropertyPackages(JSONArray propertyPackages) throws JSONException {
		if(propertyPackages != null) {
			for(int i = 0; i < propertyPackages.length(); i++) {
				JSONObject currentPackage = propertyPackages.getJSONObject(i);
				
				String packageName = (String) currentPackage.get("name");
				HashSet<String> propertyIds = new HashSet<String>();
				
				JSONArray properties = currentPackage.optJSONArray("properties");
				
				for(int j = 0; j < properties.length(); j++) {
					JSONObject currentProperty = properties.getJSONObject(j);
					
					propertyIds.add(currentProperty.getString("id"));
					
					this.setDefinitionsForProperty(packageName, currentProperty);
				}
				
				this.propertiesForPackage.put(packageName, propertyIds);
			}
		}
	}
	
	/**
	 * Associates the properties with their types and initial values
	 * 
	 * @param prefix
	 * @param property
	 * @throws JSONException
	 */
	private void setDefinitionsForProperty(String prefix, JSONObject property) throws JSONException {
		String id   = property.getString("id");			
		String type = property.getString("type");
		
		Object value;
		
		if(type.equals("Boolean"))
			value = property.optBoolean("value");
		else		
			value = property.optString("value");
						
		if(!this.defaultsForProperty.containsKey(id))
			this.defaultsForProperty.put(prefix+id, value.toString());
	}
}
