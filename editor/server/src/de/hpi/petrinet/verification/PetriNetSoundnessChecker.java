package de.hpi.petrinet.verification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.diagram.reachability.ReachabilityPath;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetSoundnessChecker {
	PetriNet net;
	Set<Marking> deadLockMarkings;
	Set<Transition> deadTransitions;
	Set<Marking> improperTerminatingMarkings;
	Set<Transition> notParticipatingTransitions;
	PetriNetReachabilityGraph rg;
	Place outputPlace;

	public PetriNetSoundnessChecker(PetriNet net) {
		this.net = net;
	}

	/**
	 * Must be called before any checks takes place!!!
	 */
	public void calculateRG() {
		PetriNetRGCalculator rgCalc = new PetriNetRGCalculator(net,
				new PTNetInterpreter());
		rg = rgCalc.calculate();
	}

	/**
	 * Checks whether given net ... 1. ... is weak sound. 2. ... has no dead
	 * transitions (each transition must participate at exactly one firing
	 * sequence).
	 */
	public boolean isSound() {
		calcDeadTransitions();
		return isWeakSound() && deadTransitions.size() == 0;
	}

	/**
	 * Checks whether each transition of given net participates in at least one
	 * process instance that starts in the initial state and reaches the final
	 * state
	 * 
	 * @return
	 */
	public boolean isRelaxedSound() {
		calcNotParticipatingTransitions();
		return notParticipatingTransitions.size() == 1;
	}

	/**
	 * Checks whether ... 1. ... any process instance coming from initial state
	 * will reach the final state 2. ... the final state is the only state
	 */
	public boolean isWeakSound(){
		// 1. Each leave must be the end marking
		calcDeadLockMarkings();
		
		// 2. No markings except end markings may have a token in end places
		calcImproperTerminatingMarkings();
		
		return deadLockMarkings.size() == 0 && improperTerminatingMarkings.size() == 0;
	}

	public void checkLazySoundness() {
		// 1. In Blättern ist in jeder Endstelle genau 1 Token
		
		// 2. All markings don't have more than 1 token on end place
		for(Marking marking : rg.getMarkings()){
			if(marking.getNumTokens(net.getFinalPlace()) > 1){
				//...
			}
		}
	}

	/**
	 * Calculate all markings which are leaves and are deadlocks (which aren't final markings)
	 */
	public void calcDeadLockMarkings() {
		if (deadLockMarkings != null)
			return;

		deadLockMarkings = new HashSet<Marking>();

		for (Marking m : rg.getLeaves()) {
			if(!m.isFinalMarking()){
				deadLockMarkings.add(m);
			}
		}
	}

	/**
	 * Calculates dead transitions, i.e. these transitions which aren't on a path from
	 * beginning to end
	 */
	public void calcDeadTransitions() {
		if (deadTransitions != null)
			return;

		deadTransitions = new HashSet<Transition>();
		// Assume, that all transitions are dead
		deadTransitions.addAll(net.getTransitions());
		// Remove these transitions which are in reachability graph
		deadTransitions.removeAll(rg.getFlowObjects());
	}

	/**
	 * Calculates markings which have token in end state and in other states
	 */
	public void calcImproperTerminatingMarkings() {
		if (improperTerminatingMarkings != null)
			return;

		improperTerminatingMarkings = new HashSet<Marking>();

		for (Marking marking : rg.getMarkings()) {
			// if end marking have token and end marking doesn't have all tokens
			// of the net
			if (marking.getNumTokens(net.getFinalPlace()) > 0
					&& marking.getNumTokens(net.getFinalPlace()) != marking.getNumTokens()) {
				improperTerminatingMarkings.add(marking);
			}
		}
	}
	
	/**
	 * Calculates the set of transitions needed for checking relaxed soundness.
	 * 1. Get all leaves which are valid final markings
	 * 2. From each leaf, go back to root and collect transitions on the way
	 * 3. Compare collected transitions to the total set.
	 */
	public void calcNotParticipatingTransitions(){
		if (notParticipatingTransitions != null)
			return;
		
		notParticipatingTransitions = new HashSet(net.getTransitions());
		
		for(Marking marking : rg.getLeaves()){
			if(marking.isFinalMarking()){
				for(ReachabilityPath<Transition, Marking> path : rg.getPathsFromRoot(marking)){
					notParticipatingTransitions.removeAll(path.getFlowObjects());
				}
			}
		}
	}

	public Set<Marking> getDeadLockMarkings() {
		return deadLockMarkings;
	}
	
	public JSONArray getDeadLocksAsJson() throws JSONException{
		return markingsToJsonWithPath(this.getDeadLockMarkings());
	}
	
	public JSONArray getImproperTerminatingsAsJson() throws JSONException{
		return markingsToJsonWithPath(this.getImproperTerminatingMarkings());
	}
	
	public JSONArray getDeadTransitionsAsJson(){
		JSONArray deadTransitions = new JSONArray();
		for(Transition trans : this.getDeadTransitions()){
			deadTransitions.put(trans.getResourceId());
		}
		return deadTransitions;
	}
	
	public JSONArray getNotParticipatingTransitionsAsJson(){
		JSONArray notParticipatingTransitions = new JSONArray();
		for(Transition trans : this.getNotParticipatingTransitions()){
			notParticipatingTransitions.put(trans.getResourceId());
		}
		return notParticipatingTransitions;
	}

	public Set<Transition> getDeadTransitions() {
		return deadTransitions;
	}

	public Set<Marking> getImproperTerminatingMarkings() {
		return improperTerminatingMarkings;
	}
	
	/**
	 * Calculates the path of given marking (how to get to given marking from initial marking).
	 * @param m Marking
	 * @return json representation including the marking and the path
	 * @throws JSONException
	 */
	private JSONObject markingToJsonWithPath(Marking m) throws JSONException{
		JSONObject markingWithPath = new JSONObject();
		
		markingWithPath.put("marking", m.toJson());
		markingWithPath.put("path", rg.getPathFromRoot(m).toJson());
		
		return markingWithPath;
	}
	
	/**
	 * Calls markingToJsonWithPath() on each marking of markings
	 * @param markings
	 * @return [markingToJsonWithPath(marking1), markingToJsonWithPath(marking2)]
	 * @throws JSONException
	 */
	private JSONArray markingsToJsonWithPath(Collection<Marking> markings) throws JSONException{
		JSONArray markingsWithPath = new JSONArray();
		
		for(Marking marking : markings){
			markingsWithPath.put(markingToJsonWithPath(marking));
		}
		
		return markingsWithPath;
	}

	public Set<Transition> getNotParticipatingTransitions() {
		return notParticipatingTransitions;
	}
}
