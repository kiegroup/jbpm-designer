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

import java.util.*;

public class YDecomposition implements FileWritingForYAWL {
	
	public enum ExternalInteraction {
		MANUAL, AUTOMATED
	}
	
	private ArrayList<YNode> nodes = new ArrayList<YNode>();
	private ArrayList<YEdge> edges = new ArrayList<YEdge>();
	
	private YInputCondition inputCondition;
	private YOutputCondition outputCondition;
	
    private String id; // The id of the decomposition
    private boolean isRootNet; // Whether this decomposition is the root
    private XsiType xsiType; // the xsi:type of the decomposition
    
    private ArrayList<YVariable> inputParameters = new ArrayList<YVariable>();
    private ArrayList<YVariable> outputParameters = new ArrayList<YVariable>();
    private ArrayList<YVariable> localVariables = new ArrayList<YVariable>();
    
	private ExternalInteraction externalInteraction = ExternalInteraction.MANUAL;
	private String codelet = "";

    public String getID(){
    	return this.id;
    }
    
    public void setID(String ID){
    	this.id = ID;
    }
    
    /**
     * Create a new YAWL decomposition, given its name, whether it is the root, and its xsi:type.
     * @param id The given name
     * @param isRootNet Whether it is the root ("true") or not (anything else)
     * @param xsiType The xsi:type
     */
        
    public YDecomposition(String id, Boolean isRootNet, XsiType xsiType) {
        setID(id);
        setRootNet(isRootNet);
        setXSIType(xsiType);
    }
    
    public void setRootNet(Boolean isRootNet){
    	this.isRootNet = isRootNet;
    }
    
    public void setXSIType(XsiType xsiType){
    	this.xsiType = xsiType;
    }
    
	public void setExternalInteraction(ExternalInteraction externalInteraction) {
		this.externalInteraction = externalInteraction;
	}

	public ExternalInteraction getExternalInteraction() {
		return externalInteraction;
	}

	public void setCodelet(String codelet) {
		this.codelet = codelet;
	}

	public String getCodelet() {
		return codelet;
	}

    /**
     * Returns whether root.
     * @return Whether root.
     */
    public boolean isRoot() {
        return isRootNet;
    }
    
    public List<YEdge> getEdges(){
    	if (edges == null)
			edges = new ArrayList<YEdge>();
		return edges;
    }
    
    public ArrayList<YNode> getNodes(){
    	if (nodes == null)
			nodes = new ArrayList<YNode>();
		return nodes;
    }
    
    public YInputCondition getInputCondition() {
		return inputCondition;
	}

	public void setInputCondition(YInputCondition inputCondition) {
		this.inputCondition = inputCondition;
	}

	public YOutputCondition getOutputCondition() {
		return outputCondition;
	}

	public void setOutputCondition(YOutputCondition outputCondition) {
		this.outputCondition = outputCondition;
	}

	/**
	 * Gets the set of edges from the first node to the second node.
	 * @param v1 the first node
	 * @param v2 the second node
	 * @return the set of edges from the first node to the second node
	 */
	public HashSet<YEdge> getEdgesBetween(YNode sourceNode, YNode targetNode) {
		HashSet<YEdge> result = new HashSet<YEdge>();
		
		for(YEdge edge: sourceNode.getOutgoingEdges()){
			if (edge.getTarget() == targetNode) {
				result.add(edge);
			}
		}
		return result;
	}
    
	/**
     * Adds the given node to the decomposition node collection.
     * @param node The YAWL node
     */
    public void addNode(YNode node){
    	if (!((node instanceof YInputCondition) || (node instanceof YOutputCondition)))
    		//prevent that an input or output condition gets part of the nodes set
    		//there has to be only one input and one output condition in a decomposition
    		nodes.add(node);
    }

    /**
     * Creates an input condition with given id and name and adds it to the decomposition.
     * @param id The id for the node (technical)
     * @param name The name for the node (representative)
     * @return created input condition node
     */
    public YInputCondition createInputCondition(String id, String name) {
        YInputCondition condition = new YInputCondition(id, name);
        setInputCondition(condition);
        return condition;
    }

    /**
     * Creates an output condition with given id and name and adds it to the decomposition.
     * @param id The id for the node (technical)
     * @param name The name for the node (representative)
     * @return created output condition node
     */
    public YOutputCondition createOutputCondition(String id, String name) {
        YOutputCondition condition = new YOutputCondition(id, name);
        setOutputCondition(condition);
        return condition;
    }

    /**
     * Creates a (normal) condition with given id and name and adds it to the decomposition nodes collection.
     * @param id The id for the node (technical)
     * @param name The name for the node (representative)
     * @return created condition node
     */
    public YCondition createCondition(String id, String name) {
        YCondition condition = new YCondition(id, name);
        addNode(condition);
        return condition;
    }
    
    /**
     * connects the input condition to the output condition
     * @return created edge
     */
    public YEdge connectInputToOutput(){
    	return createEdge(inputCondition, outputCondition);
    }

    /**
     * Creates a task with given name, join type, split type and adds it the decomposition nodes collection.
     * @param id The identifier
     * @param name The name
     * @param joinType The join type (and, xor, or)
     * @param splitType The split type (and, xor, or)
     * @return the task created
     */
    public YTask createTask(String id, String name, SplitJoinType joinType,
    		SplitJoinType splitType) {
        
        YTask task = new YTask(id, name, joinType, splitType);
        addNode(task);
        return task;
    }
    
    /**
     * create a YAWL task with no special splitting/joining behaviour (YAWL standard for this case 
     * is a joining XOR and a splitting AND behaviour)
     * @param id
     * @param name
     * @return
     */
    public YTask createTask(String id, String name){
    	YTask task = createTask(id, name, SplitJoinType.XOR, SplitJoinType.AND);
    	
    	return task;
    }

    /**
     * removes the given node from the node collection
     * @param node node to be removed
     */
    public void removeNode(YNode node) {
        nodes.remove(node);
    }
    
    /**
     * removes the given edge from the edge collection
     * @param edge edge to be removed
     */
    public void removeEdge(YEdge edge){
    	edge.getSource().getOutgoingEdges().remove(edge);
    	edge.getTarget().getIncomingEdges().remove(edge);
    	edges.remove(edge);
    }
    
    /**
     * Adds an edge to the edges list of the decomposition.
     * @param edge The edge object
     */
    public void addEdge(YEdge edge) {
        edges.add(edge);
    }

    /**
     * Adds an edge from the given source node to the given destination node, given whether it is a default flow, given its predicate and its ordering.
     * @param source the source node
     * @param target the target node
     * @param isDefaultFLow Whether it is a default edge
     * @param predicate The predicate
     * @param ordering The predicate ordering
     */
    public YEdge createEdge(YNode source, YNode target, boolean isDefaultFlow, String predicate, int ordering) {
        YEdge newEdge = new YEdge(source, target, isDefaultFlow, predicate, ordering);
        addEdge(newEdge);
        return newEdge;
    }
    
    /**
     * Adds an edge from the given source node to the given destination node.
     * @param source The name of the source node
     * @param target The name of the destination node
     */
    
    public YEdge createEdge(YNode source, YNode target) {
     	 YEdge newEdge = new YEdge(source, target);
         addEdge(newEdge);
         return newEdge;
    }
    
    /**
     * returns the list of input parameters of the decomposition
     * @return input parameters
     */
    public ArrayList<YVariable> getInputParams(){
    	if (inputParameters == null)
    		inputParameters = new ArrayList<YVariable>();
		return inputParameters;
    }
    
    /**
     * returns the list of output parameters of the decomposition
     * @return output parameters
     */
    public ArrayList<YVariable> getOutputParams(){
    	if (outputParameters == null)
    		outputParameters = new ArrayList<YVariable>();
		return outputParameters;
    }
    
    /**
     * returns the list of local variables of the decomposition
     * @return local variables
     */
    public ArrayList<YVariable> getLocalVariables(){
    	if (localVariables == null)
    		localVariables = new ArrayList<YVariable>();
		return localVariables;
    }
    
    /**
	 * serializes the list of input parameters to XML
	 * @param s XML String
	 * @return XML String
	 */
    private String writeInputParamsToYAWL(String s){
    	if(getInputParams().size() > 0){
        	for(YVariable var : getInputParams()){
        		s += "\t\t\t\t\t<inputParam>\n";
        		s += var.writeAsParameterToYAWL();
        		s += "\t\t\t\t\t</inputParam>\n";
        	}
        }
    	return s;
    }
    
    /**
	 * serializes the list of output parameters to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeOutputParamsToYAWL(String s) {
		if(getOutputParams().size() > 0){
        	for(YVariable var : getOutputParams()){
        		s += "\t\t\t\t\t<outputParam>\n";
        		s += var.writeAsParameterToYAWL();
        		s += "\t\t\t\t\t</outputParam>\n";
        	}
        }
		return s;
	}
	
	/**
	 * serializes the list of local variables to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeLocalVariablesToYAWL(String s) {
		if(getLocalVariables().size() > 0){
        	for(YVariable var : getLocalVariables()){
        		s += "\t\t\t\t\t<localVariable>\n";
        		s += var.writeAsParameterToYAWL();
        		s += "\t\t\t\t\t</localVariable>\n";
        	}
        }
		return s;
	}
	
	/**
	 * serializes the process control elements to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeProcessControlElementsToYAWL(String s) {
		Iterator<YNode> it = getNodes().iterator();
        if (it.hasNext()) {
            s += "\t\t\t<processControlElements>\n";
            s += inputCondition.writeToYAWL();
            while(it.hasNext()){
            	Object object = it.next();
            	if (object instanceof YTask) 
                    s += ((YTask) object).writeToYAWL();
                else if (object instanceof YCondition) 
                    s += ((YCondition) object).writeToYAWL(); 
            }
            s += outputCondition.writeToYAWL();
            
            s += "\t\t\t</processControlElements>\n";
        }
		return s;
	}
	
	/**
	 * serializes the web service information of the decomposition to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeWebServiceGatewayFactsTypeToYAWL(String s) {
        if(!codelet.isEmpty())
        	s += String.format("\t\t\t<codelet>%s</codelet>\n", codelet);
        s += String.format("\t\t\t<externalInteraction>%s</externalInteraction>\n", externalInteraction.toString().toLowerCase(Locale.ENGLISH));
		
        return s;
	}

    /**
     * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
     */
    public String writeToYAWL() {
        String s = "";
        
        s += String.format("\t\t<decomposition id=\"%s\" ", id);
        if (isRootNet) {
            s += "isRootNet=\"true\" ";
        }
        s += String.format("xsi:type=\"%s\" >\n", xsiType);
        
        s = writeInputParamsToYAWL(s);
        
        s = writeOutputParamsToYAWL(s);
        
        s = writeLocalVariablesToYAWL(s);

        s = writeProcessControlElementsToYAWL(s);
        
        if(xsiType.equals(XsiType.WebServiceGatewayFactsType))
        	s = writeWebServiceGatewayFactsTypeToYAWL(s);
        
        s += "\t\t</decomposition>\n";
        
        return s;
    }
}