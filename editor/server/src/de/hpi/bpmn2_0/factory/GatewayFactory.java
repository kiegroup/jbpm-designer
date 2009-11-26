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

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.diagram.GatewayShape;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGatewayType;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;

/**
 * The factory to create {@link Gateway} BPMN 2.0 elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId({ 
	"Exclusive_Databased_Gateway",  
	"ParallelGateway", 
	"EventbasedGateway", 
	"InclusiveGateway", 
	"ComplexGateway" })
public class GatewayFactory extends AbstractBpmnFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor
	 * .server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) throws BpmnConverterException {
		GatewayShape gatewayShape = (GatewayShape) this.createDiagramElement(shape);
		Gateway gateway = (Gateway) createProcessElement(shape);
		gatewayShape.setGatewayRef(gateway);
		return new BPMNElement(gatewayShape, gateway, shape.getResourceId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		GatewayShape gatewayShape = new GatewayShape();
		this.setVisualAttributes(gatewayShape, shape);
		return gatewayShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			Gateway gateway = (Gateway) this.invokeCreatorMethod(shape);
			this.identifyGatewayDirection(gateway, shape);
			return gateway;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}

	/**
	 * Creator method for an exclusive databased Gateway.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The resulting {@link ExclusiveGateway}
	 */
	@StencilId("Exclusive_Databased_Gateway")
	protected ExclusiveGateway createExclusiveGateway(Shape shape) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}

	@StencilId("ParallelGateway")
	protected ParallelGateway createParallelGateway(Shape shape) {
		ParallelGateway gateway = new ParallelGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}
	
	@StencilId("EventbasedGateway")
	protected EventBasedGateway createEventBasedGateway(Shape shape) {
		EventBasedGateway gateway = new EventBasedGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
//		String instantiate = shape.getProperty("instantiate");
//		
//		if(instantiate != null && instantiate.equals("true"))
//			gateway.setInstantiate(true);
//		else
//			gateway.setInstantiate(false);
		
		/* Set gateway type and instantiation */
		gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		gateway.setInstantiate(false);
		
		String type = shape.getProperty("eventtype");
		if(type != null) {
			if(type.equalsIgnoreCase("instantiate_parallel")) {
				gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
				gateway.setInstantiate(true);
			} else if(type.equalsIgnoreCase("instantiate_exclusive")) {
				gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
				gateway.setInstantiate(true);
			}
		}
		
//		if(type != null && type.equalsIgnoreCase("instantiate_parallel")) 
//			gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
//		else 
//			gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		
		return gateway;
	}
	
	@StencilId("InclusiveGateway")
	protected InclusiveGateway createInclusiveGateway(Shape shape) {
		InclusiveGateway gateway = new InclusiveGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		return gateway;
	}
	
	@StencilId("ComplexGateway")
	protected ComplexGateway createComplexGateway(Shape shape) {
		ComplexGateway gateway = new ComplexGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		String activationCondition = shape.getProperty("activationcondition");
		if(activationCondition != null && !activationCondition.equals("")) {
			gateway.setActivationCondition(new Expression(activationCondition));
		}
		
		return gateway;
	}
	
	/**
	 * Determines and sets the {@link GatewayDirection}
	 */
	private void identifyGatewayDirection(Gateway gateway, Shape shape) {

		/* Determine the direction of the Gateway */

		int numIncomming = shape.getIncomings().size();
		int numOutgoing = shape.getOutgoings().size();

		GatewayDirection direction = GatewayDirection.UNSPECIFIED;

		if (numIncomming > 1 && numOutgoing > 1)
			direction = GatewayDirection.MIXED;
		else if (numIncomming <= 1 && numOutgoing > 1)
			direction = GatewayDirection.DIVERGING;
		else if (numIncomming > 1 && numOutgoing <= 1)
			direction = GatewayDirection.CONVERGING;

		/* Set the gateway direction */
		gateway.setGatewayDirection(direction);
	}

}
