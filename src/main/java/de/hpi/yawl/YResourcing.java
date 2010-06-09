package de.hpi.yawl;

/**
 * Copyright (c) 2010, Armin Zamani
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
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import de.hpi.yawl.resourcing.InitiatorType;
import de.hpi.yawl.resourcing.DistributionSet;

public class YResourcing implements FileWritingForYAWL {

	private InitiatorType start;
	private InitiatorType allocate;
	private DistributionSet allocateDistributionSet;
	private InitiatorType offer;
	private DistributionSet offerDistributionSet;
	
	public void setStart(InitiatorType start) {
		this.start = start;
	}

	public InitiatorType getStart() {
		return start;
	}

	public void setAllocate(InitiatorType allocate) {
		this.allocate = allocate;
	}

	public InitiatorType getAllocate() {
		return allocate;
	}

	public void setAllocateDistributionSet(DistributionSet allocateDistributionSet) {
		this.allocateDistributionSet = allocateDistributionSet;
	}

	public DistributionSet getAllocateDistributionSet() {
		return allocateDistributionSet;
	}

	public void setOffer(InitiatorType offer) {
		this.offer = offer;
	}

	public InitiatorType getOffer() {
		return offer;
	}
	
	public void setOfferDistributionSet(DistributionSet offerDistributionSet) {
		this.offerDistributionSet = offerDistributionSet;
	}

	public DistributionSet getOfferDistributionSet() {
		return offerDistributionSet;
	}

	/**
	 * serializes offer to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeOfferToYAWL(String s) {
		s += String.format("\t<offer initiator=\"%s\" ", offer.toString().toLowerCase());
		if((offer == InitiatorType.SYSTEM) && (offerDistributionSet != null) 
				&& (offerDistributionSet.getInitialSetList().size() > 0)){
			s += ">\n";
			s += offerDistributionSet.writeToYAWL();
			s += "\t</offer>\n";
		}else
			s += "/>\n";
		
		return s;
	}
	
	/**
	 * serializes allocate to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeAllocateToYAWL(String s) {
		s += String.format("\t<allocate initiator=\"%s\" ", allocate.toString().toLowerCase());
		if((allocate == InitiatorType.SYSTEM) && (allocateDistributionSet != null) 
				&& (allocateDistributionSet.getInitialSetList().size() > 0)){
			s += ">\n";
			s += allocateDistributionSet.writeToYAWL();
			s += "\t</allocate>\n";
		}else
			s += "/>\n";
		return s;
	}
	
	/**
	 * serializes start to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeStartToYAWL(String s) {
		s += String.format("\t<start initiator=\"%s\" />\n", start.toString().toLowerCase());
		return s;
	}

	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	@Override
	public String writeToYAWL() {
		String s = "";
		
		s += "<resourcing>\n";
		s = writeOfferToYAWL(s);
		s = writeAllocateToYAWL(s);
		s = writeStartToYAWL(s);
		s += "</resourcing>\n";
		
		return s;
	}

	
}
