package de.hpi.bpel4chor.model.activities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.connections.Transition;
import de.hpi.bpel4chor.util.Output;

/**
 * A gateway is a diagram element that can have multiple incoming or
 * multiple outgoing sequence transitions. Gateways can be parallel (AND),
 * exclusive (XOR) or inclusive (OR). Exclusive decision gateways can be 
 * data-based or event-based. Event-based gateways must be followed by 
 * intermediate message or timer events or receive tasks.
 */
public class Gateway extends Activity {
	
	public static final String TYPE_XOR = "XOR";
	public static final String TYPE_OR = "OR";
	public static final String TYPE_AND = "AND";
	
	public static final String SPLIT_XOREVENT = "XOREVENT";
	public static final String SPLIT_XORDATA = "XOR";
	
	private String gatewayType = TYPE_XOR;
	private String splitType = SPLIT_XORDATA;
	private boolean createInstance = false; 
	private List<String> evaluationOrder = null;
	
	/**
	 * Constructor. Initializes the gateway and generates a unique id.
	 * By default the gateways is a data-based exclusive gateway.
	 * 
	 * @param gatewayType The type of the Gateway (AND, OR or XOR).
	 * @param splitType   If the gateway is an exclusive decision gateway, 
	 *                    the split type must be specified (XOREVENT or XORDATA). 
	 *                    Otherwise this parameter should be null.
	 * @param generated   True, if the event was generated during the
	 *                    transformation. 
	 *                    False if it is parsed from the input data.
	 * @param output      The output to print errors to.
	 */
	public Gateway(String gatewayType, String splitType, boolean generated, Output output) {
		super(generated, output);
		this.gatewayType = gatewayType;
		this.splitType = splitType;
	}
	
	/**
	 * Constructor. Initializes the gateway and generates a unique id.
	 * By default the gateways is a data-based exclusive gateway.
	 * Event-based exclusive gateways can instantiate a process. This can be 
	 * defined with createInstance.
	 * 
	 * @param gatewayType    The type of the Gateway (AND, OR or XOR).
	 * @param splitType      If the gateway is an exclusive decision gateway, 
	 *                       the split type must be specified (XOR or XORDATA). 
	 *                       Otherwise this parameter should be null.
	 * @param createInstance True, if the gateway is event-based and 
	 *                       instantiates the process, false otherwise.
	 * @param generated      True, if the event was generated during the
	 *                       transformation. 
	 *                       False if it is parsed from the input data.
	 * @param output         The output to print errors to.
	 */
	public Gateway(String gatewayType, String splitType, boolean createInstance, boolean generated, Output output) {
		super(generated, output);
		this.gatewayType = gatewayType;
		this.splitType = splitType;
		this.setCreateInstance(createInstance);
	}
	
	/**
	 * Constructor. Initializes the gateway and generates a unique id.
	 * By default the gateways is a data-based exclusive gateway.
	 * 
	 * @param output The output to print errors to.
	 */
	public Gateway(Output output) {
		super(output);
	}

	/**
	 * @return The type of the gateway ("XOR", "OR" or "AND").
	 */
	public String getGatewayType() {
		return this.gatewayType;
	}

	/**
	 * @return The split type of the gateway ("XOREVENT" or "XOR").
	 * If the split type was not defined the result is null.
	 */
	public String getSplitType() {
		return this.splitType;
	}
	
	@Override
	/**
	 * Checks if the gateway has already multiple incoming transitions.
	 * In this case there can only be one outgoing transition. Thus,
	 * if there is already an outgoing transition an error is added 
	 * to the output. Otherwise the transition is added to the list
	 * of outgoing transitions.
	 */
	public void addSourceFor(Transition transition, Output output) {
		if ((this.sourceFor.size() > 0) && (this.targetFor.size() > 1)) {
			output.addError("This gateway " +
					" is a merge gateway and can not have multiple"+
							" outgoing transitions.", getId());
		} else {
			super.addSourceFor(transition, output);
		}
	}
	
	@Override
	/**
	 * Checks if the gateway has already multiple outgoing transitions.
	 * In this case there can only be one incoming transition. Thus,
	 * if there is already an incoming transition an error is added 
	 * to the output. Otherwise the transition is added to the list
	 * of incoming transitions.
	 */
	public void addTargetFor(Transition transition, Output output) {
		if ((this.targetFor.size() > 0) && (this.sourceFor.size() > 1)) {
			output.addError("This gateway " +
					        " is a split gateway and cannot have multiple"+
							" incoming transitions.", getId());
		} else {
			super.addTargetFor(transition, output);
		}
	}
	
	/**
	 * Determines the first outgoing transition with the defined id.
	 * If there is no outgoing transition with this id, the result is null.
	 * 
	 * @param id The id of the outgoing transition to find.
	 * 
	 * @return The determined transition or null if no transition was found.
	 */
	public Transition getOutgoingWithId(String id) {
		for (Iterator<Transition> it = 
			this.sourceFor.iterator(); it.hasNext();) {
			
			Transition trans = it.next();
			if (trans.getId().equals(id)) {
				return trans;
			}
		}
		return null;
	}
	
	/**
	 * @return True, if the gateway instantiates the process, false otherwise.
	 */
	public boolean getCreateInstance() {
		return this.createInstance;
	}
	
	/**
	 * Sets gateway property that defines if the gateway instantiates the
	 * process.
	 * 
	 * @param createInstance True, if the gateway instantiates the process, 
	 * false otherwise.
	 */
	public void setCreateInstance(boolean createInstance) {
		if ((this.gatewayType != null) && this.gatewayType.equals(TYPE_XOR) && 
			(this.splitType) != null && this.splitType.equals(SPLIT_XOREVENT)) {
			this.createInstance = createInstance;
		} else {
			this.createInstance = false;
		}
	}

	/**
	 * Sets the type of the gateway.
	 * 
	 * @param gatewayType The new gateway type ("XOR", "OR" or "AND").
	 */
	public void setGatewayType(String gatewayType) {
		this.gatewayType = gatewayType;
	}

	/**
	 * Sets the split type of the gateway. The split type should only
	 * be set if the gateway is an exclusive decision gateway.
	 * 
	 * @param splitType The new split type of the gateway ("XOR" or "XOREVENT").
	 */
	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}

	/**
	 * Sets the evaluation order of the gateway. The evaluation order should 
	 * only be defined if the gateway is an exclusive decision gateway.
	 * 
	 * The evaluation order defines the order the outgoing transitions of the 
	 * gateway will be evaluated. The order of ids in the given list defines 
	 * order the outgoing transition conditions will be evaluated. The list must
	 * contain the ids of the outgoing transitions.
	 * 
	 * @param evaluationOrder A list with the ids of the outgoing transitions.  
	 */
	public void setEvaluationOrder(List<String> evaluationOrder) {
		this.evaluationOrder = evaluationOrder;
	}
	
	/**
	 * Determines the evaluation order for the gateway. The order is 
	 * given by the order of the transitions in the returned list.
	 * 
	 * If the given evaluation order is not valid, the evaluation order
	 * is determined from the order of the outgoing transitions. The evaluation
	 * order is invalid if it is not defined or if it containes a transition that 
	 * is not an outgoing transition of the gateway.
	 * 
	 * @return An ordered list with transitions, that should be evaluated in 
	 * the defined order.
	 */
	public List<Transition> determineEvaluationOrder() {
		if ((this.evaluationOrder == null) || 
				(this.evaluationOrder.size() != this.sourceFor.size())) {
			// if no evaluation order defined or the evaluation order does not 
			// contain all outgoing associations then use a default order
			return this.sourceFor;
		}
		
		List<Transition> result = new ArrayList<Transition>();
		for (Iterator<String> it = this.evaluationOrder.iterator(); it.hasNext();) {
			String id = it.next();
			Transition trans = this.getOutgoingWithId(id);
			if (trans == null) {
				// if transition with id is not an outgoing transition
				// use default order (invalid evaluation order)
				return this.sourceFor;
			}
			result.add(trans);
		}
		return result;
	}
}
