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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Class to represent a choreography diagram.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		
})
public class ChoreographyDiagram extends BpmnDiagram {
	
	@XmlElement(type = ChoreographyCompartment.class)
	private List<ChoreographyCompartment> choreographyCompartment;
	
	@XmlElement(namespace = "http://bpmndi.org")
    protected List<SequenceFlowConnector> sequenceFlowConnector;
	
	@XmlElement
	protected List<AssociationConnector> associationConnector;
		

	/* Getter & Setter */
	
	/**
	 * @return the list of {@link AssociationConnector}
	 */
	public List<AssociationConnector> getAssociationConnector() {
        if (this.associationConnector == null) {
            this.associationConnector = new ArrayList<AssociationConnector>();
        }
        return this.associationConnector;
    }
	
	/**
	 * @return the list of {@link SequenceFlowConnector}
	 */
	public List<SequenceFlowConnector> getSequenceFlowConnector() {
        if (this.sequenceFlowConnector == null) {
            this.sequenceFlowConnector = new ArrayList<SequenceFlowConnector>();
        }
        return this.sequenceFlowConnector;
    }
	
	/**
	 * @return the choreographyCompartment
	 */
	public List<ChoreographyCompartment> getChoreographyCompartment() {
		if(this.choreographyCompartment == null) {
			this.choreographyCompartment = new ArrayList<ChoreographyCompartment>();
		}
		return this.choreographyCompartment;
	}
}
