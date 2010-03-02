package de.hpi.interactionnet.localmodelgeneration;

import java.util.ArrayList;
import java.util.List;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.verification.MaxStatesExceededException;
import de.hpi.PTnet.verification.WeakTerminationChecker;
import de.hpi.PTnet.verification.WeakTerminationChecker.UnboundedNetException;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.petrinet.Transition;

/**
 * @author Gero.Decker
 */
public class DesynchronizabilityChecker {
	
	/**
	 * 
	 * @param net
	 * @param conflictingTransitions will contain the conflicting transitions (if any)
	 * @return
	 */
	public boolean check(InteractionNet net, List<Transition> conflictingTransitions) {
		if (net.getFinalMarkings().size() == 0)
			net.getFinalMarkings().addAll(new FinalMarkingsCalculator(net).getFinalMarkings());
		List<Marking> finalMarkings = new ArrayList<Marking>();
		PTNet dnet = new Desynchronizer().getDesynchronizedNet(net, finalMarkings);
		try {
			WeakTerminationChecker checker = new WeakTerminationChecker(dnet, finalMarkings);
			boolean isOk = checker.check();
			conflictingTransitions = checker.getConflictTransitions();
			return isOk;
		} catch (MaxStatesExceededException e) {
			// mark all transitions TODO provide a nicer visualization
			conflictingTransitions.addAll(net.getTransitions());
			return false;
		} catch (UnboundedNetException e) {
			// mark all transitions TODO provide a nicer visualization
			conflictingTransitions.addAll(net.getTransitions());
			return false;
		}
	}

}


