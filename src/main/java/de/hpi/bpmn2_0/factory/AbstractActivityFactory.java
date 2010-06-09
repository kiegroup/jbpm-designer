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
package de.hpi.bpmn2_0.factory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.loop.ComplexBehaviorDefinition;
import de.hpi.bpmn2_0.model.activity.loop.LoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceFlowCondition;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.StandardLoopCharacteristics;
import de.hpi.bpmn2_0.model.data_object.DataState;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.model.event.ImplicitThrowEvent;
import de.hpi.bpmn2_0.model.misc.IoOption;
import de.hpi.bpmn2_0.model.misc.ItemKind;
import de.hpi.bpmn2_0.model.misc.Property;

/**
 * Common factory for BPMN 2.0 activities
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public abstract class AbstractActivityFactory extends AbstractBpmnFactory {

	/**
	 * Sets common attributes of activity (task, subprocess, event-subprocess)
	 * derived from the source shape.
	 * 
	 * @param activity
	 *            The resulting activity.
	 * @param shape
	 *            The source diagram shape.
	 */
	protected void setStandardAttributes(Activity activity, Shape shape) {
		
		/* Handle isCompensation Property */
		this.setCompensationProperty(activity, shape);
		
		/* Loop Characteristics */
		this.createLoopCharacteristics(activity, shape);
		
		/* Properties */
		activity.getProperty().addAll(this.createPropertiesList(shape));
		
		/* Start and Completion Quantity */
		this.setStartAndCompletionQuantity(activity, shape);
		
		/* Collect data for IOSpecification */
		this.collectIoSpecificationInfo(activity, shape);
		
	}

	/**
	 * Takes the complex property input set as well as output set and stores 
	 * them in a hash map for further processing.
	 * @param shape 
	 * @param activity 
	 */
	private void collectIoSpecificationInfo(Activity activity, Shape shape) {
		activity.getOutputSetInfo().add(this.collectSetInfoFor(shape, "dataoutputset"));
		activity.getInputSetInfo().add(this.collectSetInfoFor(shape, "datainputset"));
	}
	
	/**
	 * Generic method to parse data input and output set properties.
	 * 
	 * @param property
	 * 		Identifies the shape's property to handle either a output or input set.
	 */
	private HashMap<String, IoOption> collectSetInfoFor(Shape shape, String property) {
		String ioSpecString = shape.getProperty(property);
		
		HashMap<String, IoOption> options = new HashMap<String, IoOption>();
		
		if(ioSpecString != null && !ioSpecString.isEmpty()) {
			try {
				JSONObject ioSpecObject = new JSONObject(ioSpecString);
				JSONArray ioSpecItems = ioSpecObject.getJSONArray("items");

				/* Retrieve io spec option definitions */
				for (int i = 0; i < ioSpecItems.length(); i++) {
					JSONObject propertyItem = ioSpecItems.getJSONObject(i);

					IoOption ioOpt = new IoOption();

					/* Name */
					String name = propertyItem.getString("name");
					if(name == null || name.isEmpty())
						continue;
					
					/* Optional */
					String isOptional = propertyItem.getString("optional");
					if(isOptional != null && isOptional.equalsIgnoreCase("true"))
						ioOpt.setOptional(true);
					
					/* While executing */
					String whileExecuting = propertyItem.getString("whileexecuting");
					if(whileExecuting != null && whileExecuting.equalsIgnoreCase("true"))
						ioOpt.setOptional(true);
					
					options.put(name, ioOpt);
					
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return options;
	}

	/**
	 * Process the compensation property.
	 * 
	 * @param activity
	 * @param shape
	 */
	private void setCompensationProperty(Activity activity, Shape shape) {
		activity
				.setIsForCompensation((shape.getProperty("isforcompensation") != null ? shape
						.getProperty("isforcompensation").equalsIgnoreCase(
								"true")
						: false));
	}
	

	/**
	 * Sets the start quantity of the activity based on the data of the shape.
	 * 
	 * @param activity
	 * @param shape
	 * 		The resource shape
	 */
	private void setStartAndCompletionQuantity(Activity activity, Shape shape) {
		
		/* Start quantity */
		
		String startQuantity = shape.getProperty("startquantity");
		if(startQuantity != null) {
			try {
				activity.setStartQuantity(BigInteger.valueOf(Integer.valueOf(startQuantity)));
			} catch(Exception e) {
				e.printStackTrace();
				/* Set to default value in case of an exception */
				activity.setStartQuantity(BigInteger.valueOf(1));
			}
			
		}
		
		/* Completion quantity */
		String completionQuantity = shape.getProperty("completionquantity");
		if(completionQuantity != null) {
			try {
				activity.setCompletionQuantity(BigInteger.valueOf(Integer.valueOf(completionQuantity)));
			} catch(Exception e) {
				/* Set to default value in case of an exception */
				e.printStackTrace();
				activity.setCompletionQuantity(BigInteger.valueOf(1));
			}
		}
	}
	
	/**
	 * Entry method to create the {@link LoopCharacteristics} for a given 
	 * activity's shape.
	 * 
	 * @param activity
	 * @param shape
	 * @return
	 */
	protected void createLoopCharacteristics(Activity activity, Shape shape) {

		/* Distinguish between standard and multiple instance loop types */
		String loopType = shape.getProperty("looptype");
		if (loopType != null && !loopType.isEmpty()) {

			/* Loop type standard */
			if (loopType.equalsIgnoreCase("Standard")) 
				activity.setLoopCharacteristics(createStandardLoopCharacteristics(shape));
			
			/* Loop type multiple instances */
			else if (loopType.equalsIgnoreCase("Parallel")
					|| loopType.equalsIgnoreCase("Sequential")) {
				activity.setLoopCharacteristics(createMultiInstanceLoopCharacteristics(shape, loopType));
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param shape
	 * @return
	 */
	protected List<Property> createPropertiesList(Shape shape) {
		ArrayList<Property> propertiesList = new ArrayList<Property>();
		
		String propertiesString = shape.getProperty("properties");
		if(propertiesString != null && !propertiesString.isEmpty()) {
			try {
				JSONObject propertyObject = new JSONObject(propertiesString);
				JSONArray propertyItems = propertyObject.getJSONArray("items");

				/*
				 * Retrieve property definitions and process
				 * them.
				 */
				for (int i = 0; i < propertyItems.length(); i++) {
					JSONObject propertyItem = propertyItems.getJSONObject(i);

					Property property = new Property();

					/* Name */
					String name = propertyItem.getString("name");
					if(name != null && !name.isEmpty())
						property.setName(name);
					
					/* Data State */
					String dataState = propertyItem.getString("datastate");
					if(dataState != null && !dataState.isEmpty())
						property.setDataState(new DataState(dataState));
					
					/* ItemKind */
					String itemKind = propertyItem.getString("itemkind");
					if(itemKind != null && !itemKind.isEmpty())
						property.setItemKind(ItemKind.fromValue(itemKind));
					
					/* Structure */
					String structureString = propertyItem.getString("structure");
					if(structureString != null && !structureString.isEmpty())
						property.setStructure(structureString);
					
					/* isCollection */
					String isCollection = propertyItem.getString("iscollection");
					if(isCollection != null && isCollection.equalsIgnoreCase("false"))
						property.setCollection(false);
					else 
						property.setCollection(true);
					
					propertiesList.add(property);
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return propertiesList;
	}

	/**
	 * Creates the loop characteristics for multiple instances loops.
	 * 
	 * @param shape
	 * @param loopType
	 */
	private MultiInstanceLoopCharacteristics createMultiInstanceLoopCharacteristics(Shape shape,
			String loopType) {
		MultiInstanceLoopCharacteristics miLoop = new MultiInstanceLoopCharacteristics();

		/* Determine whether it is parallel or sequential */
		if (loopType.equalsIgnoreCase("Parallel"))
			miLoop.setIsSequential(false);
		else
			miLoop.setIsSequential(true);

		/* Set loop cardinality */
		String loopCardinalityString = shape
				.getProperty("loopcardinality");
		if (loopCardinalityString != null && !loopCardinalityString.isEmpty()) {
			FormalExpression loopCardinality = new FormalExpression(
					loopCardinalityString);
			miLoop.setLoopCardinality(loopCardinality);
		}

		/* Reference required DataInput */
		// miLoop.setLoopDataInput(value)
		// Task t = null;
		// t.get

		/* Completion condition */
		String completionCondition = shape
				.getProperty("completioncondition");
		if (completionCondition != null
				&& !completionCondition.isEmpty()) {
			FormalExpression completionConditionExpr = new FormalExpression(
					completionCondition);
			miLoop.setCompletionCondition(completionConditionExpr);
		}

		/* Handle loop behavior */
		handleLoopBehaviorAttributes(shape, miLoop);
		
		return miLoop;
	}

	/**
	 * Processes the attributes that are related to the loop behavior. 
	 * 
	 * @param shape
	 * @param miLoop
	 */
	private void handleLoopBehaviorAttributes(Shape shape,
			MultiInstanceLoopCharacteristics miLoop) {
		String behavior = shape.getProperty("behavior");
		if (behavior != null && !behavior.isEmpty()) {
			miLoop.setBehavior(MultiInstanceFlowCondition
					.fromValue(behavior));
		}

		/* Complex behavior */
		if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.COMPLEX)) {
			try {
				String comBehavDefString = shape
						.getProperty("complexbehaviordefinition");
				JSONObject complexDef = new JSONObject(
						comBehavDefString);
				JSONArray complexDefItems = complexDef
						.getJSONArray("items");

				/*
				 * Retrieve complex behavior definitions and process
				 * them.
				 */
				for (int i = 0; i < complexDefItems.length(); i++) {
					JSONObject complexDefItem = complexDefItems
							.getJSONObject(i);

					ComplexBehaviorDefinition comBehavDef = new ComplexBehaviorDefinition();

					/* Condition */
					String condition = complexDefItem
							.getString("cexpression");
					if (condition != null && !condition.isEmpty())
						comBehavDef.setCondition(new FormalExpression(
								condition));

					/* Event */
					ImplicitThrowEvent event = new ImplicitThrowEvent(
							complexDefItem.getString("ceventdefinition"));
					comBehavDef.setEvent(event);

					miLoop.getComplexBehaviorDefinition().add(
							comBehavDef);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			/* Handle none behavior choice */
		} else if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.NONE)) {
			String noneBehavString = shape
					.getProperty("nonebehavioreventref");
			if (noneBehavString != null && !noneBehavString.isEmpty()) {
				miLoop.setNoneBehaviorEventRef(EventDefinition
						.createEventDefinition(noneBehavString));
			}
			/* Handle one behavior choice */
		} else if (miLoop.getBehavior().equals(
				MultiInstanceFlowCondition.ONE)) {
			String oneBehavString = shape
					.getProperty("onebehavioreventref");
			if (oneBehavString != null && !oneBehavString.isEmpty()) {
				miLoop.setOneBehaviorEventRef(EventDefinition
						.createEventDefinition(oneBehavString));
			}
		}
	}

	/**
	 * Creates a {@link StandardLoopCharacteristics} object based on the shape's 
	 * properties.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		The {@link StandardLoopCharacteristics}
	 */
	private LoopCharacteristics createStandardLoopCharacteristics(Shape shape) {
		StandardLoopCharacteristics standardLoop = new StandardLoopCharacteristics();

		/* Set loop condition */
		String loopConditionString = shape.getProperty("loopcondition");
		if (loopConditionString != null
				&& !loopConditionString.isEmpty()) {
			FormalExpression loopCondition = new FormalExpression(
					loopConditionString);
			standardLoop.setLoopCondition(loopCondition);
		}

		/* Determine the point in time to check the loop condition */
		String testBeforeString = shape.getProperty("testbefore");
		if (testBeforeString != null
				&& testBeforeString.equalsIgnoreCase("true")) {
			standardLoop.setTestBefore(true);
		} else {
			standardLoop.setTestBefore(false);
		}

		/* Set the maximum number of loop iterations */
		try {
			standardLoop.setLoopMaximum(BigInteger.valueOf(Integer
					.parseInt(shape.getProperty("loopmaximum"))));
		} catch (Exception e) {
			/* In case of an exception do not set a loop iteration cap */
		}

		return standardLoop;
	}

}
