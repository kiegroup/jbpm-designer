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

public class YEdge{
	
	protected YNode source;
	protected YNode target;
	
	private boolean defaultEdge;
    private String predicate = "";
    private int ordering = 0;
    
    /**
     * constructor of class 
     */
    public YEdge(YNode edgeSource, YNode edgeTarget)
    {
    	setSource(edgeSource);
    	setTarget(edgeTarget);
    	setEdgeType(false, "", 0);
    }
    
    /**
     * constructor of class 
     */
    public YEdge(YNode edgeSource, YNode edgeTarget, boolean defaultEdge, String predicate, int ordering){
    	setSource(edgeSource);
    	setTarget(edgeTarget);
    	setEdgeType(defaultEdge, predicate, ordering);
    }
    
    public YNode getSource() {
		return source;
	}

	public void setSource(YNode value) {
		if (source != null)
			source.getOutgoingEdges().remove(this);
		source = value;
		if (source != null)
			source.getOutgoingEdges().add(this);
	}

	public YNode getTarget() {
		return target;
	}

	public void setTarget(YNode value) {
		if (target != null)
			target.getIncomingEdges().remove(this);
		target = value;
		if (target != null)
			target.getIncomingEdges().add(this);
	}
	
	/**
	 * sets the edge parameters
	 * @param defaultEdge is the edge a default edge
	 * @param predicate the predicate of the edge
	 * @param ordering the ordering number
	 */
	public void setEdgeType(boolean defaultEdge, String predicate, int ordering) {
		setDefault(defaultEdge);
		setPredicate(predicate);
		setOrdering(ordering);
	}
	
	public boolean isDefault() {
		return defaultEdge;
	}

	public void setDefault(boolean value) {
		this.defaultEdge = value;
	}
	
	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String givenPredicate) {
		if(givenPredicate.equals("true()"))
			this.defaultEdge = true;
		else
			this.predicate = givenPredicate;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int predicateOrdering) {
		this.ordering = predicateOrdering;
	}
	
	/**
	 * serializes the predicate to XML
	 * @param splitType split type of the source Task
	 * @param s XML String
	 * @return XML String
	 */
	private String writePredicateToYAWL(SplitJoinType splitType, String s) {
        if (splitType == SplitJoinType.AND)
        	return s;
        
        s += "\t\t\t\t\t\t<predicate ";
        if (splitType == SplitJoinType.XOR) {
            s += String.format(" ordering=\"%s\">", ordering);
        } else if (splitType == SplitJoinType.OR) {
            s += ">"; //closing tag bracket
        }
        
        // Predicate might contain special characters. Have them replaced.
        s += predicate.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        s += "</predicate>\n";
		return s;
	}
	
	/**
     * Serializes the edge to XML.
     * @param splitType The split type of the originating YAWL node.
     * @return String The string to export for this YAWLDecompositon.
     */
    public String writeToYAWL(SplitJoinType splitType) {
        String s = "";

        s += "\t\t\t\t\t<flowsInto>\n";
        s += String.format("\t\t\t\t\t\t<nextElementRef id=\"%s\"/>\n", getTarget().getID());
        
        if (predicate != null && predicate.length() > 0)
        	s = writePredicateToYAWL(splitType, s);
        
        if (isDefault())
        	s += "\t\t\t\t\t\t<isDefaultFlow/>\n";
        
        s += "\t\t\t\t\t</flowsInto>\n";
        return s;
    }
}
