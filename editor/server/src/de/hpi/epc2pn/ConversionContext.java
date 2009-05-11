/**
 * Copyright (c) 2009 Matthias Weidlich
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
package de.hpi.epc2pn;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.petrinet.Node;

/**
 * Conversion context for the EPC to PTNet transformation. Basically captures the relation
 * between EPC model elements and the corresponding Petri net nodes. 
 * 
 * @author matthias.weidlich
 *
 */
public class ConversionContext {

	protected Map<IFlowObject, Node> conversionMapIn = new HashMap<IFlowObject, Node>();
	protected Map<IFlowObject, Node> conversionMapOut = new HashMap<IFlowObject, Node>();

	protected int id = 0;
	
	public ConversionContext() {
		this.conversionMapIn = new HashMap<IFlowObject, Node>();
		this.conversionMapOut = new HashMap<IFlowObject, Node>();
	}

	/**
	 * Might be used to generate a fresh ID during the transformation. Of course,
	 * a prefix has to be applied in addition in order to avoid conflicting ID assignments.
	 * 
	 * @return
	 */
	public int getId() {
		return this.id++;
	}
	
	public Map<IFlowObject, Node> getConversionMapIn() {
		return conversionMapIn;
	}

	public void setConversionMapIn(Map<IFlowObject, Node> conversionMapIn) {
		this.conversionMapIn = conversionMapIn;
	}

	public Map<IFlowObject, Node> getConversionMapOut() {
		return conversionMapOut;
	}

	public void setConversionMapOut(Map<IFlowObject, Node> conversionMapOut) {
		this.conversionMapOut = conversionMapOut;
	}
	
	public void setAllConversionMaps(IFlowObject object, Node node) {
		this.conversionMapIn.put(object, node);
		this.conversionMapOut.put(object, node);
	}
	
}