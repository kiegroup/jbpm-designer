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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilSet;

import de.hpi.bpmn2_0.exceptions.BpmnMigrationException;
import de.hpi.bpmn2_0.exceptions.MigrationHelperException;

/**
 * @author Philipp Giese
 *
 */
public class BPMN2Migrator {

	private Diagram diagram;
	private String path;
	private MigrationHelper helper;
	
	private HashSet<Shape> activityShapes 					= new HashSet<Shape>();
	private HashSet<Shape> gatewayShapes  					= new HashSet<Shape>();
	private HashSet<Shape> swimlaneShapes 					= new HashSet<Shape>();
	private HashSet<Shape> artifactShapes					= new HashSet<Shape>();
	private HashSet<Shape> startEventShapes 				= new HashSet<Shape>();
	private HashSet<Shape> catchingIntermediateEventShapes 	= new HashSet<Shape>();
	private HashSet<Shape> throwingIntermediateEventShapes 	= new HashSet<Shape>();
	private HashSet<Shape> endEventShapes 				   	= new HashSet<Shape>();
	private HashSet<Shape> connectorShapes					= new HashSet<Shape>();
	
	private ArrayList<String> stencilSetExtensions;
	
	public BPMN2Migrator(String json) throws BpmnMigrationException {
		try {
			
			diagram = DiagramBuilder.parseJson(json, true);
			stencilSetExtensions = diagram.getSsextensions();
			
			initializeShapes(diagram.getChildShapes());
			
		} catch (JSONException e) {
			throw new BpmnMigrationException("Error while Transforming the Diagram to JSON!");
		} 
	}
	
	/**
	 * Splits up all child shapes of a given shape to their 
	 * belonging category
	 * 
	 * @param shapes
	 */
	private void initializeShapes(ArrayList<Shape> shapes) {
		for(Shape shape : shapes) {
		
			if(shape.getChildShapes().size() > 0)
				this.initializeShapes(shape.getChildShapes());
			
			String stencilId = shape.getStencilId();
			
			/*
			 * Activities:
			 * 	- Task
			 * 	- Subprocess
			 * 	- Collapsed Subprocess
			 */
			if(MigrationHelper.acitivityIds.contains(stencilId)) 
				activityShapes.add(shape);
						
			/*
			 * Gateways:
			 * 	- Exclusive Databased 
			 * 	- Eventbased 
			 * 	- Inclusive 
			 * 	- Parallel 
			 * 	- Complex
			 */
			if(MigrationHelper.gatewayIds.contains(stencilId))
				gatewayShapes.add(shape);
			
			/*
			 * Swimlanes:
			 * 	- Pool
			 * 	- Collapsed Pool
			 * 	- Lane
			 */
			if(MigrationHelper.swimlaneIds.contains(stencilId))
				swimlaneShapes.add(shape);
			
			/*
			 * Artifacts:
			 * 	- Group
			 * 	- Text Annotation
			 * 	- DataObject (only BPMN 1.1)
			 */
			if(MigrationHelper.artifactIds.contains(stencilId))
				artifactShapes.add(shape);
			
			/*
			 * StartEvents:
			 * 	- None
			 * 	- Message
			 *  - Timer
			 *  - Conditional
			 *  - Signal
			 *  - Multiple
			 */
			if(MigrationHelper.startEventIds.contains(stencilId))
				startEventShapes.add(shape);
			
			/*
			 * CatchingIntermediateEvents:
			 * 	- None
			 * 	- Message
			 * 	- Timer
			 * 	- Error
			 * 	- Cancel
			 *  - Compensation
			 *  - Conditional
			 *  - Signal
			 *  - Multiple
			 *  - Link
			 */
			if(MigrationHelper.catchingIntermediateEventIds.contains(stencilId))
				catchingIntermediateEventShapes.add(shape);
			
			/*
			 * ThrowingIntermediateEvents:
			 *  - Message
			 *  - Compensation
			 *  - Signal
			 *  - Multiple
			 *  - Link
			 */
			if(MigrationHelper.throwingIntermediateEventIds.contains(stencilId))
				throwingIntermediateEventShapes.add(shape);
			
			/*
			 * EndEvents:
			 *  - None
			 *  - Message
			 *  - Error
			 *  - Cancel
			 *  - Compensation
			 *  - Signal
			 *  - Multiple
			 *  - Terminate
			 */
			if(MigrationHelper.endEventIds.contains(stencilId))
				endEventShapes.add(shape);
			
			/*
			 * Connectors:
			 *  - Sequence Flow
			 *  - Message Flow
			 *  - Undirected Association
			 *  - Unidirectional Association
			 *  - Bidirectional Association
			 */
			if(MigrationHelper.connectorIds.contains(stencilId))
				connectorShapes.add(shape);
			
		}
	}

	/**
	 * Migrates the given Document into its representation in
	 * BPMN 2.0
	 * 
	 * @return 
	 * @throws BpmnMigrationException
	 */
	public String migrate(String stencilsetPath) throws BpmnMigrationException {

		try {
		
			path = stencilsetPath;
			
			if(diagram.getStencilset().equals("http://b3mn.org/stencilset/bpmn2.0#"))
				return JSONBuilder.parseModeltoString(diagram);
			
			/* Convert the Namespace and URL */
			StencilSet ss = new StencilSet("/oryx/stencilsets/bpmn2.0/bpmn2.0.json", "http://b3mn.org/stencilset/bpmn2.0#");
			diagram.setStencilset(ss);
				
			migrateActivities();
			migrateGateways();
			migrateSwimlanes();
			migrateArtifacts();
			migrateStartEvents();
			migrateCatchingIntermediateEvents();
			migrateThrowingIntermediateEvents();
			migrateEndEvents();
			migrateConnectors();		
			
			activateStencilSetExtensions();
			
			return JSONBuilder.parseModeltoString(diagram);		
			
		} catch(JSONException e) {
			throw new BpmnMigrationException("Error while converting the Diagram to JSON!");
		}
	}

	/**
	 * Adds the correct Stencilset Extensions
	 */
	private void activateStencilSetExtensions() {
		
		ArrayList<String> extensions = new ArrayList<String>();
		
		for(String ssextension : stencilSetExtensions) {			
			
			if(ssextension.equals("http://oryx-editor.org/stencilsets/extensions/bpmn1.1basicsubset#")) {
				extensions.add("http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#");
			} 
			
			// TODO: What happens with extensions that are currently not present in BPMN 2.0?
		}
		
		diagram.setSsextensions(extensions);
		
	}

	/**
	 * Migrates all Connectors
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateConnectors() throws BpmnMigrationException {
		
		try {
		
			for(Shape connector : connectorShapes) {
				updateProperties(connector);
			}
			
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Connectors!");
		}
	}
	
	/**
	 * Migrates all End Events
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateEndEvents() throws BpmnMigrationException {
		
		try {
		
			for(Shape endEvent : endEventShapes) {
				String stencilId = endEvent.getStencilId();
				
				/* Update obsolete StencilIds */
				if(stencilId.equals("EndEvent"))
					endEvent.getStencil().setId("EndNoneEvent");
				
				updateProperties(endEvent);
			}
			
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the End Events!");
		}
	}
	
	/**
	 * Migrates all Throwing Intermediate Events
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateThrowingIntermediateEvents() throws BpmnMigrationException {
		
		try {
		
			for(Shape throwingIntermediateEvent : throwingIntermediateEventShapes) {
				updateProperties(throwingIntermediateEvent);
			}
			
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Throwing Intermediate Events!");
		}
	}
	
	/**
	 * Migrates all Catching Intermediate Events
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateCatchingIntermediateEvents() throws BpmnMigrationException {
		
		try {
		
			for(Shape catchingIntermediateEvent : catchingIntermediateEventShapes) {
				updateProperties(catchingIntermediateEvent);
			}
		
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Catching Intermediate Events!");
		}
	}
	
	/**
	 * Migrates all Start Events
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateStartEvents() throws BpmnMigrationException {
		
		try {
		
			for(Shape startEvent : startEventShapes) {
				String stencilId = startEvent.getStencilId();
				
				/* Update obsolete StencilIds */
				if(stencilId.equals("StartEvent"))
					startEvent.getStencil().setId("StartNoneEvent");
				
				/* Update obsolete Properties */
				if(stencilId.equals("StartConditionalEvent")) {
					startEvent.getProperties().remove("trigger");
					startEvent.getProperties().put("trigger", "Conditional");
				} else if(stencilId.equals("StartSignalEvent")) {
					startEvent.getProperties().remove("trigger");
					startEvent.getProperties().put("trigger", "Signal");
				}
				
				updateProperties(startEvent);
			}
		
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Start Events!");
		}
	}
	
	/**
	 * Migrates all Artifacts
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateArtifacts() throws BpmnMigrationException {
		
		try {
		
			for(Shape artifact : artifactShapes) {
				updateProperties(artifact);
			}
			
		} catch(MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Artifacts!");
		}
	}
	
	/**
	 * Migrates all Swimlanes
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateSwimlanes() throws BpmnMigrationException {
		
		try {
		
			for(Shape swimlane : swimlaneShapes) {
				updateProperties(swimlane);	
			}
		
		} catch(MigrationHelperException e) {			
			throw new BpmnMigrationException("Error while migrating the Swimlanes");			
		}
	}
	
	/**
	 * Migrates all Gateways
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateGateways() throws BpmnMigrationException {
		
		try {
		
			for(Shape gateway : gatewayShapes) {
	
				String stencilId = gateway.getStencilId();
				
				/* Update obsolete Stencil Ids */
				if(stencilId.equals("AND_Gateway"))
					gateway.getStencil().setId("ParallelGateway");
				else if(stencilId.equals("OR_Gateway"))
					gateway.getStencil().setId("InclusiveGateway");
				else if(stencilId.equals("Exclusive_Eventbased_Gateway"))
					gateway.getStencil().setId("EventbasedGateway");
				else if(stencilId.equals("Complex_Gateway"))
					gateway.getStencil().setId("ComplexGateway");
				
				updateProperties(gateway);
			}
			
		} catch (MigrationHelperException e) {
			throw new BpmnMigrationException("Error while migrating the Gateways!");
		}			
	}
	
	/**
	 * Migrates all Activities
	 * 
	 * @throws BpmnMigrationException 
	 */
	private void migrateActivities() throws BpmnMigrationException {
		
		try {
		
			for(Shape activity : activityShapes) {				
				updateProperties(activity);
				
				HashMap<String, String> props = activity.getProperties();			
				String id = activity.getStencilId();			
				
				/* Map MI-Ordering to LoopType */			
				if(props.containsKey("looptype")) {
					/* replace old value with new one */
					if(activity.getProperty("looptype").equals("MultiInstance")) {
						props.remove("looptype");
						props.put("looptype", activity.getProperty("mi_ordering"));
					}
				}
				
				/* Map isCompensation to isforcompensation */			
				if(props.containsKey("iscompensation")) {
					/* replace */
					props.put("isforcompensation", activity.getProperty("iscompensation"));
					props.remove("iscompensation");
				}
				
				
				if(id.equals("Task")) {
					String taskType = activity.getProperty("tasktype");
					
					if(taskType.equals("Reference"))
						props.put("tasktype", "None");
					else
						props.put("tasktype", activity.getProperty("tasktype"));
					
				}
				
				// TODO: remove obsolete Properties
				
				/* Remove depreciated Attrs */
				props.remove("mi_ordering");
			}
		
		} catch (MigrationHelperException e) {			
			throw new BpmnMigrationException("Error while migrating the Activities!");			
		}
	}	
	
	/**
	 * Adds all properties that are new in BPMN 2.0
	 * 
	 * @param shape
	 * @return 
	 * @throws MigrationHelperException 
	 */
	private void updateProperties(Shape shape) throws MigrationHelperException {
		if (this.helper == null)
			this.helper = new MigrationHelper(path);
		
		HashMap<String, String> properties = this.helper.getProperties(shape.getStencilId());
		
		/* find the properties and add them */
		for(String property : properties.keySet()) {
			if(shape.getProperty(property.toLowerCase()) == null) {
				shape.getProperties().put(property.toLowerCase(), properties.get(property));
			}
		}
	}
}