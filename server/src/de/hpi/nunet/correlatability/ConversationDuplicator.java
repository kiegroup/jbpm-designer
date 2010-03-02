package de.hpi.nunet.correlatability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Node;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.NuNetFactory;
import de.hpi.nunet.Place;
import de.hpi.nunet.ProcessModel;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;

/**
 * Needed for correlatability checking.
 * Duplicates the marking, introduces process instance identifiers and process model variables 
 * 
 * @author gero.decker
 *
 */
public class ConversationDuplicator {
	
	private NuNetFactory factory;
	private InterconnectionModel model;

	public ConversationDuplicator(InterconnectionModel model) {
		this.model = model;
		this.factory = NuNetFactory.eINSTANCE;
	}

	private void duplicateTokens(NuNet net, Map map) {
		for (Iterator<Place> it=model.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			Place newp = factory.createPlace();
			map.put(p, newp);
			net.getPlaces().add(newp);
			
			newp.setLabel(p.getLabel());
			
			for (Iterator<Token> it2=model.getInitialMarking().getTokens(p).iterator(); it2.hasNext(); ) {
				Token tok = it2.next();
				Token newtok1 = factory.createToken();
				Token newtok2 = factory.createToken();
				net.getInitialMarking().getTokens(newp).add(newtok1);
				net.getInitialMarking().getTokens(newp).add(newtok2);
				
				newtok1.getNames().add(p.getProcessModel().getName()+"#1");
				newtok1.getNames().addAll(tok.getNames());
				newtok2.getNames().add(p.getProcessModel().getName()+"#2");
				newtok2.getNames().addAll(tok.getNames());
			}
		}
		
		for (Iterator<Transition> it=model.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			Transition newt = factory.createTransition();
			map.put(t, newt);
			net.getTransitions().add(newt);
			
			newt.setLabel(t.getLabel());
		}
	}

	/**
	 * introduces a second conversation into the model
	 * process model variables = names of process models (=> requirement: names must be properly set)
	 * process instance identifiers = process model variables + "#1" / "#2"
	 * 
	 * @return
	 */
	public NuNet getDuplicatedModel() {
		NuNet net = factory.createNuNet();
		Map map = new HashMap();
		
		duplicateTokens(net, map);
		
		for (Iterator<FlowRelationship> it=model.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			FlowRelationship newrel = factory.createFlowRelationship();
			net.getFlowRelationships().add(newrel);
			
			newrel.setSource((Node)map.get(rel.getSource()));
			newrel.setTarget((Node)map.get(rel.getTarget()));
			
			if (rel.getSource().getProcessModel() != null && rel.getTarget().getProcessModel() != null)
				newrel.getVariables().add(rel.getSource().getProcessModel().getName());
			newrel.getVariables().addAll(rel.getVariables());
		}
		
		return net;
	}

	public NuNet getRestrictedDuplicatedModel() {
		NuNet net = factory.createNuNet();
		Map map = new HashMap();
		
		duplicateTokens(net, map);
		
		int numplaces = model.getPlaces().size();
		boolean[][] knows = calculateKnowsRelation();
		String[] pmnames = getProcessModelNames();
		
		for (Iterator<FlowRelationship> it=model.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			FlowRelationship newrel = factory.createFlowRelationship();
			net.getFlowRelationships().add(newrel);
			
			newrel.setSource((Node)map.get(rel.getSource()));
			newrel.setTarget((Node)map.get(rel.getTarget()));
			
			int index;
			if (rel.getSource() instanceof Place)
				index = model.getPlaces().indexOf(rel.getSource());
			else
				index = numplaces + model.getTransitions().indexOf(rel.getSource());
			
			for (int i = 0; i < pmnames.length; i++) {
				if (knows[index][i])
					newrel.getVariables().add(pmnames[i]);
			}
			newrel.getVariables().addAll(rel.getVariables());
		}
		
		return net;
	}

	private boolean[][] calculateKnowsRelation() {
		boolean[][] flowstar = calculateTransitiveClosure();
		boolean[][] knows = new boolean[flowstar.length][model.getProcessModels().size()];

		int i = 0;
		for (Iterator<Place> it=model.getPlaces().iterator(); it.hasNext(); i++) {
			Place p = it.next();
			if (p.getProcessModel() != null) {
				int index = model.getProcessModels().indexOf(p.getProcessModel());
				for (int j=0; j<flowstar.length; j++)
					if (flowstar[i][j])
						knows[j][index] = true;
			}
		}
		
		return knows;
	}

	private boolean[][] calculateTransitiveClosure() {
		int numplaces = model.getPlaces().size();
		int numtransitions = model.getTransitions().size();
		boolean[][] flowstar = new boolean[numplaces+numtransitions][numplaces+numtransitions];

		// init
		Map<Node,Integer> indexmap = new HashMap(model.getFlowRelationships().size());
		int i = 0;
		for (Iterator<Place> it=model.getPlaces().iterator(); it.hasNext(); i++)
			indexmap.put(it.next(), new Integer(i));
		for (Iterator<Transition> it=model.getTransitions().iterator(); it.hasNext(); i++)
			indexmap.put(it.next(), new Integer(i));
		
		for (Iterator<FlowRelationship> it=model.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			flowstar[indexmap.get(rel.getSource())][indexmap.get(rel.getTarget())] = true;
		}
		for (int j = 0; j < flowstar.length; j++)
			flowstar[j][j] = true;
		
		// compute closure
		calculateTransitiveClosure(flowstar);
		
		return flowstar;
	}

    private void calculateTransitiveClosure(boolean[][] matrix) {
        for (int i=0; i<matrix.length; i++)
            for (int j=0; j<matrix.length; j++)
                if (matrix[i][j])
                    for (int k=0; k<matrix.length; k++)
                        if (matrix[j][k] && !matrix[i][k]) {
                            matrix[i][k] = true;
                            if (k < j)
                                j = k;
                        }
    }

	private String[] getProcessModelNames() {
		String[] pmnames = new String[model.getProcessModels().size()];
		int i = 0;
		for (Iterator<ProcessModel> it=model.getProcessModels().iterator(); it.hasNext(); i++)
			pmnames[i] = it.next().getName();
		return pmnames;
	}

}
