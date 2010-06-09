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

import java.util.ArrayList;
import java.util.List;

public abstract class YNode implements FileWritingForYAWL {
	
	protected String id = ""; //the internal ID of a node
	protected String name = ""; //the name of a node that is shown in the editor
	protected List<YEdge> incomingEdges;
	protected List<YEdge> outgoingEdges;
	
	/**
	 * constructor of class 
	 */
	public YNode(String ID, String name){
		setID(ID);
		setName(name);
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setID(String anID) {
		this.id = anID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String nodeName){
		this.name = nodeName;
	}
	
	/**
	 * the incomingEdges getter
	 * @return incoming edges
	 */
	public List<YEdge> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<YEdge>();
		return incomingEdges;
	}

	/**
	 * the outcomingEdges getter
	 * @return outgoing edges
	 */
	public List<YEdge> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<YEdge>();
		return outgoingEdges;
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL()
	{
		//implementation of the FileWritingForYAWL interface
		return "";
	}
}