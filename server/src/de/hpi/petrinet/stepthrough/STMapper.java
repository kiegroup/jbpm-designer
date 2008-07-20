package de.hpi.petrinet.stepthrough;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.PTnet.impl.PTNetInterpreterImpl;
import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SubProcess;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;

public class STMapper {
	private PTNet petriNet;
	private PTNetInterpreter petriNetInterpreter;
	private Marking petriNetMarking;
	private List<STTransition> changedObjs;
	private AutoSwitchLevel switchLevel = AutoSwitchLevel.SemiAuto;
	
	public STMapper(PTNet pn) {
		petriNet = pn;
		petriNetInterpreter = new PTNetInterpreterImpl();
		petriNetMarking = petriNet.getInitialMarking();

		fireInvisibleTransitions(); // invisible start events may have been generated
		changedObjs = getFireableTransitions();		
	}
	
	public void setAutoSwitchLevel(AutoSwitchLevel level) {
		this.switchLevel = level;
	}
	
//	private List<STTransition> findTransitions(String objResourceID) {
//		List<Transition> transitionList = petriNet.getTransitions();
//		List<STTransition> foundTransitions = new ArrayList();
//		STTransition t;
//		for(int i = 0; i < transitionList.size(); i++) {
//			t = (STTransition)transitionList.get(i);
//			if(t.getBPMNObj().getResourceId().equals(objResourceID)) {
//				foundTransitions.add(t);
//			}
//		}
//		
//		return foundTransitions;
//	}
	
	private STTransition findTransition(String objResourceID) {
		List<Transition> transitionList = petriNet.getTransitions();
		STTransition t;
		for(int i = 0; i < transitionList.size(); i++) {
			t = (STTransition)transitionList.get(i);
			if(t.getBPMNObj().getResourceId().equals(objResourceID)) {
				return t;
			}
		}
		return null;
	}

	public List<DiagramObject> getFireableObjects () {
		List<DiagramObject> objects = new ArrayList();
		//List<Transition> transitionList = petriNetInterpreter.getEnabledTransitions(petriNetMarking);
		List<Transition> transitionList = petriNetInterpreter.getEnabledTransitions(petriNet, petriNetMarking);
		STTransition t;
		
		for(int i = 0; i < transitionList.size(); i++) {
			t = (STTransition)transitionList.get(i);
			objects.add(t.getBPMNObj());
		}
		
		return objects;
	}
	
	private List<STTransition> getFireableTransitions () {
		List<STTransition> transitions = new ArrayList();
		//List<Transition> transitionList = petriNetInterpreter.getEnabledTransitions(petriNetMarking);
		List<Transition> transitionList = petriNetInterpreter.getEnabledTransitions(petriNet, petriNetMarking);
		
		for(int i = 0; i < transitionList.size(); i++) {
			transitions.add((STTransition)transitionList.get(i));
		}
		
		return transitions;
	}
	
	private void fireInvisibleTransitions() {
		// Assumption: Invisible transitions exist because the preprocessor added a bpmn object that the user can't see in Oryx
		// The resulting transitions have no resourceId set and can't be fired by the user
		List<STTransition> fireableObjects;
		boolean transitionFired = true;
		
		while(transitionFired)
		{
			transitionFired = false;
			fireableObjects = getFireableTransitions();

			for(int i = 0; i < fireableObjects.size(); i++) {
				// If no resourceId is set, there is no visible object in the Oryx diagram
				DiagramObject obj = fireableObjects.get(i).getBPMNObj();
				if(obj != null && obj.getResourceId() == null) {
					STTransition stt = fireableObjects.get(i);
					petriNetMarking = petriNetInterpreter.fireTransition(petriNet, petriNetMarking, stt);
					stt.incTimesExecuted();
					transitionFired = true;
					break;
				}
			}
		}
	}
	
	public boolean fireObject(String objResourceID) {
		STTransition t = null;
		// Check if this object can be fired
		List<STTransition> fireableObjects = getFireableTransitions();
		boolean isFireable = false;
		for(int i = 0; i < fireableObjects.size(); i++) {
			if (fireableObjects.get(i).getBPMNObj().getResourceId() != null) { // Otherwise there is a null-pointer exception when checking an invisble object
				if (fireableObjects.get(i).getBPMNObj().getResourceId().equals(objResourceID)) {
					t = fireableObjects.get(i);
					isFireable = true;
					break;
				}
			}
		}
		
		if(!isFireable) {
			return false;
		}
		
		// Fire transition
		petriNetMarking = petriNetInterpreter.fireTransition(petriNet, petriNetMarking, t);		
		t.incTimesExecuted();
		
		// Add obj to list because timesExecuted has changed
		changedObjs.add(t);
		
		// Fire invisible transitions
		fireInvisibleTransitions();
		
		if (switchLevel == AutoSwitchLevel.HyperAuto) {
			// HyperAuto tries to fire all transitions until the user has to make a decision, e.g. at an XOR gateway
			int timesFired = 0;
			List<STTransition> nowFireableObjects = getFireableTransitions();
			
			while (timesFired < 50) {
				// Find all transitions that were not fireable before
				List<STTransition> newFireableObjects = new ArrayList();
				
				for (int i = 0; i < nowFireableObjects.size(); i++) {
					STTransition stt = nowFireableObjects.get(i);
					// Transition wasn't enabled before?
					if (!fireableObjects.contains(stt)) {
						newFireableObjects.add(stt);
					}
				}
				
				// If there is only one newly fireable transition, the user has no choice.
				// Otherwise the user needs to decide what to do next.
				if(newFireableObjects.size() != 1) break;
				
				STTransition stt = newFireableObjects.get(0);
				petriNetMarking = petriNetInterpreter.fireTransition(petriNet, petriNetMarking, stt);
				stt.incTimesExecuted();
				changedObjs.add(stt);
				
				timesFired++;
				fireInvisibleTransitions();
				nowFireableObjects = getFireableTransitions();
			}
		}
		else {
			// Auto fire
			AutoSwitchLevel currentLevel = switchLevel;
			// Only fire transitions that are above NoAuto
			while (currentLevel != AutoSwitchLevel.NoAuto) {
				boolean transitionFired = true;
				while (transitionFired) {
					transitionFired = false;

					List<STTransition> nowFireableObjects = getFireableTransitions();

					for (int i = 0; i < nowFireableObjects.size(); i++) {
						STTransition stt = nowFireableObjects.get(i);
						// Transition on the current level?
						if (stt.getAutoSwitchLevel().equals(currentLevel)) {
							// Transition wasn't enabled before?
							if (!fireableObjects.contains(stt)) {
								//petriNetMarking = petriNetInterpreter.fireTransition(petriNetMarking, stt);
								petriNetMarking = petriNetInterpreter.fireTransition(petriNet, petriNetMarking, stt);
								stt.incTimesExecuted();
								changedObjs.add(stt);
								transitionFired = true;
								// There should only be one transition enabled that wasn't enabled before on this level
								// So let's break and see if a new one has been enabled
								fireInvisibleTransitions();
								break;
							}
						}
					}
				}
				// Continue checking transitions on a lower level
				if (currentLevel.lowerLevelExists())
					currentLevel = currentLevel.lowerLevel();
			}
		}
		List<STTransition> newlyFireableObjects = getFireableTransitions();
		// Update changedObjs with Objects that weren't fireable before
		for(int i = 0; i < newlyFireableObjects.size(); i++) {
			if(!fireableObjects.contains(newlyFireableObjects.get(i))) {
				changedObjs.add(newlyFireableObjects.get(i));
			}
		}
		
		// Update changedObjs with Objects that aren't fireable anymore
		for(int i = 0; i < fireableObjects.size(); i++) {
			if(!newlyFireableObjects.contains(fireableObjects.get(i))) {
				// May already be inside the list because it has been fired
				if(!changedObjs.contains(fireableObjects.get(i))) {
					changedObjs.add(fireableObjects.get(i));
				}
			}
		}
		
		return true;
	}
	
	public void clearChangedObjs() {
		changedObjs.clear();
	}
	
	public String getChangedObjsAsString() {
		StringBuilder sb = new StringBuilder(15 * changedObjs.size());
		List<STTransition> fireableObjects = getFireableTransitions();
		// Save relevant information in the string
		// Format: BPMNResourceID,TimesExecuted,isFireable;
		for(STTransition t : changedObjs) {
			// Don't send invisible objects
			if (t.getBPMNObj().getResourceId() != null) {
				// Non-empty subprocesses should only be highlighted if the user has to fire them
				if (t.getBPMNObj() instanceof SubProcess) {
					SubProcess sp = (SubProcess) t.getBPMNObj();
					if((sp.getChildNodes().size() > 0) && !fireableObjects.contains(t))
						continue;
				}
				sb.append(t.getBPMNObj().getResourceId());
				sb.append(",");
				sb.append(t.getTimesExecuted());
				sb.append(",");
				if (fireableObjects.contains(t))
					sb.append("t;");
				else
					sb.append("f;");
			}
		}
		
		return sb.toString();
	}
	
//	public List<DiagramObject> getChangedObjs() {
//		List<DiagramObject> objs = new ArrayList();
//		for(int i = 0; i < changedObjs.size(); i++) {
//			objs.add(changedObjs.get(i).getBPMNObj());
//		}		
//		return objs;
//	}
	
//	public List<DiagramObject> flushChangedObjs() {
//		List<DiagramObject> obj = changedObjs;
//		changedObjs = new ArrayList();
//		return obj;
//	}
}
