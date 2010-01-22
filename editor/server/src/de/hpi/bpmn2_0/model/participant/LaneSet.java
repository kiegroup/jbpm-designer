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

package de.hpi.bpmn2_0.model.participant;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.Process;


/**
 * <p>Java class for tLaneSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tLaneSet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}lane" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLaneSet", propOrder = {
    "lanes",
    "parentLane"
})
public class LaneSet
    extends BaseElement
{
	
	@XmlElementRef(type = Lane.class)
    protected List<Lane> lanes;
	
	@XmlIDREF
	@XmlAttribute
	protected Lane parentLane;
	
//	@XmlIDREF
//	@XmlAttribute
	@XmlTransient
	protected Process process;
	
	public void addChild(BaseElement child) {
		if(child instanceof Lane) {
			Lane lane = (Lane) child;
			this.getLanes().add(lane);
			lane.setLaneSet(this);
		}
	}
	
//	/**
//	 * Creates the lane compartment including all sub lane compartment for this
//	 * lane set.
//	 */
//	public LaneCompartment createLaneCompartment() {
//		LaneCompartment laneComp = new LaneCompartment();
//		laneComp.setId(Lane)
//	}
//	
	/**
	 * 
	 * @return All {@link FlowElement} that are contained in the {@link LaneSet}
	 */
	public List<FlowElement> getChildFlowElements() {
		ArrayList<FlowElement> deepestFlowElements = new ArrayList<FlowElement>();
		List<Lane> lanes = this.getDeepestLanes(this.getLanes()); 
		
		for(Lane lane : lanes) {
			deepestFlowElements.addAll(lane.getFlowElementRef());
		}
		
		return deepestFlowElements;
	}
	
	/**
	 * Retrieve the deepest child lanes in a lane set
	 * @param lanes
	 * @return
	 */
	private List<Lane> getDeepestLanes(List<Lane> lanes) {
		ArrayList<Lane> laneList = new ArrayList<Lane>();
		if(lanes == null)
			return laneList;
		for(Lane lane : lanes) {
			if(lane.childLaneSet == null) 
				/* Deepest lane in lane tree */
				laneList.add(lane);
			else if(lane.getChildLaneSet().lanes != null && lane.getChildLaneSet().getLanes().size() > 0) {
				laneList.addAll(this.getDeepestLanes(lane.getChildLaneSet().getLanes()));
			} else {
//				laneList.add(lane);
			}
		}
		return laneList;
	}
	
	/* Getter & Setter */
	
    /**
     * Gets the value of the lane property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lane property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLane().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Lane }
     * 
     * 
     */
	@ChildElements
    public List<Lane> getLanes() {
        if (this.lanes == null) {
            this.lanes = new ArrayList<Lane>();
        }
        return this.lanes;
    }

	/**
	 * @return the parentLane
	 */
	public Lane getParentLane() {
		return parentLane;
	}

	/**
	 * @param parentLane the parentLane to set
	 */
	public void setParentLane(Lane parentLane) {
		this.parentLane = parentLane;
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @param process the process to set
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

}
