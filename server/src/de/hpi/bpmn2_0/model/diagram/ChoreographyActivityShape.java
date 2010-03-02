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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.diagram.activity.ActivityShape;

/**
 * Class to represent the visual appearance of a choreography activity.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		
})
public class ChoreographyActivityShape extends ActivityShape {
	
	@XmlElement(type = ChoreographyParticipantShape.class)
	protected List<ChoreographyParticipantShape> participantShape;
	
	@XmlElementRef
	protected List<BpmnNode> bpmnShape;
	
	public void addChild(BpmnNode child) {
		if(child instanceof ChoreographyParticipantShape) {
			this.getParticipantShapes().add((ChoreographyParticipantShape) child);
		} else if(child instanceof BpmnNode) {
			this.getBpmnShape().add(child);
		}
	}
	
	
	/* Getter & Setter */
	
	/**
	 * @return the List of all a choreography subprocess containing shapes.
	 */
	public List<BpmnNode> getBpmnShape() {
		if(this.bpmnShape == null) {
			this.bpmnShape = new ArrayList<BpmnNode>();
		}
		return this.bpmnShape;
	}
	
	/**
	 * @return the list of participant shapes of this choreography activity.
	 */
	public List<ChoreographyParticipantShape> getParticipantShapes() {
		if(this.participantShape == null) {
			this.participantShape = new ArrayList<ChoreographyParticipantShape>();
		}
		return this.participantShape;
	}
	
}
