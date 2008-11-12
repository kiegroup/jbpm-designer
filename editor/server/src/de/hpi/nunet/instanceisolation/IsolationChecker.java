package de.hpi.nunet.instanceisolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.nunet.EnabledTransition;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Marking;
import de.hpi.nunet.Transition;
import de.hpi.nunet.simulation.Interpreter;

public class IsolationChecker {
	
	public static final int MAX_NUM_STATES = 1000;
	
	private Interpreter interpreter;
	private Set markings;
	private InterconnectionModel model;
	
	public IsolationChecker(InterconnectionModel model) {
		this.model = model;
		this.interpreter = new Interpreter();
		this.markings = new HashSet();
	}
	
	public boolean isIsolated() {
		List<Transition> competitionTransitions = new ArrayList();
		InterconnectionModel duplicatedModel = new ConversationDuplicator(model).getDuplicatedModel(competitionTransitions);
		return !checkReachability(duplicatedModel, competitionTransitions);
	}

	private boolean checkReachability(InterconnectionModel duplicatedModel, List<Transition> competitionTransitions) {
		markings.clear();
		Marking marking = duplicatedModel.getInitialMarking();
		return checkReachability(duplicatedModel, competitionTransitions, marking);
	}

	private boolean checkReachability(InterconnectionModel duplicatedModel, List<Transition> competitionTransitions, Marking marking) {
		if (markings.size() > MAX_NUM_STATES)
			return false;
		
		String markingStr = marking.toString();
//		System.out.println("Checking marking "+markingStr);
		
		// check if this marking was already processed
		if (markings.contains(markingStr))
			return false;
		markings.add(markingStr);

		List<EnabledTransition> tmodes = interpreter.getEnabledTransitions(duplicatedModel, marking);
		for (Iterator<EnabledTransition> it=tmodes.iterator(); it.hasNext(); ) {
			EnabledTransition tmode = it.next();
			
			if (competitionTransitions.contains(tmode.transition))
				return true;
			
			Marking newmarking = marking.getCopy();
			interpreter.fireTransition(duplicatedModel, newmarking, tmode);
			if (checkReachability(duplicatedModel, competitionTransitions, newmarking))
				return true;
		}
		return false;
	}

}
