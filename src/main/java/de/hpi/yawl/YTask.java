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
import java.util.Locale;

public class YTask extends YNode{
	
	private SplitJoinType joinType = SplitJoinType.XOR;
	private SplitJoinType splitType = SplitJoinType.AND;
	private YDecomposition decomposesTo = null;
	private String xsiType = "";
	private YMultiInstanceParam miParam = null;

	private boolean isMultipleTask = false;
	private ArrayList<YNode> cancellationSet;
	private ArrayList<YVariableMapping> startingMappings = new ArrayList<YVariableMapping>();
	private ArrayList<YVariableMapping> completedMappings = new ArrayList<YVariableMapping>();
	private YTimer timer = null;
	private YResourcing resourcing = null;
	
	public YTimer getTimer() {
		return timer;
	}

	public void setTimer(YTimer timer) {
		this.timer = timer;
	}

	public YTask(String ID)
	{
		super(ID, "");
	}
	
	/**
	 * constructor of class 
	 */
	public YTask(String ID, String name)
	{
		super(ID, name);
	}
	
	/**
	 * constructor of class 
	 */
	public YTask(String ID, String name, SplitJoinType join, SplitJoinType split){
		super(ID, name);
		
		setJoinType(join);
		setSplitType(split);
	}
	
	public SplitJoinType getJoinType(){
		return this.joinType;
	}
	
	public void setJoinType(SplitJoinType join){
		this.joinType = join;
	}
	
	public SplitJoinType getSplitType(){
		return this.splitType;
	}
	
	public void setSplitType(SplitJoinType split){
		this.splitType = split;
	}
	
	public YDecomposition getDecomposesTo() {
		return this.decomposesTo;
	}
	
	public void setDecomposesTo(YDecomposition decomposesTo) {
		this.decomposesTo = decomposesTo;
	}
	
	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}

	public String getXsiType() {
		return xsiType;
	}

	public void setMiParam(YMultiInstanceParam miParam) {
		this.miParam = miParam;
	}

	public YMultiInstanceParam getMiParam() {
		return miParam;
	}
	
	public boolean isMultipleTask(){
		return this.isMultipleTask;
	}
	
	public void setIsMultipleTask(boolean multiple){
		this.isMultipleTask = multiple;
	}
	
	/**
	 * the cancellationSet getter
	 * @return cancellation set
	 */
	public ArrayList<YNode> getCancellationSet(){
		if (cancellationSet == null)
			cancellationSet = new ArrayList<YNode>();
		return cancellationSet;
	}
	
	/**
	 * the startingMappings getter
	 * @return starting mappings
	 */
	public ArrayList<YVariableMapping> getStartingMappings(){
		if (startingMappings == null)
			startingMappings = new ArrayList<YVariableMapping>();
		return startingMappings;
	}
	
	/**
	 * the completedMappings getter
	 * @return completed mappings
	 */
	public ArrayList<YVariableMapping> getCompletedMappings(){
		if (completedMappings == null)
			completedMappings = new ArrayList<YVariableMapping>();
		return completedMappings;
	}

	public void setResourcing(YResourcing resourcing) {
		this.resourcing = resourcing;
	}

	public YResourcing getResourcing() {
		return resourcing;
	}

	/**
	 * serializes the outgoing edges to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeOutgoingEdgesToYAWL(String s) {
		for(YEdge edge: this.getOutgoingEdges())
			s += edge.writeToYAWL(this.splitType);

		return s;
	}

	/**
	 * serializes the completed mappings to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeCompletedMappingsToYAWL(String s) {
		if (getCompletedMappings().size() > 0){
			s += "\t\t\t\t\t<completedMappings>\n";
			for(YVariableMapping mapping : getCompletedMappings()){
				s += mapping.writeToYAWL();
			}
			s += "\t\t\t\t\t</completedMappings>\n";
		}
		return s;
	}

	/**
	 * serializes the starting mappings to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeStartingMappingsToYAWL(String s) {
		if (getStartingMappings().size() > 0){
			s += "\t\t\t\t\t<startingMappings>\n";
			for(YVariableMapping mapping : getStartingMappings()){
				s += mapping.writeToYAWL();
			}
			s += "\t\t\t\t\t</startingMappings>\n";
		}
		return s;
	}

	/**
	 * serializes the cancellation set to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeCancellationSetToYAWL(String s) {
		if (getCancellationSet().size() > 0){
			for(YNode removeNode: getCancellationSet()){
				s += "\t\t\t\t\t<removesTokens id=\"" + removeNode.getID() + "\"/>\n";
			}
		}
		return s;
	}
	
	/**
	 * serializes the multiple instance parameters to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeMiParamToYAWL(String s) {
		if (isMultipleTask()) {
            s += getMiParam().writeToYAWL();
        }
		return s;
	}

	/**
	 * serializes the decomposesTo to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeDecomposesToToYAWL(String s) {
		if (decomposesTo != null) {
            s += String.format("\t\t\t\t\t<decomposesTo id=\"%s\"/>\n", getDecomposesTo().getID());
        }
		return s;
	}

	/**
	 * serializes the timer to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeTimerToYAWL(String s) {
		if (timer != null){
			s += timer.writeToYAWL();
		}
		return s;
	}
	
	/**
	 * serializes resourcing to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeResourcingToYAWL(String s) {
		if (resourcing != null){
			s += resourcing.writeToYAWL();
		}
		return s;
	}

	/**
	 * serializes the split and join type to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeSplitJoinTypeToYAWL(String s) {
		s += String.format("\t\t\t\t\t<join code=\"%s\"/>\n", getJoinType().toString().toLowerCase(Locale.ENGLISH));
		s += String.format("\t\t\t\t\t<split code=\"%s\"/>\n", getSplitType().toString().toLowerCase(Locale.ENGLISH));
		return s;
	}
	
	/**
	 * @see de.hpi.yawl.YNode#writeToYAWL()
	 */
	public String writeToYAWL() {
		String s = "";
			
		if(!getXsiType().isEmpty())
			s += String.format("\t\t\t\t<task id=\"%s\" xsi:type=\"%s\">\n", getID(), getXsiType());
		else
			s += String.format("\t\t\t\t<task id=\"%s\">\n", getID());

		s += String.format("\t\t\t\t\t<name>%s</name>\n", getName());

		// First, normal edges
		s = writeOutgoingEdgesToYAWL(s);

		// Second, join and split type
		s = writeSplitJoinTypeToYAWL(s);

		// Third, reset set
		s = writeCancellationSetToYAWL(s);
			
		s = writeStartingMappingsToYAWL(s);
			
		s = writeCompletedMappingsToYAWL(s);
			
		s = writeTimerToYAWL(s);
		s = writeResourcingToYAWL(s);
			
        s = writeDecomposesToToYAWL(s);
        s = writeMiParamToYAWL(s);
            
		s +="\t\t\t\t</task>\n";
		return s;
	}
}
