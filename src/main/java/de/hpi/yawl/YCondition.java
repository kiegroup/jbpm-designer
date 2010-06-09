package de.hpi.yawl;

/**
 * Copyright (c) 2009
 * 
 * @author Armin Zamani
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

public class YCondition extends YNode {
	
	/**
	 * constructor of class 
	 */
	public YCondition(String ID, String name){
		super(ID, name);
	}
	
	/**
	 * @see de.hpi.yawl.YNode#writeToYAWL()
	 */
	public String writeToYAWL() {
		String s = "";
		s +="\t\t\t\t<condition id=\"" + getID() + "\">\n";
		s +="\t\t\t\t\t<name>" + getName() + "</name>\n";
		
		s = writeOutgoingEdgesToYAWL(s);
		s +="\t\t\t\t</condition>\n";
		return s;
	}
	
	/**
	 * serializes outgoing edges to XML
	 * @param s XML String
	 * @return XML String
	 */
	protected String writeOutgoingEdgesToYAWL(String s) {
		for(YEdge edge: this.getOutgoingEdges())
			s += edge.writeToYAWL(SplitJoinType.AND);

		return s;
	}
}
