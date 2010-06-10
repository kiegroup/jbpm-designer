package de.hpi.nunet.correlatability;

import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.simulation.SigmaBisimulationChecker;

public class CorrelatabilityChecker {
	
	private InterconnectionModel model;
	private SigmaBisimulationChecker checker;
	
	public CorrelatabilityChecker(InterconnectionModel model) {
		this.model = model;
		checker = new SigmaBisimulationChecker();
	}
	
	public boolean checkCorrelatability() {
		ConversationDuplicator duplicator = new ConversationDuplicator(model);
		NuNet dup = duplicator.getDuplicatedModel();
		NuNet rdup = duplicator.getRestrictedDuplicatedModel();
		return checker.checkSigmaBisimilarity(dup, rdup);
	}

	public Marking[] getLastMarkingsChecked() {
		return checker.getLastMarkingsChecked();
	}

	public Object getLastTransitionModeChecked() {
		return checker.getLastTransitionModeChecked();
	}

}
