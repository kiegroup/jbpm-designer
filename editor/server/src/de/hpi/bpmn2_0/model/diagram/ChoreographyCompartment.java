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
package de.hpi.bpmn2_0.model.diagram;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlIDREF;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.choreography.Choreography;

/**
 * A {@link ChoreographyCompartment} represents the displaying area that 
 * contains choreography elements.
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class ChoreographyCompartment extends BpmnCompartment {
	
	@XmlElementRefs({
		@XmlElementRef(type = ChoreographyActivityShape.class),
		@XmlElementRef(type = EventShape.class),
		@XmlElementRef(type = GatewayShape.class),
    	@XmlElementRef(type = TextAnnotationShape.class),
    	@XmlElementRef(type = MessageShape.class)
	})
	protected List<BpmnNode> bpmnShape;
	
	@XmlIDREF
	@XmlAttribute
	protected Choreography choreographyRef;
	
	/* Getter & Setter */
	
	/**
	 * Get the list of contained BPMN shape in the choreography.
	 * 
	 * Objects of the following type(s) are allowed in the list
	 * 
	 */
	public List<BpmnNode> getBpmnShape() {
        if (this.bpmnShape == null) {
            this.bpmnShape = new ArrayList<BpmnNode>();
        }
        return this.bpmnShape;
    }

	/**
	 * @return the choreographyRef
	 */
	public Choreography getChoreographyRef() {
		return choreographyRef;
	}

	/**
	 * @param choreographyRef the choreographyRef to set
	 */
	public void setChoreographyRef(Choreography choreographyRef) {
		this.choreographyRef = choreographyRef;
	}

	@Override
	protected FlowElement getFlowElement() {
		return null;
	}
}
